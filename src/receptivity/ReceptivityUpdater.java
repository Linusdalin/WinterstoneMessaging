package receptivity;

import dbManager.ConnectionHandler;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.GameSessionTable;
import java.sql.Connection;
import java.sql.Timestamp;

/*************************************************************************
 *
 *              Initial significance scanner
 * .
 */
public class ReceptivityUpdater {


    private static final String startDate = "0000-00-00";
    private static final int    sessionCap   = 300000;


    public static void main(String[] arg){

        System.out.println(" *******************************************************\n * Updating receptivity database");

        Connection connection    = ConnectionHandler.getConnection(ConnectionHandler.Location.local);

        SignificanceManager significanceManager = new SignificanceManager(connection);

        Timestamp last = significanceManager.getLastUpdate(connection);
        String startFrom = startDate;
        if(last != null){

            startFrom = last.toString();
        }

        System.out.println("Starting from " + startFrom);

        GameSessionTable gameSessions = new GameSessionTable();
        gameSessions.load(connection, "and timeStamp > '" + startFrom + "'", "ASC", sessionCap);
        GameSession session = gameSessions.getNext();

        while(session != null){

            significanceManager.updateReceptivity(session);
            session = gameSessions.getNext();
        }


        significanceManager.display();

    }


}
