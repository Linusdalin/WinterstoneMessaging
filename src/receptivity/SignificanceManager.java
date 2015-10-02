package receptivity;

import localData.ReceptivityTable;
import remoteData.dataObjects.GameSession;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

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
        receptivityTable.store(profile, connection);

    }


    private ReceptivityProfile getProfileForPlayer(String facebookId) {

        return null;
    }

    public void display() {

        System.out.println("**************************************\nSignificance: ");


    }

}
