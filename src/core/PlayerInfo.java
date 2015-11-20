package core;

import receptivity.ReceptivityProfile;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.Payment;
import remoteData.dataObjects.User;

import java.sql.Timestamp;
import java.util.List;

/**********************************************************************
 *
 *          Primitives based on the data for the user
 *
 *          This uses lazy loading of sessions and payments and is based
 *          on the session and payment data in the player object.
 *
 */


public class PlayerInfo {

    private final User user;
    private final DataCache dbCache;

    private List<Payment> userPayments;
    private Timestamp lastSession = null;

    private ReceptivityProfile receptivityProfile = null;

    public PlayerInfo(User user, DataCache dbCache){

        this.user = user;
        this.dbCache = dbCache;

    }


    public List<GameSession> getSessionsYesterday(Timestamp analysisDate) {

        List<GameSession> sessionsYesterday = dbCache.getSessionsYesterday(user, analysisDate);

        System.out.println("     (Got " + sessionsYesterday.size() + "sessions yesterday)");
        return sessionsYesterday;
    }



    public List<Payment> getPaymentsForUser() {

        if(userPayments == null){

           userPayments = dbCache.getPaymentsForUser(user);
        }

        return userPayments;
    }



    public Timestamp getLastSession() {

        if(lastSession != null)
            return lastSession;

        if(user.sessions == 0)
            return null;

        lastSession = dbCache.getLastSessionForUser(user);

        if(lastSession == null){

            System.out.println(" -- Found no sessions for user " + user.name);
            return null;
        }

        System.out.println(" -- Got last session @" + lastSession.toString() + " for user " + user.name);
        return lastSession;


    }

    public User getUser() {

        return user;
    }

    public Payment getFirstPayment() {

        getPaymentsForUser();
        if(userPayments.size() == 0)
            return null;
        return userPayments.get( 0 );

    }


    public Payment getLastPayment() {

        getPaymentsForUser();
        if(userPayments.size() == 0)
            return null;
        return userPayments.get(userPayments.size() - 1);

    }

    public ReceptivityProfile getReceptivityForPlayer() {

        if(receptivityProfile == null)
            receptivityProfile = dbCache.getReceptivityProfileForPlayer(user.facebookId);

        if(receptivityProfile == null)
            receptivityProfile = new ReceptivityProfile(user.facebookId);

        return receptivityProfile;


    }

    /********************************************************************************
     *
     *              get the profile for usage across mobile and desktop
     *
     * @return     - profile classification
     *
     *          //TODO: Not implemented classification of profile
     *
     */

    public UsageProfileClassification getUsageProfile(){

        return UsageProfileClassification.UNKNOWN;

    }

}
