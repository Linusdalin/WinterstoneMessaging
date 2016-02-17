package campaigns;

import action.ActionInterface;
import action.EmailAction;
import core.PlayerInfo;
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

public class SeventeenEmailCampaign extends AbstractMobileCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Seventeen";
    private static final int CoolDown_Days = 100;     // This is a reminder mail
    private int[] MessageIds = { 1, 2 };

    private static final int DAILY_CAP = 2000;
    private int count = 0;

    private static final int MIN_SESSIONS = 100;
    private static final int MIN_AGE = 150;                      // 150 days old players. Lost cause....
    private static final int MAX_INACTIVITY_FREE = 20;
    private static final int MAX_INACTIVITY_PAYING = 0;
    private final Reward reward;

    SeventeenEmailCampaign(int priority, CampaignState activation, Reward reward){

        super(Name, priority, activation);
        this.reward = reward;
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

        if(count > DAILY_CAP){

            System.out.println("    -- Campaign " + Name + " not firing. Daily cap reached for campaign." );
            return null;

        }


        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();


        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }

        if(user.sessions < MIN_SESSIONS){

            System.out.println("    -- Campaign " + Name + " not firing. Not enough sessions");
            return null;

        }

        if(isPaying(user)){

            System.out.println("    -- Campaign " + Name + " not firing. Only non paying users.");
            return null;

        }

        if(playerInfo.getUsageProfile().isMobilePlayer()){

            System.out.println("    -- Campaign " + Name + " not firing. Not mobile players.");
            return null;

        }

        int age = getDaysBetween(user.created, executionDay);

        if(age < MIN_AGE){

            System.out.println("    -- Campaign " + Name + " not firing. Not old enough. (Age = " + age + ")");
            return null;

        }


        int inactivity = getDaysBetween(lastSession, executionDay);

        if(inactivity > MAX_INACTIVITY_FREE){

            System.out.println("    -- Campaign " + Name + " not firing. User is inactive.");
            return null;

        }

        if(playerInfo.hasClaimed(reward)){

            System.out.println("    -- Campaign " + Name + " not firing. Player already claimed the reward " );
            return null;
        }


        System.out.println("    -- Campaign " + Name + " firing");
        count++;

        return new EmailAction(loyaltyEmail(user, createPromoCode( 201 ), reward), user, executionTime, getPriority(), getTag(), 201, getState(), responseFactor);

    }



    public static EmailInterface loyaltyEmail(User user, String tag, Reward reward) {

        return new NotificationEmail("your Loyalty Bonus!",

                "<p>You are one of SlotAmericas most loyal players. We love you and we are glad you like us. To say a small thank you for being such a loyal player at SlotAmerica, we invite you to the loyalty VIP group. </p>" +

                        "<table width=\"100%\"><tr><td width=\"50%\">" +
                        "<p> You do not have to do anything, " +
                        "but play the games. To allow you to play more, occasionally we will send out free coins and free spins for the games as our <i>\"Loyalty Mystery Bonuses\"</i>. As long as you continue to be a " +
                        "loyal player and play with your free coins, they will continue to come to your facebook page.</p>" +

                        "</td>" +
                        "<td width=\"50%\">" +
                        "<a href=\"\"+ LaunchLink +tag+\"&game=os2x3x4x5x\"><img src=\"https://"+imageURL+"2x3x4x5x_mailimage.jpg\" width=\"100%\"></a>" +
                        "</td></tr></table>" +
                        "<p>Keep your eyes open!To get you started, just click <a href=\""+LaunchLink+tag+"&game=0s2x3x4x5x"+ (reward != null ? "&reward="+reward.getCode() : "")+"\">here</a> :-) </p>",




                "To say a small thank you for being such a loyal player at SlotAmerica, we invite you to the loyalty VIP group. You do not have to do anything, " +
                        "but play the games. To allow you to play more, occasionally we will send out free coins and free spins for the games as our \"Loyalty Mystery Bonuses\" to your facebook account. As long as you continue to be a " +
                        "loyal player and continue to take our offers, they will continue to come to your facebook page. Keep your eyes open :-)");
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
