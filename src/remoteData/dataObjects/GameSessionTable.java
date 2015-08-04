package remoteData.dataObjects;

import java.sql.*;

/******
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-04-21
 * Time: 16:55
 * To change this template use File | Settings | File Templates.
 */
public class GameSessionTable extends GenericTable{

    private static final String tableName = "game_session";
    private static final String orderByRemote = "timestamp";
    private static final String orderByLocally = "timeStamp";

    private static final String getRemote =
                "select timestamp, sessions.sessionId, game_stats.game, facebookId, name, sessions.promoCode, fbSource, game_stats.firstActionTime as 'action time', game_stats.totalWager, game_stats.totalWin, game_stats.lastBalance as 'end balance', game_stats.actions as spins, session_stats.actions as 'total spins' \n"+
                "        from sessions, users, session_stats, game_stats \n"+
                "        where users.facebookId = sessions.playerId and sessions.sessionId = session_stats.sessionId and sessions.sessionId = game_stats.sessionId \n"+
                "        and timestamp > \"$(THRESHOLD)\"  order by "+ orderByRemote+" limit $(LIMIT);";

    private static final String getLocal = "select * from "+ tableName+" where timeStamp > '$(START)' $(RESTRICTION) order by " + orderByLocally;


    public GameSessionTable(String startTime, String restriction, int limit){

        super(getRemote, startTime, restriction, limit);
        maxLimit = limit;
    }

    public GameSessionTable(){

        this("", "", -1);
    }




    public GameSession getNext(){

        try {
            if(!resultSet.next())
                return null;

            return new GameSession(resultSet.getTimestamp(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),resultSet.getString(6),resultSet.getString(7),
                    resultSet.getTimestamp(8),
                    resultSet.getInt(9),resultSet.getInt(10),resultSet.getInt(11),resultSet.getInt(12),resultSet.getInt(13));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void storeLocally(Connection connection, GameSession gameSession){

        String insert = "insert into "+ tableName+" values (" + gameSession.toSQLValues() + ")";

        //System.out.println("Insert locally with: " + insert);

        try{

            Statement statement = connection.createStatement();
            statement.execute(insert);

        }catch(SQLException e){

            System.out.println("Error accessing data in database");
            e.printStackTrace();
        }

    }

    public Timestamp getLast(Connection connection){

        String sql = "select timeStamp from game_session order by timeStamp desc limit 1;";

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


}
