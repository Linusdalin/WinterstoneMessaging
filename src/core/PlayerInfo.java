package core;

import localData.CachedUser;
import localData.GamePlay;
import receptivity.ReceptivityProfile;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.Payment;
import remoteData.dataObjects.User;
import rewards.Reward;
import transfer.PaymentAnalyser;

import java.sql.Connection;
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
    private List<GameSession> userSessions = null;
    private Timestamp lastSession = null;
    private Timestamp lastMobile = null;


    private int paymentBehavior = PaymentAnalyser.UNDEFINED_BEHAVIOUR;

    private ReceptivityProfile receptivityProfile = null;
    private CachedUser cachedUser;
    private int levelUp = 0;
    private Connection connection;

    private String claimedRewards = null;

    public PlayerInfo(User user, DataCache dbCache, Connection connection){

        this.user = user;
        this.dbCache = dbCache;

        cachedUser = dbCache.getCachedUser(this.user, connection);

        if(cachedUser != null)
            checkLevelUp(user, cachedUser);

        if(receptivityProfile == null)
            receptivityProfile = dbCache.getReceptivityProfileForPlayer(user.id);

        this.connection = connection;

    }

    /********************************************************************************************'
     *
     *          Comparing the data from the database with the cached data about the user to see if the
     *          user has levelled up and if so how much
     *
     *          Storing the number of levels for later access in level-based campaigns
     *
     *
     * @param user             - current data from external db
     * @param cachedUser       - cached data from last time
     */


    private void checkLevelUp(User user, CachedUser cachedUser) {

        if(user.level > cachedUser.level && cachedUser.level != -1)
            this.levelUp = user.level - cachedUser.level;

        if(user.level > cachedUser.level){

            dbCache.updateLevel(user.id, user.level);

        }

    }


    public List<GameSession> getSessionsYesterday(Timestamp analysisDate, int days) {

        List<GameSession> sessionsYesterday = dbCache.getSessionsYesterday(user, analysisDate, days);

        System.out.println("     (Got " + sessionsYesterday.size() + "sessions yesterday)");
        return sessionsYesterday;
    }

    public List<GameSession> getSessions() {

        if(userSessions == null)
            userSessions = dbCache.getSessionsForUser(user);

        System.out.println("     (Got " + userSessions.size() + "sessions for user)");
        return userSessions;
    }


    public List<Payment> getPaymentsForUser() {

        if(userPayments == null){

           userPayments = dbCache.getPaymentsForUser(user);
        }

        return userPayments;
    }

    public Timestamp getLastSession(){

        if(lastSession != null)
            return lastSession;

        if(user.sessions == 0)
            return null;

        lastSession = user.lastActivity;

        if(lastSession == null){

            System.out.println(" -- Found no sessions for user " + user.name);
            return null;
        }

        System.out.println(" -- Got last session @" + lastSession.toString() + " for user " + user.name);
        return lastSession;

    }


    public Timestamp getLastSessionLocalCache() {

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

    public Timestamp getLastMobileSession() {

        if(lastMobile != null)
            return lastMobile;

        if(user.sessions == 0)
            return null;

        lastMobile = dbCache.getLastMobileSessionForUser(user);

        if(lastMobile == null){

            System.out.println(" -- Found no sessions for user " + user.name);
            return null;
        }

        System.out.println(" -- Got last session @" + lastMobile.toString() + " for user " + user.name);
        return lastMobile;


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
            receptivityProfile = dbCache.getReceptivityProfileForPlayer(user.id);

        if(receptivityProfile == null)
            receptivityProfile = new ReceptivityProfile(user.id);

        return receptivityProfile;


    }

    /********************************************************************************
     *
     *              get the profile for usage across mobile and desktop
     *
     * @return     - profile classification
     *
     *          //TODO: Missing time aspect for switching
     *
     */

    public UsageProfileClassification getUsageProfile(){

        if(user.id.startsWith("ap_") || user.id.startsWith("go_"))
            return UsageProfileClassification.ANONYMOUS;


        if(cachedUser == null)
            return UsageProfileClassification.UNKNOWN;

        if(cachedUser.desktopSessions > 0 && cachedUser.iosSessions == 0)
            return UsageProfileClassification.CANVAS;

        if(cachedUser.iosSessions > 0){

            if(cachedUser.iosSessions > cachedUser.desktopSessions )
                return UsageProfileClassification.CONVERTED;

            if(cachedUser.iosSessions > 50 && cachedUser.desktopSessions > 50 )
                return UsageProfileClassification.HALF_HALF;

            return UsageProfileClassification.MOBILE_TRY;
        }

        return UsageProfileClassification.UNKNOWN;

    }

    public CachedUser getCachedUserData(){

        return cachedUser;
    }

    public Timestamp getFirstMobileSession(){

        if(cachedUser == null)
            return null;

        return cachedUser.firstMobileSession;

    }

    public GamePlay getGamePlay(String game) {

        return dbCache.getGamePlay(getUser().id, game);
    }


    public int getLevelUp(){

        return levelUp;
    }

    public boolean hasClaimed(Reward reward) {

        if(claimedRewards == null)
            claimedRewards = dbCache.getClaimedRewards(getUser().id);

        if(claimedRewards == null)
            return false;

        return(claimedRewards.indexOf(reward.getCode()) >= 0);

    }

    public boolean fallbackFromMobile() {

        if(cachedUser == null)
            return false;

        return cachedUser.fallbackFromMobile();

    }

    public Connection getConnection() {
        return connection;
    }



    public int getPaymentBehavior(){

        if(paymentBehavior == PaymentAnalyser.UNKNOWN_BEHAVIOUR){

            PaymentAnalyser analyzer = new PaymentAnalyser();
            paymentBehavior = analyzer.getBehaviorForPlayer(getUser().id);

        }

        return paymentBehavior;

    }

}
