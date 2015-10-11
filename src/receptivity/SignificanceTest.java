package receptivity;

import dbManager.ConnectionHandler;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.GameSessionTable;
import java.sql.Connection;

/*************************************************************************
 *
 *              Initial significance scanner
 * .
 */
public class SignificanceTest {


    private static final String startDate = "0000-00-00";
    private static final int    sessionCap   = 1000000;


    public static void main(String[] arg){

        System.out.println("Testing significance");

        Connection connection    = ConnectionHandler.getConnection(ConnectionHandler.Location.local);

        SignificanceManager significanceManager = new SignificanceManager(connection);

        GameSessionTable gameSessions = new GameSessionTable();
        gameSessions.load(connection, "and timeStamp > '" + startDate + "'", "ASC", sessionCap);
        GameSession session = gameSessions.getNext();

        while(session != null){

            significanceManager.updateReceptivity(session);
            session = gameSessions.getNext();
        }


        significanceManager.display();

    }


}
