package remoteData.dataObjects;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/******
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-04-21
 * Time: 16:55
 * To change this template use File | Settings | File Templates.
 */
public class GameSessionTable extends GenericTable{

    private static final String tableName = "game_session";
    private static final String orderBy = "timestamp";

    private static final String getSQL =
                "select * " +
                "        from game_session \n"+
                "        where 1 = 1\n"+
                "        -RESTRICTION- order by "+ orderBy+" -ORDER- -LIMIT-;";


    private static final String getRemoteSQL =
                "select timestamp, sessions.sessionId, game_stats.game, facebookId, name, sessions.promoCode, fbSource, game_stats.firstActionTime as 'action time', game_stats.totalWager, game_stats.totalWin, game_stats.lastBalance as 'end balance', game_stats.actions as spins, session_stats.actions as 'total spins', sessions.clientType \n"+
                "        from sessions, users, session_stats, game_stats \n"+
                "        where users.facebookId = sessions.playerId and sessions.sessionId = session_stats.sessionId and sessions.sessionId = game_stats.sessionId \n"+
                "        -RESTRICTION-  order by timestamp -LIMIT-;";


    public GameSessionTable(String restriction, int limit){

        super(getSQL, restriction, limit);
        maxLimit = limit;
    }

    public GameSessionTable(){

        this("", -1);
    }


                       /*
                       timeStamp datetime
                       sessionId varchar(45)
                       game varchar(45)
                       facebookId varchar(45)
                       name varchar(45)
                       promoCode varchar(80)
                       fbSource varchar(45)
                       actionTime datetime
                       totalWager int(11)
                       totalWin int(11)
                       endBalance int(11)
                       spins int(11)
                       totalSpins int(11)
                        */

    public GameSession getNext(){

        try {
            if(!resultSet.next())
                return null;

            return new GameSession(resultSet.getTimestamp(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),resultSet.getString(6), resultSet.getString(7),
                    resultSet.getTimestamp(8),
                    resultSet.getInt(9),resultSet.getInt(10),resultSet.getInt(11),resultSet.getInt(12),resultSet.getInt(13), resultSet.getString(14));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<GameSession> getAll(){

        List<GameSession> sessions = new ArrayList<GameSession>();
        GameSession session = getNext();

        while(session != null){

            sessions.add(session);
            session = getNext();
        }

        return sessions;

    }




    public List<GameSession> getSessionsForUser(User user, Connection connection) {

        load(connection, "and users.facebookId = '"+ user.facebookId+"'");
        List<GameSession> sessionsForUser = getAll();
        System.out.println("Found " + sessionsForUser.size() + " sessions for user " + user.name);
        return sessionsForUser;

    }


    public Timestamp getLast(Connection connection){

        String sql = "select timeStamp from game_session order by timestamp desc limit 1;";

        try{

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            if(!rs.next())
                return null;  // There are no entries in the database

            return rs.getTimestamp(1);

        }catch(SQLException e){

            System.out.println("Error accessing data in database");
            e.printStackTrace();
        }


        return null;
    }

    public void loadRemote(Timestamp from, int records, Connection connection){

        String sql = getRemoteSQL(from, records);
        System.out.println(" -- Retrieving remote data with " + sql );

        loadFromDB(connection, sql);
    }

    public String getRemoteSQL(Timestamp fromTime, int maxRecords) {

        String queryString = getQueryString(getRemoteSQL, "and timeStamp > '"+fromTime.toString()+"'", maxRecords, -1, order);
        //System.out.println("Query: " + queryString);

        return queryString;
    }




}
