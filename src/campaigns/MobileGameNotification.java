package campaigns;

import action.ActionInterface;
import action.MobilePushAction;
import core.PlayerInfo;
import core.UsageProfileClassification;
import email.EmailInterface;
import email.NotificationEmail;
import remoteData.dataObjects.User;
import response.ResponseStat;
import rewards.Reward;

import java.sql.Timestamp;


/************************************************************************'
 *
 *          Sending out a message to the mobile players about a new game release
 */

public class MobileGameNotification extends AbstractMobileCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "MobileGameNotification";
    private static final int CoolDown_Days = 13;     // Avoid duplicate runs for the same game
    private int[] MessageIds = { 1, 31 };


    // Trigger specific config data
    private static final int INACTIVITY_LIMIT_FREE      = 120;   // Max days inactivity to get message
    private static final int INACTIVITY_LIMIT_PAYING    = 120;   // Max days inactivity to get message
    private static final int ACTIVITY_MIN   = 2;               // Min sessions to be active


    private final String message;
    private String gameCode;
    private Reward reward;

    MobileGameNotification(int priority, CampaignState activation, String gameCode, String message, Reward reward){

        super(Name, priority, activation);
        this.gameCode = gameCode;
        this.reward = reward;
        setCoolDown(CoolDown_Days);
        registerMessageIds( MessageIds );
        this.message = message;
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

        if(!playerInfo.getUsageProfile().hasTriedMobile()){

            System.out.println("    -- Campaign " + Name + " not active. Only mobile payers");
            return null;

        }

        if(user.sessions < ACTIVITY_MIN){

            System.out.println("    -- Campaign " + Name + " not active. Player has not been active enough ("+ user.sessions +" sessions <  " + ACTIVITY_MIN);
            return null;

        }

        Timestamp lastSession = playerInfo.getLastSession();
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


        if(!playerInfo.fallbackFromMobile()){

            System.out.println("    -- Campaign " + Name + " firing mobile push ");
            MobilePushAction action =  new MobilePushAction("New game release! " + message, user, executionTime, getPriority(), getTag(), Name,  301, getState(), responseFactor)
                    .withGame(gameCode);

            if(reward != null)
                action.withReward(reward);

            return  action;

        }
        else{

            //System.out.println("    -- Campaign " + Name + " firing email to player that failed push");
            //return new EmailAction(gameEmail(gameCode, user, createPromoCode(201)), user, executionTime, getPriority(), getTag(), 201, getState(), responseFactor);
            return null;
        }


    }

    public static EmailInterface gameEmail(String game, User user, String tag) {


        if(game.equals("os_crystal"))

            return new NotificationEmail("New Mobile Game Crystal!",

                            "<table width=\"100%\"><tr><td width=\"50%\">" +
                                "<p> Finally a new mobile game release <b><i>Golden Dollar</i></b> game has come to SlotAmerica Mobile too! " +
                                    "Come in and try it!<p>" +
                            "</td>" +
                            "<td width=\"50%\">" +
                                "<img src=\"https://"+imageURL+"goldendollar-mailimage.png\" width=200px>" +
                            "</td></tr></table>" +
                            "Get the feeling of the dusty old west where.</p>" +
                            "<p>Happy playing!</p>\n" +
                            "<p><b>Sam and Diane</b></p>\n" +
                            "<p>Your SlotAmerica Casino Managers</p>\n" +
                            "<table width=\"100%\" border=\"0\"><tr>" +
                            "<td width=\"50%\"><a href=\""+ UpgradeLink +"\"><img src=\"https://"+imageURL+"icon_appleStore.png\" width=200px></a></td>" +
                            "<td width=\"50%\"><a href=\""+ UpgradeLink +"\"><img src=\"https://"+imageURL+"icon_googlePlay.png\" width=200px></a></td>" +
                            "</tr></table>\n",



                    "Finally the Crystal game has come to SlotAmerica Mobile too!" +
                    "It’s another one of SlotAmerica’s Old School Casino Classics, but this time the team has positively packed it with 2x and 4x WILD multipliers!" +
                    "They’re positively everywhere. And I’ve hit some really nice wins and been soooo close to hitting a mountainous jackpot!");


        if(game.equals("golden_dollar"))

            return new NotificationEmail("New Mobile Game Golden Dollar!",

                    "<table width=\"100%\"><tr><td width=\"50%\">" +
                            "<p> Now there is a new  <b><i>Crystal</i></b> game has come to SlotAmerica Mobile too! It’s another one of SlotAmerica’s Old School Casino Classics, but this time the team has positively packed it with 2x and 4x WILD multipliers!<p>" +
                            "</td>" +
                            "<td width=\"50%\">" +
                            "<img src=\"https://"+imageURL+"crystals-mailimage.png\" width=200px>" +
                            "</td></tr></table>" +
                            "They’re positively everywhere. And I’ve hit some really nice wins and been soooo close to hitting a mountainous jackpot! Click below to upgrade the app to get the new game!</p>" +
                            "<p>Happy playing!</p>\n" +
                            "<p><b>Sam and Diane</b></p>\n" +
                            "<p>Your SlotAmerica Casino Managers</p>\n" +
                            "<table width=\"100%\" border=\"0\"><tr>" +
                            "<td width=\"50%\"><a href=\""+ UpgradeLink +"\"><img src=\"https://"+imageURL+"icon_appleStore.png\" width=200px></a></td>" +
                            "<td width=\"50%\"><a href=\""+ UpgradeLink +"\"><img src=\"https://"+imageURL+"icon_googlePlay.png\" width=200px></a></td>" +
                            "</tr></table>\n",



                    "Finally the Crystal game has come to SlotAmerica Mobile too!" +
                            "It’s another one of SlotAmerica’s Old School Casino Classics, but this time the team has positively packed it with 2x and 4x WILD multipliers!" +
                            "They’re positively everywhere. And I’ve hit some really nice wins and been soooo close to hitting a mountainous jackpot!");

        throw new RuntimeException("No email defined for game " + game);

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



}
