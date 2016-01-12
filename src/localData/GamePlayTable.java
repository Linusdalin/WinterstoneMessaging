package localData;

import dbManager.DatabaseException;
import remoteData.dataObjects.GenericTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-04-16
 * Time: 13:17
 * To change this template use File | Settings | File Templates.
 */
public class GamePlayTable extends GenericTable {

    private static final String getRemote =
            "select *" +
            "     from game_play where 1=1" +
            "      -RESTRICTION- -LIMIT-";
    private Connection connection;


    public GamePlayTable(String restriction, int limit){

        super(getRemote, restriction, limit);
        maxLimit = limit;
    }

    public GamePlayTable(Connection connection){

        this( "", -1);
        this.connection = connection;
    }


    public GamePlay getNext(){

        try {
            if(!resultSet.next())
                return null;

            return new GamePlay(resultSet.getString(1), resultSet.getString(2),resultSet.getInt(3),resultSet.getTimestamp(4));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<GamePlay> getAll(){

        List<GamePlay> sessions = new ArrayList<GamePlay>();
        GamePlay session = getNext();

        while(session != null){

            sessions.add(session);
            session = getNext();
        }

        return sessions;

    }


    /***********************************************************************************
     *
     *
     * @param facebookId
     * @param game
     * @return
     *
     *
     */


    public GamePlay getGamesForUser(String facebookId, String game) throws DatabaseException {

        load(connection, "and playerId= '" + facebookId + "' and game='" + game + "'");
        GamePlay gamePlay = getNext();
        close();
        return gamePlay;

    }


    public void updateGamePlay(GamePlay gamePlay, Timestamp timeStamp, Connection connection) {


        String update = "update game_play set occurrences = occurrences + 1, lastPlay = '"+ timeStamp+"' where playerId = '" + gamePlay.facebookId + "' and game = '" + gamePlay.game + "'";

        //System.out.println("Update game_play with: " + update);

        try{

            Statement statement = connection.createStatement();
            statement.execute(update);

        }catch(SQLException e){

            System.out.println("Error accessing data in database. SQL:" + update);
            e.printStackTrace();
        }


    }

}
