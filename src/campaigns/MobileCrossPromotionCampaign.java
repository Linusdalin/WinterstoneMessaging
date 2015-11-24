package campaigns;

import action.ActionInterface;
import action.EmailAction;
import core.PlayerInfo;
import core.UsageProfileClassification;
import email.EmailInterface;
import email.NotificationEmail;
import remoteData.dataObjects.User;
import rewards.Reward;
import rewards.RewardRepository;

import java.sql.Timestamp;


/************************************************************************'
 *
 *          Sending out a message to the mobile players about a new game release
 */

public class MobileCrossPromotionCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "CrossPromotion";
    private static final int CoolDown_Days = 999999999;     // Only Once
    private int[] MessageIds = { 1 };

    private static final String GameLink = "https://app.adjust.com/drl7ga?deep_link=slotamerica://&campaign=";
    private static final String ImageLink = "http://slamdunkgaming.com/IpadIphone.png";

    private static final int INACTIVITY_LIMIT_FREE      = 18;   // Max days inactivity to get message
    private static final int INACTIVITY_LIMIT_PAYING    = 75;   // Max days inactivity to get message
    private static final int ACTIVITY_MIN   = 12;               // Min sessions to be active

    private static final int DAILY_LIMIT   = 400;               // Min sessions to be active

    private int count = 0;

    MobileCrossPromotionCampaign(int priority, CampaignState activation){

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

        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }
        int inactivity = getDaysBetween(lastSession, executionDay);

        if(isPaying(user)){

            System.out.println("    -- Campaign " + Name + " not active. Only free users for now");
            return null;
        }

        if(!isPaying(user) && inactivity > INACTIVITY_LIMIT_FREE){

            System.out.println("    -- Campaign " + Name + " not active. Free player is inactive. ("+ inactivity+" days >  " + INACTIVITY_LIMIT_FREE);
            return null;

        }

        if(isPaying(user) && inactivity > INACTIVITY_LIMIT_PAYING){

            System.out.println("    -- Campaign " + Name + " not active. Paying player is inactive. ("+ inactivity+" days >  " + INACTIVITY_LIMIT_PAYING);
            return null;

        }


        UsageProfileClassification classification = playerInfo.getUsageProfile();

        if(classification.hasTriedMobile()){

            System.out.println("    -- Campaign " + Name + " not active. Only for non-mobile players )");
            return null;

        }

        if(count >= DAILY_LIMIT){

            System.out.println("    -- Campaign " + Name + " not active. Daily limit "+DAILY_LIMIT + " reached.");
            return null;
        }

        count++;
        System.out.println("    -- Campaign " + Name + " firing. ");



        Reward reward = getRewardForUser(user);
        return new EmailAction(tryMobileEmail(user, createPromoCode( 1 ), reward), user, executionTime, getPriority(), getTag(), 1, getState(), responseFactor);

    }

    private String createPromoCode(int messageId) {
        return Name.replaceAll(" ", "") + "-" + messageId;
    }

    public static EmailInterface tryMobileEmail(User user, String tag, Reward reward) {


        return new NotificationEmail("SlotAmerica is now available on iOS!", "<p>SlotAmerica is now available on iOS</p>" +

                "<table><tr><td width=40%>"+
                "It is with great pleasure that we announce the launch of SlotAmerica on iOS. Available today on the <a href=\""+GameLink+tag+"\">Appstore</a>, you can now finally enjoy SlotAmerica on your iPhone and iOS."+
                "</td><td width=60%><img src=\""+ ImageLink+"\" width=\"250\"></td>"+
                "</tr></table>"+
                "<p>Here, we’ve collected some of your favourite SlotAmerica games, including Royal Colors, Black Onyx and Pink Sapphires. More great games will be added all the time.</p>"+
                "<p>There are several ways to download the game to your iOS device:</p>\n"+
                "<li>Enter the Appstore on your iPhone or iPad, and search for ‘SlotAmerica’</li>\n"+
                "<li>Got to our <a href=\"www.facebook.com/slotamerica/\">SlotAmerica Facebook page</a>  and click on the “Send to Mobile”-button.</li>\n"+
                "<li>Follow this link to go directly to the Appstore <a href=\""+ GameLink+tag+"\">here</a></li>\n"+
                "<p>Once you’ve downloaded and entered SlotAmerica, be sure to register with your Facebook account and continue to play from your existing SlotAmerica account – same level, same coins, same daily bonus.</p>\n"+
                "<p>As a gift to you for downloading the game, we’ll send you "+ reward.getCoins()+" free coins to your phone (this might take a day or two – please bear with us). All you then have to do is click on the notification and claim your gift. \n"+
                "<p>Finally, if you experience any difficulties with any of this – or have any questions or feedback about the game, please don’t hesitate to contact us on support@slot-america.com.</p>" +
                "<p>We’re really look forward to finally sharing the SlotAmerica iOS experience with you!</p>\n" +
                "<p>Happy playing!</p>\n" +
                "<p><b>Sam and Diane</b></p>\n" +
                "<p>Your SlotAmerica Casino Managers</p>\n" +
                "<p><i>Support for Android devices is still in the works. We’ll let you know as soon as we’re ready to launch.</i></p>\n",

                "It is with great pleasure that we announce the launch of SlotAmerica on iOS. Available today on the Appstore, you can now finally enjoy SlotAmerica on your iPhone and iOS.\n"+
                        "Here, we’ve collected some of your favourite SlotAmerica games, including Royal Colors, Black Onyx and Pink Sapphires. More great games will be added all the time.\n"+
                        "There are several ways to download the game to your iOS device:</p>\n"+
                        " - Enter the Appstore on your iPhone or iPad, and search for ‘SlotAmerica’\n"+
                        " - Got to our SlotAmerica Facebook page and click on the “Send to Mobile”-button.\n"+
                        "Once you’ve downloaded and entered SlotAmerica, be sure to register with your Facebook account and continue to play from your existing SlotAmerica account – same level, same coins, same daily bonus.\n"+
                        "As a gift to you for downloading the game, we’ll send you "+ reward.getCoins()+" free coins to your phone (this might take a day or two – please bear with us). All you then have to do is click on the notification and claim your gift."+


                        "plain text");
    }



    //TODO: Different actions depending on user (payments and frequency)

    private Reward getRewardForUser(User user) {
        return RewardRepository.mobile1;
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
