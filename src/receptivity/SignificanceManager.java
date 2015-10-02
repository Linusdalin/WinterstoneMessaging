package receptivity;

import remoteData.dataObjects.GameSession;

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

    private List<ReceptivityProfile> profiles = new ArrayList<ReceptivityProfile>(4000);


    SignificanceManager(){

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

    }


    private ReceptivityProfile getProfileForPlayer(String facebookId) {

        for (ReceptivityProfile profile : profiles) {

            if(profile.getUserId().equals(facebookId))
                return profile;
        }

        // User did not exist

        ReceptivityProfile newProfile = new ReceptivityProfile(facebookId);
        return newProfile;

    }

    public void display() {

        System.out.println("**************************************\nSignificance: ");

        for (ReceptivityProfile profile : profiles) {

            System.out.println(profile.toString());

        }

    }
}
