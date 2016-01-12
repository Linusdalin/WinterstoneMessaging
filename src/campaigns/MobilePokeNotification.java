package campaigns;

import action.ActionInterface;
import action.EmailAction;
import action.GiveCoinAction;
import action.MobilePushAction;
import core.PlayerInfo;
import core.UsageProfileClassification;
import email.EmailInterface;
import email.NotificationEmail;
import remoteData.dataObjects.User;

import java.sql.Timestamp;


/************************************************************************'
 *
 *          Sending out a message to the mobile players about a new game release
 */

public class MobilePokeNotification extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "MobilePokeNotification";
    private static final int CoolDown_Days = 10;
    private int[] MessageIds = {
                                 21, 22, 23, 24,
                                 31, 32, 33, 34 };


    // Trigger specific config data
    private static final int INACTIVITY_LIMIT_FREE      = 40;   // Max days inactivity to get message
    private static final int INACTIVITY_LIMIT_PAYING    = 90;   // Max days inactivity to get message
    private static final int ACTIVITY_MIN   = 8;               // Min sessions to be active


    MobilePokeNotification(int priority, CampaignState activation){

        super(Name, priority, activation);
        setCoolDown(CoolDown_Days);
        registerMessageIds( MessageIds );
    }



    /**************************************************************************
     *
     *              Decide on the campaign
     *
     * @param playerInfo             - the user to evaluate
     * @param executionTime          - when
     * @param responseFactor
     * @return                       - resulting action. (or null)
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor) {


        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();


        if(user.sessions < ACTIVITY_MIN){

            System.out.println("    -- Campaign " + Name + " not active. Player has not been active enough ("+ user.sessions +" sessions <  " + ACTIVITY_MIN);
            return null;

        }

        Timestamp lastSession = playerInfo.getLastMobileSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }
        int inactivity = getDaysBetween(lastSession, executionDay);

        if(user.payments == 0 && inactivity > INACTIVITY_LIMIT_FREE){

            System.out.println("    -- Campaign " + Name + " not active. Free player is inactive. ("+ inactivity+" days >  " + INACTIVITY_LIMIT_FREE);
            return null;

        }

        if(user.payments > 0 && inactivity > INACTIVITY_LIMIT_PAYING){

            System.out.println("    -- Campaign " + Name + " not active. Paying player is inactive. ("+ inactivity+" days >  " + INACTIVITY_LIMIT_PAYING);
            return null;

        }

        UsageProfileClassification classification = playerInfo.getUsageProfile();

        if(!classification.hasTriedMobile()){

            System.out.println("    -- Campaign " + Name + " not active. Not a mobile player. ( Classification: "+ classification.name()+" )");
            return null;

        }

        boolean fallBackToMail = playerInfo.getCachedUserData().fallbackFromMobile();

        if(inactivity < 4){

            System.out.println("    -- Campaign " + Name + " Not firing. Waiting for inactivity...(" + inactivity + "< 4)");
            return null;
        }

            if(inactivity > 4 && inactivity < 10){

            if(fallBackToMail){
                System.out.println("    -- Campaign " + Name + " firing (1) but falling back to mail. ");
                return new EmailAction(getMail1(user), user, executionTime, getPriority(), getTag(), 21, getState(), responseFactor);

            }

            System.out.println("    -- Campaign " + Name + " firing (1) with a mobile push. ");
            return new MobilePushAction("Hello, the games are awaiting you. Level up to take part of new benefits!", user, executionTime, getPriority(), getTag(), Name,  31, getState(), responseFactor);

        }

        if(inactivity >= 10 && inactivity < 20){

            if(fallBackToMail){
                System.out.println("    -- Campaign " + Name + " firing (2) but falling back to mail. ");
                return new EmailAction(getMail2(user), user, executionTime, getPriority(), getTag(), 22, getState(), responseFactor);
            }

            System.out.println("    -- Campaign " + Name + " firing (2) with a mobile push. ");
            return new MobilePushAction("What a lovely day for some exciting slots!", user, executionTime, getPriority(), getTag(), Name,  32, getState(), responseFactor);

        }

        if(inactivity >= 20 && inactivity < 30){

            if(fallBackToMail){
                System.out.println("    -- Campaign " + Name + " firing (3) but falling back to mail. ");
                return new EmailAction(getMail3(user), user, executionTime, getPriority(), getTag(), 23, getState(), responseFactor)
                        .attach(new GiveCoinAction(2000, user, executionTime, getPriority(), Name, 23, getState(), responseFactor));

            }
            System.out.println("    -- Campaign " + Name + " firing (3) with a mobile push. ");
            return new MobilePushAction("We have added 2000 extra coins to your account!", user, executionTime, getPriority(), getTag(), Name,  33, getState(), responseFactor)
                    .attach(new GiveCoinAction(2000, user, executionTime, getPriority(), Name, 33, getState(), responseFactor));


        }

        if(inactivity >= 30 && inactivity < 40){

            if(fallBackToMail){
                System.out.println("    -- Campaign " + Name + " firing (4) but falling back to mail. ");
                return new EmailAction(getMail4(user), user, executionTime, getPriority(), getTag(), 24, getState(), responseFactor);

            }
            System.out.println("    -- Campaign " + Name + " firing (4) with a mobile push. ");
            return new MobilePushAction("There are new games released! Come in and check them out!", user, executionTime, getPriority(), getTag(), Name,  34, getState(), responseFactor);


        }

        if(inactivity >= 40 && inactivity < 50){

            if(fallBackToMail){
                System.out.println("    -- Campaign " + Name + " firing (5) but falling back to mail. ");
                return new EmailAction(getMail5(user), user, executionTime, getPriority(), getTag(), 25, getState(), responseFactor);

            }

            System.out.println("    -- Campaign " + Name + " firing (5) with a mobile push. ");
            return new MobilePushAction("There are new games released! Come in and check them out!", user, executionTime, getPriority(), getTag(), Name,  35, getState(), responseFactor);


        }

        System.out.println("    -- Campaign " + Name + " Not firing, inactive too long ");

        return null;
    }


    /*********************************************************************
     *
     *              Campaign timing restrictions
     *
     * @param executionTime     - time of execution
     * @return                  - messgage or null if ok.
     */

    public String testFailCalendarRestriction(Timestamp executionTime, boolean overrideTime) {

        return isTooEarly(executionTime, overrideTime);

    }


    public static EmailInterface getMail1(User user) {

        return new NotificationEmail("Don't miss the level system at SlotAmerica", "<p>The games are awaiting you. Level up to take part of new benefits!</p>" +
                "<p>The more you level up, the more bonus you get. There will also be a number of benefits for you when you reach the different levels.</p>" +
                "<p> Just click here <a href=\"https://apps.facebook.com/slotAmerica/?promocode="+ Name+"\"> to test it out. Can you level up today?</a></p>"
                ,"The games are awaiting you. Level up to take part of new benefits!");
    }

    public static EmailInterface getMail2(User user) {

        return new NotificationEmail("Today is the day!", "<p>What a lovely day for some exciting slots!</p>" +
                "<p>Come play with us today and try the games. Which is your favourite?</p>" +
                "<p> Just click here <a href=\"https://apps.facebook.com/slotAmerica/?promocode="+ Name+"\"> to test it out.</a></p>",
                "What a lovely day for some exciting slots!");
    }
    public static EmailInterface getMail3(User user) {

        return new NotificationEmail("You got 2000 coins!", "<p>We have added 2000 extra coins to your account! You can use it to try some new games to see if you can find a new favourite!</p>" +
                "<p> Just click here <a href=\"https://apps.facebook.com/slotAmerica/?promocode="+ Name+"\"> to claim your coins.?</a></p>",
                "We have added 2000 extra coins to your account! You can use it to try some new games to see if you can find a new favourite!");
    }
    public static EmailInterface getMail4(User user) {

        return new NotificationEmail("Today is the day!", "<p>What a lovely day for some exciting slots!</p>" +
                "<p>Come play with us today and try the games. Which is your favourite?</p>" +
                "<p> Just click here <a href=\"https://apps.facebook.com/slotAmerica/?promocode="+ Name+"\"> to test it out.</a></p>",
                "What a lovely day for some exciting slots!");
    }
    public static EmailInterface getMail5(User user) {

        return new NotificationEmail("You got 2000 coins!", "<p>We have added 2000 extra coins to your account! You can use it to try some new games to see if you can find a new favourite!</p>" +
                "<p> Just click here <a href=\"https://apps.facebook.com/slotAmerica/?promocode="+ Name+"\"> to claim your coins.?</a></p>",
                "We have added 2000 extra coins to your account! You can use it to try some new games to see if you can find a new favourite!");
    }



}
