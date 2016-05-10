package balanceAnalysis;

import dbManager.ConnectionHandler;
import dbManager.DatabaseException;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.User;
import remoteData.dataObjects.UserTable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*********************************************************************
 *
 * Get the balance for all players on a given day
 */

public class BalanceAnalysis {

    private static int totalOutcome;

    private static Timestamp[] dates = {

            Timestamp.valueOf("2016-03-18 00:00:00"),



    };

    public static void main(String[] arg){

        System.out.println("Getting the start and end balance for paying players");
        Connection localConnection = null;
        Connection remoteConnection = null;
        localConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);

        if(localConnection == null){

            System.out.println("failed local Connection");
            return;
        }

        remoteConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.remote);

        if(remoteConnection == null){

            System.out.println("failed remote Connection");
            return;
        }

        System.out.println("  -- loading all paying users....");
        List<User> payingUsers = getPayingPlayers(remoteConnection);
        System.out.println("  -- Found " + payingUsers.size() + " paying users.");


        BalanceAnalysis analyser = new BalanceAnalysis();

        long[] balances = new long[dates.length];

        for (int d = 0; d < dates.length; d++) {

            balances[d] = analyser.analyse(dates[d], payingUsers, localConnection, remoteConnection);

        }

        System.out.println("Resulting balances:");


        for (int d = 0; d < dates.length; d++) {

            System.out.println(dates[d] + "\t" + balances[d]);

        }

    }

    private long analyse(Timestamp date, List<User> payingUsers, Connection localConnection, Connection remoteConnection) {

        System.out.println("Getting all game_stats from the remote database");
        List<ActiveUser> activeUsers = new ArrayList<>(2000);

        String sql = "select playerId, firstActionTime, lastActionTime, totalWager, totalWin, lastBalance " +
                            "from game_stats, sessions " +
                            "where sessions.sessionId = game_stats.sessionId and (date(firstActionTime) = '" + date.toString().substring(0, 10) + "' or date(lastActionTime) = '" + date.toString().substring(0, 10) + "')";
        ResultSet resultSet;
        int sessionCount = 0;

        try{
            System.out.println("Looking for sessions with: " + sql);
            Statement statement = remoteConnection.createStatement();
            resultSet = statement.executeQuery(sql);

            while(resultSet.next()){

                int totalWager = resultSet.getInt("totalWager");
                int totalWin = resultSet.getInt("totalWin");
                int endBalance = resultSet.getInt("lastBalance");

                Game gameSession = new Game(
                        resultSet.getString("playerId"),
                        resultSet.getTimestamp("firstActionTime"),
                        resultSet.getTimestamp("lastActionTime"),
                        endBalance,
                        endBalance - (totalWin - totalWager),
                        totalWin - totalWager);

                handleSession(gameSession, activeUsers, payingUsers);
                sessionCount++;
            }

        }catch(SQLException e){
            e.printStackTrace();
            return 0;
        }



        System.out.println("Handled " + sessionCount + " sessions");
        System.out.println("Stored " + activeUsers.size() + " active users with last session");


        System.out.println(" -- calculating coin balance");

        long totalBefore = 0;
        long totalAfter = 0;

        for (ActiveUser activeUser : activeUsers) {

            System.out.println("");

            long before = activeUser.getLast().startBalance;
            long after = activeUser.getLast().endBalance;

            System.out.println(" User: " + activeUser.getFacebookId() + " before: " + before + " after: " + after);

            totalAfter += after;
            totalBefore += before;
        }


        System.out.println("Total Coin balance per " + date.toString().substring(0, 10) + " is " + totalBefore + " -> " + totalAfter + "(" + (totalAfter - totalBefore) + ") Outcome: " + totalOutcome );

        return totalAfter;
    }

    private long calculateBalanceBefore(GameSession first) {

        if(first == null)
            return 0;

        return first.endBalance - first.totalWager + first.totalWin;
    }


    private static void handleSession(Game session, List<ActiveUser> activeUsers, List<User> payingUsers) {

        if(!isPaying(session.playerId, payingUsers)){
            return;
        }

        if(isIgnoreList(session.playerId)){

            return;

        }


        ActiveUser activeUser = findActiveUser(session.playerId, activeUsers);

        if(activeUser == null){

            activeUsers.add(new ActiveUser(session.playerId, session));
        }
        else{

            activeUser.setFirst(session);
            activeUser.setLast(session);
            totalOutcome += session.outcome;
        }

    }

    private static boolean isIgnoreList(String facebookId) {
        return facebookId.equals("10153227232909035") || facebookId.equals("627716024");
    }

    private static ActiveUser findActiveUser(String facebookId, List<ActiveUser> activeUsers) {

        System.out.print("     -- Looking for player " + facebookId + "...");

        for (ActiveUser existingUser : activeUsers) {

            if(existingUser.getFacebookId().equals(facebookId)){

                System.out.println("Found!");
                return existingUser;
            }
        }

        System.out.println("New!");
        return null;

    }

    private static boolean isPaying(String facebookId, List<User> payingUsers) {

        System.out.print("     -- Is Paying player " + facebookId + "...");

        for (User user : payingUsers) {

            if(user.id.equals(facebookId)){

                System.out.println("Yes!");
                return true;
            }
        }

        System.out.println("No");
        return false;

    }


    private static List<User> getPayingPlayers(Connection remoteConnection) {

        UserTable table = new UserTable();
        try {
            table.load(remoteConnection, "and numberOfPayments > 0");
        } catch (DatabaseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        List<User> payingUsers = table.getAll();

        return payingUsers;

    }


}
