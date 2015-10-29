package receptivity;

import localData.ReceptivityTable;
import remoteData.dataObjects.GameSession;

import java.sql.Connection;
import java.sql.Timestamp;

/************************************************************
 *
 *          Handle and calculate significance
 *
 *          //TODO: Add a method for registering a payment. It should be significant
 *          //TODO: Add more significance for longer sessions
 *
 */

public class SignificanceManager {

    private ReceptivityTable receptivityTable = new ReceptivityTable();
    private Connection connection;

    SignificanceManager(Connection connection){

        this.connection = connection;
    }

    /****************************************************************''
     *
     *          Main method to register a session
     *
     *          It handles creating new profiles for new players and registers the session for the player
     *
     *
     * @param session      - the game session from the database
     */


    public void updateReceptivity(GameSession session) {

        ReceptivityProfile profile = getProfileForPlayer(session.facebookId);
        profile.registerSession(session);
        receptivityTable.update(profile, connection);

    }


    private ReceptivityProfile getProfileForPlayer(String facebookId) {

        ReceptivityTable table = new ReceptivityTable("and facebookId = '"  + facebookId + "'", 1);
        table.load(connection);
        ReceptivityProfile profile = table.getNext();

        if(profile == null){

            profile = new ReceptivityProfile(facebookId);
            table.store(profile, connection);
        }

        return profile;

    }

    public void display() {

        System.out.println("**************************************\nDone!");


    }

    public Timestamp getLastUpdate(Connection connection) {

        ReceptivityTable table = new ReceptivityTable();
        return table.getLast( connection );
    }
}
