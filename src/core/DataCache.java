package core;

import localData.*;
import receptivity.ReceptivityProfile;
import remoteData.dataObjects.*;
import response.ResponseHandler;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/***********************************************************************
 *
 *              Precached data that then will be used to filter data per user
 *
 */

public class DataCache {

    private List<GameSession> allSessions = new ArrayList<GameSession>();
    private static List<Payment> allPayments;
    private Connection connection;
    private CachedUserTable cachedUserTable = new CachedUserTable();

    /********************************************************************************
     *
     *
     * @param connection
     * @param startDate
     * @param limit                        - how many users
     */


    public DataCache(Connection connection, String startDate, int limit){

        System.out.println(" **********************************************************\n*  Loading cached data...");

        this.connection = connection;

        //GameSessionTable sessionTable = new GameSessionTable();
        //sessionTable.load(connection,"and timestamp > '"+ startDate+"'", limit );
        //allSessions = sessionTable.getAll();

        //System.out.println(" -- Got " + allSessions.size() + " sessions after " + startDate);

        if(allPayments == null){


            PaymentTable paymentTable = new PaymentTable();
            paymentTable.load(this.connection,"and timestamp > '"+ startDate+"'", "ASC", limit );
            allPayments = paymentTable.getAll();

        }


        System.out.println(" -- Caching last Sessions for users");
        cacheLastSessions();

    }


    public List<GameSession> getSessionsForUser(User user) {

        GameSessionTable sessionTable = new GameSessionTable();
        sessionTable.load(connection,"and facebookId='" + user.facebookId + "'","ASC", -1 );

        List<GameSession> sessionsForUser = sessionTable.getAll();

        System.out.println(" -- Got " + sessionsForUser.size() + " sessions for user " + user.name);

        return sessionsForUser;

    }

    public Timestamp getLastSessionForUser(User user) {

        CachedUserTable userTable = new CachedUserTable();
        userTable.load(connection,"and facebookId='" + user.facebookId + "'","ASC", 1 );

        CachedUser last = userTable.getNext();
        if(last == null)
            return null;

        return last.lastSession;

    }

    public CachedUser getCachedUser(User user) {

        CachedUserTable userTable = new CachedUserTable();
        userTable.load(connection,"and facebookId='" + user.facebookId + "'","ASC", 1 );

        return userTable.getNext();

    }


    public List<Payment> getPaymentsForUser(User user) {

        if(user.payments == 0)
            return new ArrayList<Payment>();

        PaymentTable paymentTable = new PaymentTable();
        paymentTable.load(connection,"and playerId='" + user.facebookId + "'","ASC", -1 );

        List<Payment> paymentsForUser = paymentTable.getAll();

        System.out.println(" -- Got " + paymentsForUser.size() + " payments for user " + user.name);

        return paymentsForUser;

    }

    /**********************************************************************************
     *
     *          This is to update the last sessions for players by going through any new sessions.
     *
     *
     *          Also look for mobile sessions to store if the player is playing mobile
     *
     */


    private void cacheLastSessions() {

        Timestamp lastForAnyone = cachedUserTable.getLastSession(connection);

        if(lastForAnyone == null)
            System.out.println(" No last session found");
        else
            System.out.println(" Last session in db: " + lastForAnyone.toString());


        GameSessionTable gameSessions = new GameSessionTable();

        if(lastForAnyone == null)
            gameSessions.load(connection, "", "ASC", 10000);
        else
            gameSessions.load(connection, "and timeStamp > '" + lastForAnyone.toString() + "'", "ASC", 1200000);

        GameSession session = gameSessions.getNext();

        while(session != null){

            updateLastSession(session, connection);

            updateGamePlay(session, connection);

            ResponseHandler responseHandler = new ResponseHandler(session.facebookId, connection);
            responseHandler.storeResponse(session, connection);



            session = gameSessions.getNext();
        }

    }


    private void updateLastSession(GameSession session, Connection connection) {

        cachedUserTable.load(connection, "and facebookId = '" + session.facebookId + "'", "ASC", 1);
        CachedUser cachedUser = cachedUserTable.getNext();

        if(cachedUser == null){

            // Store new

            cachedUser = new CachedUser(session.facebookId, session.timeStamp, 0, 0, 0, (session.clientType.equals("facebook") ? 1 : 0), (session.clientType.equals("ios") ? 1 : 0), (session.clientType.equals("ios") ? session.timeStamp : null));
            cachedUserTable.store(cachedUser, connection);
            System.out.println("  - Creating new user " + cachedUser.facebookId + " with last session " + session.timeStamp);


        }else{

            if(cachedUser.lastSession.after(session.timeStamp))
                System.out.println("  - NOT updating older session for user " + cachedUser.facebookId);
            else{

                boolean iOsSession = (session.clientType.equals("ios"));
                cachedUser.lastSession = session.timeStamp;
                cachedUserTable.updateTime(cachedUser, iOsSession, session.timeStamp, connection);
                System.out.println("  - Updating with new session for user " + cachedUser.facebookId + "@ " + session.timeStamp);
            }
        }


    }


    public static void updateGamePlay(GameSession session, Connection connection) {

        GamePlayTable table = new GamePlayTable(connection);

        GamePlay gamePlay = table.getGamesForUser(session.facebookId, session.game);

        if(gamePlay == null){

            // Store new

            gamePlay = new GamePlay(session.facebookId, session.game, 1, session.timeStamp);
            gamePlay.store(connection);

            System.out.println("  - Noting new GamePlay ("+(gamePlay.occurrences + 1)+") by " + gamePlay.facebookId + " with game " + gamePlay.game + " @" + session.timeStamp.toString());


        }else{

            if(gamePlay.lastTime.after(session.timeStamp))
                System.out.println("  - NOT updating older game play of "+ gamePlay+" @ "+ session.timeStamp.toString() +" for user " + gamePlay.facebookId);
            else{

                table.updateGamePlay(gamePlay, session.timeStamp, connection);
                System.out.println("  - Updating game play ("+ gamePlay.game+") for user " + gamePlay.facebookId + " @ " + session.timeStamp);
            }
        }


    }



    //select * from game_session where facebookId= '10154214671319358' and date(timestamp) = date(date_sub('2015-08-25', interval 1 day)) limit 100;


    public List<GameSession> getSessionsYesterday(User user, Timestamp analysisDate) {

        GameSessionTable gameSessions = new GameSessionTable();
        gameSessions.load(connection, "and facebookId='"+ user.facebookId+"' and date(timeStamp) = date(date_sub('"+ analysisDate+"', interval 1 day))", "ASC", 100);
        return gameSessions.getAll();

    }

    public ReceptivityProfile getReceptivityProfileForPlayer(String facebookId) {

        ReceptivityTable table = new ReceptivityTable();
        return  table.getReceptivityForPlayer(facebookId, connection);

    }

    public GamePlay getGamePlay(String facebookId, String game) {

        GamePlayTable table = new GamePlayTable(connection);
        return table.getGamesForUser(facebookId, game);
    }
}
