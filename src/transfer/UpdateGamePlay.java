package transfer;

import core.DataCache;
import dbManager.ConnectionHandler;
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

    private static final int MAX_RECORDS = 1500000;     // Max records at a time
    private static final String startTime = "2015-10-23 22:17:39.0";

    Connection localConnection = null;

    public static void main(String[] args){

        UpdateGamePlay transfer = new UpdateGamePlay();
        transfer.executeUpdate();

    }


    public void executeUpdate(){


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
