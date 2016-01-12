package transfer;

import core.DataCache;
import dbManager.ConnectionHandler;
import dbManager.DatabaseException;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.GameSessionTable;

import java.sql.Connection;
import java.sql.SQLException;

/**************************************************************************
 *
 *          This is a temporary data caching to be run once to backdate the cache information
 *
 */

public class UpdateGamePlay {

    private static final int MAX_RECORDS = 1000000;     // Max records at a time
    private static final String startTime = "2015-11-30 01:09:50.0";

    Connection localConnection = null;

    public static void main(String[] args){

        try {

            UpdateGamePlay transfer = new UpdateGamePlay();
            transfer.executeUpdate();

        } catch (DatabaseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


    public void executeUpdate() throws DatabaseException {


        GameSessionTable gameSessions = new GameSessionTable();
        gameSessions.load(localConnection, "and timeStamp > '" + startTime + "'", "ASC", MAX_RECORDS);

        GameSession session = gameSessions.getNext();

        while(session != null){


            DataCache.updateGamePlay(session, localConnection);
            session = gameSessions.getNext();
        }

    }




    private void close() {


        if(localConnection != null){

            try {
                localConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    public UpdateGamePlay(){

        localConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);

         if(localConnection == null){

             System.out.println("failed local Connection");
             return;
         }

    }



}
