package balanceAnalysis;

import dbManager.ConnectionHandler;
import dbManager.DatabaseException;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.GameSessionTable;
import remoteData.dataObjects.User;
import remoteData.dataObjects.UserTable;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/*********************************************************************
 *
 * Get the balance for all players on a given day
 */

public class BalanceAnalysis {

    private static Timestamp[] dates = {

            Timestamp.valueOf("2015-06-01 00:00:00"),
            Timestamp.valueOf("2015-07-01 00:00:00"),
            Timestamp.valueOf("2015-08-01 00:00:00"),
            Timestamp.valueOf("2015-09-01 00:00:00"),
            Timestamp.valueOf("2015-10-01 00:00:00"),
            Timestamp.valueOf("2015-11-01 00:00:00"),
            Timestamp.valueOf("2015-12-01 00:00:00"),
            Timestamp.valueOf("2016-01-01 00:00:00"),



    };

    public static void main(String[] arg){

        System.out.println("Getting the end balance for paying players");
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

            balances[d] = analyser.analyse(dates[d], payingUsers, localConnection);

        }

        System.out.println("Resulting balances:");


        for (int d = 0; d < dates.length; d++) {

            System.out.println(dates[d] + "\t" + balances[d]);

        }

    }

    private long analyse(Timestamp date, List<User> payingUsers, Connection localConnection) {


        GameSessionTable sessionTable = new GameSessionTable();
        try {
            sessionTable.load(localConnection, "and date(timestamp) = '" + date.toString().substring(0, 10) + "'");
        } catch (DatabaseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        List<ActiveUser> activeUsers = new ArrayList<>(2000);

        GameSession session = sessionTable.getNext();

        int sessionCount = 0;

        while(session != null){

            System.out.println(" -- Session:" + session.toString());

            handleSession(session, activeUsers, payingUsers);
            sessionCount++;
            session = sessionTable.getNext();
        }


        System.out.println("Handled " + sessionCount + " sessions");
        System.out.println("Stored " + activeUsers.size() + " active users with last session");

        System.out.println(" -- calculating coin balance");

        long balance = getCoinBalance(activeUsers);

        System.out.println("Total Coin balance per end of " + date.toString().substring(0, 10) + " is " + balance);

        return balance;
    }

    private static long getCoinBalance(List<ActiveUser> activeUsers) {
        long total = 0;

        for (ActiveUser activeUser : activeUsers) {

            total += activeUser.getLast().endBalance;
        }


        return total;
    }

    private static void handleSession(GameSession session, List<ActiveUser> activeUsers, List<User> payingUsers) {

        if(!isPaying(session.facebookId, payingUsers)){
            return;
        }

        if(isIgnoreList(session.facebookId)){

            return;

        }


        ActiveUser activeUser = findActiveUser(session.facebookId, activeUsers);

        if(activeUser == null){

            activeUsers.add(new ActiveUser(session.facebookId, session));
        }
        else{

            activeUser.setLast(session);
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
