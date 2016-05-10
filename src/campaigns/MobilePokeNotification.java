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
import response.ResponseStat;

import java.sql.Timestamp;


/************************************************************************'
 *
 *          Sending out a message to the mobile players about a new game release
 */

public class MobilePokeNotification extends AbstractMobileCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "MobilePokeNotification";
    private static final int CoolDown_Days = 8;
    private int[] MessageIds = { };


    // Trigger specific config data
    private static final int INACTIVITY_LIMIT_FREE      = 60;   // Max days inactivity to get message
    private static final int INACTIVITY_LIMIT_PAYING    = 150;   // Max days inactivity to get message
    private static final int ACTIVITY_MIN   = 4;               // Min sessions to be active


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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {


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
                return new EmailAction(getMail1(user), user, executionTime, getPriority(), getTag(), 201, getState(), responseFactor);

            }

            System.out.println("    -- Campaign " + Name + " firing (1) with a mobile push. ");
            return new MobilePushAction("Hello, the games are awaiting you. Level up to take part of new benefits!", user, executionTime, getPriority(), getTag(), Name,  301, getState(), responseFactor);

        }

        if(inactivity >= 10 && inactivity < 16){

            System.out.println("    -- Campaign " + Name + " firing 10 day email ");
            return new EmailAction(getMail7(user, createPromoCode(207)), user, executionTime, getPriority(), getTag(), 207, getState(), responseFactor);

        }

        if(inactivity >= 16 && inactivity < 24){

            if(fallBackToMail){
                System.out.println("    -- Campaign " + Name + " firing (2) but falling back to mail. ");
                return new EmailAction(getMail2(user), user, executionTime, getPriority(), getTag(), 202, getState(), responseFactor);
            }

            System.out.println("    -- Campaign " + Name + " firing (2) with a mobile push. ");
            return new MobilePushAction("What a lovely day for some exciting slots!", user, executionTime, getPriority(), getTag(), Name,  302, getState(), responseFactor);

        }



        if(inactivity >= 24 && inactivity < 32){

            if(fallBackToMail){
                System.out.println("    -- Campaign " + Name + " firing (3) but falling back to mail. ");
                return new EmailAction(getMail3(user), user, executionTime, getPriority(), getTag(), 203, getState(), responseFactor)
                        .attach(new GiveCoinAction(2000, user, executionTime, getPriority(), Name, 203, getState(), responseFactor));

            }
            System.out.println("    -- Campaign " + Name + " firing (3) with a mobile push. ");
            return new MobilePushAction("We have added 2000 extra coins to your account!", user, executionTime, getPriority(), getTag(), Name,  303, getState(), responseFactor)
                    .attach(new GiveCoinAction(2000, user, executionTime, getPriority(), Name, 303, getState(), responseFactor));


        }

        if(inactivity >= 32 && inactivity < 40){

            if(fallBackToMail){
                System.out.println("    -- Campaign " + Name + " firing (4) but falling back to mail. ");
                return new EmailAction(getMail4(user), user, executionTime, getPriority(), getTag(), 204, getState(), responseFactor);

            }
            System.out.println("    -- Campaign " + Name + " firing (4) with a mobile push. ");
            return new MobilePushAction("There are new games released! Come in and check them out!", user, executionTime, getPriority(), getTag(), Name,  304, getState(), responseFactor);


        }

        if(inactivity >= 40 && inactivity < 48){

            if(fallBackToMail){
                System.out.println("    -- Campaign " + Name + " firing (5) but falling back to mail. ");
                return new EmailAction(getMail5(user), user, executionTime, getPriority(), getTag(), 205, getState(), responseFactor);

            }

            System.out.println("    -- Campaign " + Name + " firing (5) with a mobile push. ");
            return new MobilePushAction("There are new games released! Come in and check them out!", user, executionTime, getPriority(), getTag(), Name,  305, getState(), responseFactor);


        }

        if(inactivity >= 48){

                System.out.println("    -- Campaign " + Name + " firing (6) - email. ");
                return new EmailAction(getMail6(user), user, executionTime, getPriority(), getTag(), 206, getState(), responseFactor);


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

    public String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {

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
                "<p> Just click here <a href=\"http://smarturl.it/launch_slotamerica?promocode="+ Name+"\"> to test it out.</a></p>",
                "What a lovely day for some exciting slots!");
    }
    public static EmailInterface getMail3(User user) {

        return new NotificationEmail("You got 2000 coins!", "<p>We have added 2000 extra coins to your account! You can use it to try some new games to see if you can find a new favourite!</p>" +
                "<p> Just click here <a href=\"http://smarturl.it/launch_slotamerica?promocode="+ Name+"\"> to claim your coins.</a></p>",
                "We have added 2000 extra coins to your account! You can use it to try some new games to see if you can find a new favourite!");
    }
    public static EmailInterface getMail4(User user) {

        return new NotificationEmail("Today is the day!", "<p>What a lovely day for some exciting slots!</p>" +
                "<p>Come play with us today and try the games. Which is your favourite?</p>" +
                "<p> Just click here <a href=\"http://smarturl.it/launch_slotamerica?promocode="+ Name+"\"> to test it out.</a></p>",
                "What a lovely day for some exciting slots!");
    }
    public static EmailInterface getMail5(User user) {

        return new NotificationEmail("You got 2000 coins!", "<p>We have added 2000 extra coins to your account! You can use it to try some new games to see if you can find a new favourite!</p>" +
                "<p> Just click here <a href=\"http://smarturl.it/launch_slotamerica?promocode="+ Name+"\"> to claim your coins.</a></p>",
                "We have added 2000 extra coins to your account! You can use it to try some new games to see if you can find a new favourite!");
    }

    public static EmailInterface getMail6(User user) {

        return new NotificationEmail("Which is your favourite game?", "<p>The game selector on mobile is getting close to full. For new games, this means that an old game will have to go. " +
                "It is always the least played game that is removed. To ensure that not the wrong game is put on the shelf, go in and play now to put a vote on your favorite! The more you bet, the more votes your game gets.</p>" +
                "<p> Just click here <a href=\"http://smarturl.it/launch_slotamerica?promocode="+ Name+"\"> to claim your coins.</a></p>",
                "The game selector on mobile is getting close to full. For new games, this means that an old game will have to go." +
                "It is always the least played game that is removed. To ensure that not the wrong game is put on the shelf, go in and play now to put a vote on your favorite! The more you bet, the more votes your game gets.");
    }


    public static EmailInterface getMail7(User user, String tag) {

        return new NotificationEmail("your SlotAmerica loyalty VIP Level",
                "<p>The SlotAmerica Loyalty VIP system is full of presents and surprises. While playing you advance through the system and the reach the better it gets." +


                        "<table width=100%><tr><td width=40%>"+
                "<p>Here you see the loyalty player profile in the app. You can see your current level and your current bonus levels. Here you can also see your user Id which is good to have if you have any questions for support.</p>"+
                "</td><td width=60%><img src=\"https://"+ imageURL+"loyalty_mobile.png\" width=\"100%\"></td>"+
                "</tr></table>"+
                "<p>The VIP loyalty rewards include:</p>" +
                "<ul>" +
                        "<li>Diamond Baseline where you will start over at <i>two or more diamonds</i> whenever starting over to collect.</li>" +
                        "<li>Personal discount where you get <i>extra coins</i> whenever purchasing a coin package.</li>"+
                        "<li>Special surprise rewards.</li>" +
                "</ul>" +
                "<p>Please also note the coin rewards <i>every time you level up</i></p>"+
                "<p>To get to the game you can click <i><a href=\""+LaunchLink+tag+"\">here</a></i></p>",


                "The SlotAmerica Loyalty VIP system is full of presents and surprises. While playing you advance through the system and the reach the better it gets.");

    }


}
