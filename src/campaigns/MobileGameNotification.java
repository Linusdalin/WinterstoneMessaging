package campaigns;

import action.ActionInterface;
import action.MobilePushAction;
import action.NotificationAction;
import core.PlayerInfo;
import core.UsageProfileClassification;
import remoteData.dataObjects.User;
import rewards.Reward;

import java.sql.Timestamp;


/************************************************************************'
 *
 *          Sending out a message to the mobile players about a new game release
 */

public class MobileGameNotification extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "MobileGameNotification";
    private static final int CoolDown_Days = 13;     // Avoid duplicate runs for the same game
    private int[] MessageIds = { 1 };


    // Trigger specific config data
    private static final int INACTIVITY_LIMIT_FREE      = 30;   // Max days inactivity to get message
    private static final int INACTIVITY_LIMIT_PAYING    = 72;   // Max days inactivity to get message
    private static final int ACTIVITY_MIN   = 5;               // Min sessions to be active


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

        System.out.println("    -- Campaign " + Name + " firing. ");

        if(!playerInfo.fallbackFromMobile()){

            MobilePushAction action =  new MobilePushAction("New game release! " + message, user, executionTime, getPriority(), getTag(), Name,  31, getState(), responseFactor)
                    .withGame(gameCode);

            if(reward != null)
                action.withReward(reward);

            return  action;

        }
        else{

            // Mobile player that has failed mobile communication. Fallback to facebook message

            if(!classification.isAnonymousMobile()){

                return new NotificationAction("New release on SlotAmerica mobile! ! " + message,
                        user, executionTime, getPriority(), getTag() , Name, 1, getState(), responseFactor);

            }

        }

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



}
