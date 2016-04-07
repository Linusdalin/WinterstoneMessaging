package campaigns;

import action.ActionInterface;
import action.EmailAction;
import action.GiveCoinAction;
import core.PlayerInfo;
import email.EmailInterface;
import email.NotificationEmail;
import remoteData.dataObjects.User;
import response.ResponseStat;

import java.sql.Timestamp;


/************************************************************************'
 *
 *                  Old players
 *
 *                  This is a new shot to reactivate our old players through email.
 *
 *
 */

public class ReactivationEmailCampaign extends AbstractMobileCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "ReactivationEmail";
    private static final int CoolDown_Days = 365000;     // Only once per player

    // Trigger specific config data
    private static final int INACTIVITY_LIMIT   = 200;      // Three months old
    private static final int MIN_ACTIVITY   = 20;           // This set very high to test out potential

    private static final int DAILY_CAP   = 1000;         // Max per day
    private int count = 0;


    ReactivationEmailCampaign(int priority, CampaignState activation){

        super(Name, priority, activation);
        setCoolDown(CoolDown_Days);
    }


    /********************************************************************
     *
     *              Decide on the campaign
     *
     *              The output could be one of 4 different messages depending on the day
     *
     *
     *
     * @param playerInfo             - the user to evaluate
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {

        if(count > DAILY_CAP){

            System.out.println("    -- Campaign " + Name + " not firing. Daily cap reached for campaign." );
            return null;

        }

        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        Timestamp lastSession = playerInfo.getLastSessionLocalCache();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }
        int inactivity = getDaysBetween(lastSession, executionDay);

        if(inactivity > INACTIVITY_LIMIT){

            int coins = 0;
            int message = 0;

            if(isHighSpender(user)){
                coins = 100000;
                message = 1;
            }
            else if(isFrequent(user)){
                coins = 25000;
                message = 2;
            }
            else if(isPaying(user)){
                coins = 10000;
                message = 3;
            }
            else{
                coins = 5000;
                message = 4;

            }


            if(message > 0){

                System.out.println("    -- Campaign " + Name + " firing with 20000 coins" );
                count++;

                return new EmailAction(comebackEmail(user, coins, createPromoCode( message + 200 )), user, executionTime, getPriority(), getTag(), message + 200, getState(), responseFactor)
                        .attach(new GiveCoinAction(coins, user, executionTime, getPriority(), Name, message+200, getState(), responseFactor));
            }


        }


        System.out.println("    -- Campaign " + Name + " not firing. waiting "+ INACTIVITY_LIMIT+" days ( found "+ inactivity+". last:" + lastSession.toString() );
        return null;



    }


    public static EmailInterface comebackEmail(User user, int coins, String tag) {

        return new NotificationEmail("SlotAmerica is still here for you!",

                "<p>While you were away, we have topped up your account with <b>"+ coins+"</b> coins. " +
                    "It would be a shame to let them go to waste, right?</p>" +

                        "<table width=\"100%\"><tr><td width=\"50%\">" +
                        "<p> There are new developments on the site, and of course some new and fabulous games you can try out with them! Like the Old School 2x 3x 4x 5x." +
                        "Welcome back to test it out. Just click <a href=\""+LaunchLink+tag+"&game=0s2x3x4x5x\">here</a> :-) </p>" +
                        "</td>" +
                        "<td width=\"50%\">" +
                        "<a href=\"\"+ LaunchLink +tag+\"&game=os2x3x4x5x\"><img src=\"https://"+imageURL+"2x3x4x5x_mailimage.jpg\" width=\"100%\"></a>" +
                        "</td></tr></table>" +

                "<p>Please also note that we are available on both iPhone and Android! On a mobile device you can go directly here<a><href=\""+ LaunchLink +tag+"\"> to download!</a>  " +
                        "If you login with your facebook account, you will keep your existing account with all your coins and level.</p>"+
                "<p>Happy playing!</p>\n" +
                "<p><b>Sam and Diane</b></p>\n" +
                "<p>Your SlotAmerica Casino Managers</p>\n" +
                "<table width=\"100%\" border=\"0\"><tr>" +
                        "<td width=\"50%\"><a href=\""+ LaunchLink +tag+"\"><img src=\"https://"+imageURL+"icon_appleStore.png\" width=200px></a></td>" +
                        "<td width=\"50%\"><a href=\""+ LaunchLink +tag+"\"><img src=\"https://"+imageURL +"icon_googlePlay.png\" width=200px></a></td>" +
                        "</tr></table>\n",



                "We have topped up your account with "+ coins+ " coins.There are some fabulous new games you can try out with it.");
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



}
