package campaigns;

import action.ActionInterface;
import action.EmailAction;
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
    private static final int CoolDown_Days = 8;     // Avoid duplicate runs for the same game


    // Trigger specific config data
    private static final int INACTIVITY_LIMIT_FREE      = 120;   // Max days inactivity to get message
    private static final int INACTIVITY_LIMIT_PAYING    = 200;   // Max days inactivity to get message
    private static final int ACTIVITY_MIN   = 2;               // Min sessions to be active


    private final String message;
    private String gameCode;
    private Reward reward;

    MobileGameNotification(int priority, CampaignState activation, String gameCode, String message, Reward reward){

        super(Name, priority, activation);
        this.gameCode = gameCode;
        this.reward = reward;
        setCoolDown(CoolDown_Days);
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

            System.out.println("    -- Campaign " + Name + " firing email to player that failed push");
            return new EmailAction(gameEmail(gameCode, user, createPromoCode(201)), user, executionTime, getPriority(), getTag(), 201, getState(), responseFactor);


        }


    }

    public static EmailInterface gameEmail(String game, User user, String tag) {


        if(game.equals("os_crystal"))

            return new NotificationEmail("New Mobile Game Crystal!",

                            "<table width=\"100%\"><tr><td width=\"50%\">" +
                                "<p> Finally a new mobile game release <b><i>Crystal</i></b> game has come to SlotAmerica Mobile too! " +
                                    "Come in and try it!<p>" +
                            "</td>" +
                            "<td width=\"50%\">" +
                                "<img src=\"https://"+imageURL+"crystal-mailimage.png\" width=200px>" +
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


        if(game.equals("os5xq"))

            return new NotificationEmail("New Mobile Game Quintuple Pay!",

                    "<table width=\"100%\"><tr><td width=\"50%\">" +
                            "<p>The latest mobile release <b><i>Quintuple Pay</i></b> is now live! An Old School Casino Classic with the magic number 5.<p>" +
                            "<p>If you have an older version of the App, you will be asked to upgrade it</p>" +
                            "</td>" +
                            "<td width=\"50%\">" +
                            "<img src=\"https://"+imageURL+"os5xq_mailimage.png\" width=200px>" +
                            "</td></tr></table>" +
                            "This is is an ‘Old School Casino Classic’-series game. We’ve had games with 5x multipliers on SlotAmerica before, but this one was designed from the ground up with " +
                            "all our latest experiences of perfecting the ‘Old School Casino Classic’-series. Transforming it to mobile added the touch of smoothness we love in a slot.</p>" +
                            "<p>Happy playing!</p>\n" +
                            "<p><b>Sam and Diane</b></p>\n" +
                            "<p>Your SlotAmerica Casino Managers</p>\n" +
                            "<table width=\"100%\" border=\"0\"><tr>" +
                            "<td width=\"50%\"><a href=\""+ UpgradeLink +"\"><img src=\"https://"+imageURL+"icon_appleStore.png\" width=200px></a></td>" +
                            "<td width=\"50%\"><a href=\""+ UpgradeLink +"\"><img src=\"https://"+imageURL+"icon_googlePlay.png\" width=200px></a></td>" +
                            "</tr></table>\n",



                    "Finally the Quituple Pay game has come to SlotAmerica Mobile too!" +
                            "This is is an ‘Old School Casino Classic’-series game. We’ve had games with 5x multipliers on SlotAmerica before, but this one was designed from the ground up with " +
                            "all our latest experiences of perfecting the ‘Old School Casino Classic’-series. Transforming it to mobile added the touch of smoothness we love in a slot.");


        if(game.equals("os7x"))

            return new NotificationEmail("New Mobile Game Seven Times Pay!",

                    "<table width=\"100%\"><tr><td width=\"50%\">" +
                            "<p>The latest mobile release <b><i>Seven Times Pay</i></b> is now live! An Old School Casino Classic. Hitting the 7:s will ensure really high pay outs.<p>" +
                            "<p>If you have an older version of the App, you will be asked to upgrade it.</p>" +
                            "</td>" +
                            "<td width=\"50%\">" +
                            "<img src=\"https://"+imageURL+"os7x_mailimage.png\" width=200px>" +
                            "</td></tr></table>" +
                            "The pulse of wicked and wonderful Vegas beats fiercely in this old-school gem. It’s a game designed to start off easy and build up momentum, to get the " +
                            "heart pounding and to have some real leverage on the pay lines as you hit. As usual, putting it on mobile added that touch of smoothness...</p>" +
                            "<p>Happy playing!</p>\n" +
                            "<p><b>Sam and Diane</b></p>\n" +
                            "<p>Your SlotAmerica Casino Managers</p>\n" +
                            "<table width=\"100%\" border=\"0\"><tr>" +
                            "<td width=\"50%\"><a href=\""+ UpgradeLink +"\"><img src=\"https://"+imageURL+"icon_appleStore.png\" width=200px></a></td>" +
                            "<td width=\"50%\"><a href=\""+ UpgradeLink +"\"><img src=\"https://"+imageURL+"icon_googlePlay.png\" width=200px></a></td>" +
                            "</tr></table>\n",



                    "Seven Times Pay released on SlotAmerica mobile!" +
                            "The pulse of wicked and wonderful Vegas beats fiercely in this old-school gem. It’s a game designed to start off easy and build up momentum, to get the " +
                            "heart pounding and to have some real leverage on the pay lines as you hit. As usual, putting it on mobile added that touch of smoothness...</p>");

        if(game.equals("clockwork"))

            return new NotificationEmail("New Mobile Game Clockwork!",

                    "<table width=\"100%\"><tr><td width=\"50%\">" +
                            "<p>The latest mobile release <b><i>Clockwork</i></b> is now live! It is one of our all time favorites here at SlotAmerica<p>" +
                            "<p>If you have an older version of the App, you will be asked to upgrade it.</p>" +
                            "</td>" +
                            "<td width=\"50%\">" +
                            "<img src=\"https://"+imageURL+"clockwork_mailimage.png\" width=200px>" +
                            "</td></tr></table>" +
                            "<p>Not a minute too soon, this second-to-none slot serves up round-the-clock fun and excitement!</p>" +
                            "<p>Enter our wonderful world of dials, cogs and springs – all geared up to make you win things! With a timely mix of " +
                            "free-spins and multiplier wilds, we’re sure Clockwork will stand the tests of time!\n</p>" +
                            "<p>Happy playing!</p>\n" +
                            "<p><b>Sam and Diane</b></p>\n" +
                            "<p>Your SlotAmerica Casino Managers</p>\n" +
                            "<table width=\"100%\" border=\"0\"><tr>" +
                            "<td width=\"50%\"><a href=\""+ UpgradeLink +"\"><img src=\"https://"+imageURL+"icon_appleStore.png\" width=200px></a></td>" +
                            "<td width=\"50%\"><a href=\""+ UpgradeLink +"\"><img src=\"https://"+imageURL+"icon_googlePlay.png\" width=200px></a></td>" +
                            "</tr></table>\n",



                    "Clockwork released on SlotAmerica mobile!" +
                            "Not a minute too soon, this second-to-none slot serves up round-the-clock fun and excitement!" +
                            "Enter our wonderful world of dials, cogs and springs – all geared up to make you win things! With a timely mix of " +
                            "free-spins and multiplier wilds, we’re sure Clockwork will stand the tests of time!\n");



        if(game.equals("eight_times_pay"))

            return new NotificationEmail("New Mobile Game Eight Times Pay!",

                    "<table width=\"100%\"><tr><td width=\"50%\">" +
                            "<p>Time again for a new mobile release. This time  <b><i>Eight Times Pay</i></b>! This is the latest in the wild multiplier games.<p>" +
                            "<p>If you have an older version of the App, you will be asked to upgrade it.</p>" +
                            "</td>" +
                            "<td width=\"50%\">" +
                            "<img src=\"https://"+imageURL+"os8x_mailimage.png\" width=200px>" +
                            "</td></tr></table>" +
                            "<p>Rack’ em up! SlotAmerica’s new release is 8x Special, inspired in style by the wonderful world of Pool. We fondly remember “The Colour of Money”, with Paul Newman and Tom Cruise.</p>" +
                            "<p>And whilst there’s absolutely no hustling going on in SlotAmerica – our 8x Special has plenty of ball-breaking action! Double pay wilds, a whopping 8x wild on the center reel and - to top it off - a wild joker instant re-spin that will hopefully have you on a roll in no time!</p>" +
                            "<p>Happy playing!</p>\n" +
                            "<p><b>Sam and Diane</b></p>\n" +
                            "<p>Your SlotAmerica Casino Managers</p>\n" +
                            "<table width=\"100%\" border=\"0\"><tr>" +
                            "<td width=\"50%\"><a href=\""+ UpgradeLink +"\"><img src=\"https://"+imageURL+"icon_appleStore.png\" width=200px></a></td>" +
                            "<td width=\"50%\"><a href=\""+ UpgradeLink +"\"><img src=\"https://"+imageURL+"icon_googlePlay.png\" width=200px></a></td>" +
                            "</tr></table>\n",



                    "Clockwork released on SlotAmerica mobile!" +
                            "Rack’ em up! SlotAmerica’s new release is 8x Special, inspired in style by the wonderful world of Pool. We fondly remember “The Colour of Money”, with Paul Newman and Tom Cruise." +
                            "And whilst there’s absolutely no hustling going on in SlotAmerica – our 8x Special has plenty of ball-breaking action! Double pay wilds, a whopping 8x wild on the center reel and - to top it off - a wild joker instant re-spin that will hopefully have you on a roll in no time!\n"
                            );



        throw new RuntimeException("No email defined for game " + game);

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
