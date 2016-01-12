package campaigns;

import action.ActionInterface;
import action.MobilePushAction;
import core.PlayerInfo;
import core.UsageProfileClassification;
import remoteData.dataObjects.User;
import rewards.Reward;

import java.sql.Timestamp;


/************************************************************************'
 *
 *          Sending out a message to the mobile players about a new game release
 */

public class MobileConversionWelcomeCampaign extends AbstractMobileCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "ConversionWelcome";
    private static final int CoolDown_Days = 999999999;     // Only Once
    private int[] MessageIds = { 1 };


    MobileConversionWelcomeCampaign(int priority, CampaignState activation){

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



        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }
        int inactivity = getDaysBetween(lastSession, executionDay);

        if(user.payments == 0 && inactivity > 1){

            System.out.println("    -- Campaign " + Name + " not firing. No activity yesterday");
            return null;

        }


        UsageProfileClassification classification = playerInfo.getUsageProfile();

        if(classification.isAnonymousMobile()){

            System.out.println("    -- Campaign " + Name + " not active. Player is anonymous mobile. Campaign is only for converted players )");
            return null;

        }



        if(user.isMobileFirst()){

            System.out.println("    -- Campaign " + Name + " not active. Player is registered on mobile, not converted)");
            return null;

        }
        Timestamp firstMobile = playerInfo.getFirstMobileSession();

        if(firstMobile == null){

            System.out.println("    -- Campaign " + Name + " not active. Player has not played mobile)");
            return null;
        }

        int daysMobile = getDaysBetween(lastSession, executionDay);


        if(daysMobile != 1){

            System.out.println("    -- Campaign " + Name + " not active. Player did not start mobile yesterday");
            return null;

        }

        System.out.println("    -- Campaign " + Name + " firing. ");

        Reward reward = getRewardForUser(user);

        MobilePushAction action =  new MobilePushAction("Happy to see you playing SlotAmerica on mobile. Here are your " + reward.getCoins() + " coins for testing it out", user, executionTime, getPriority(), getTag(), Name,  1, getState(), responseFactor)
                    .withReward(reward);

        return action;

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
