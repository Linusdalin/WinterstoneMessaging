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

    private static final String getRemote =
                "select * " +
                "        from game_session \n"+
                "        where 1 = 1\n"+
                "        $(RESTRICTION)  order by "+ orderBy+" $(LIMIT);";



    public GameSessionTable(String restriction, int limit){

        super(getRemote, restriction, limit);
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
                    resultSet.getInt(9),resultSet.getInt(10),resultSet.getInt(11),resultSet.getInt(12));

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

        load(connection, "users.facebookId = '"+ user.facebookId+"'");
        List<GameSession> sessionsForUser = getAll();
        System.out.println("Found " + sessionsForUser.size() + " sessions for user " + user.name);
        return sessionsForUser;

    }


}
