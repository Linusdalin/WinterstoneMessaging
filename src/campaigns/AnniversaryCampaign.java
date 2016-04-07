package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;
import response.ResponseStat;
import rewards.Reward;
import rewards.RewardRepository;

import java.sql.Timestamp;


/************************************************************************'
 *
 *          Sending out a message to the mobile players about a new game release
 */



public class AnniversaryCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Anniversary";
    private static final int CoolDown_Days = 360;     // Once in a while

    private static final int DAILY_CAP = 1000;
    private int count = 0;

    private static final int MIN_SESSIONS = 50;
    private static final int MIN_AGE = 365;                      // 150 days old players. Lost cause....
    private static final int MAX_INACTIVITY_FREE = 30;
    private static final int MAX_INACTIVITY_PAYING = 30;

    AnniversaryCampaign(int priority, CampaignState activation){

        super(Name, priority, activation);
        setCoolDown(CoolDown_Days);

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



        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }

        if(user.sessions < MIN_SESSIONS){

            System.out.println("    -- Campaign " + Name + " not firing. Not enough sessions");
            return null;

        }


        if(playerInfo.getUsageProfile().isMobilePlayer()){

            System.out.println("    -- Campaign " + Name + " not firing. Not mobile players.");
            return null;

        }

        int age = getDaysBetween(user.created, executionDay);

        if(age < MIN_AGE || age > MIN_AGE){

            System.out.println("    -- Campaign " + Name + " not firing. Not right day. (Age = " + age + ")");
            return null;

        }


        Reward reward;
        int inactivity = getDaysBetween(lastSession, executionDay);
        int messageId;


        if(isPaying(user)){

            if(inactivity > MAX_INACTIVITY_PAYING){

                System.out.println("    -- Campaign " + Name + " not firing. Paying user is inactive.");
                return null;

            }

            reward = RewardRepository.anniversaryPaying;
            messageId = 2;
        }
        else{

            if(inactivity > MAX_INACTIVITY_FREE){

                System.out.println("    -- Campaign " + Name + " not firing. free user is inactive.");
                return null;

            }

            reward = RewardRepository.anniversaryFree;
            messageId = 3;
        }



        System.out.println("    -- Campaign " + Name + " firing");
        count++;

        return new NotificationAction( "Happy anniversary," + user.name +". You have been with the SlotAmerica family one year now. There is a present waiting for you...",
                user, executionTime, getPriority(), getTag(),  Name, messageId, getState(), responseFactor)
                .withReward(reward);


    }


    /*********************************************************************
     *
     *              Campaign timing restrictions
     *
     * @param executionTime     - time of execution
     * @return                  - messgage or null if ok.
     */

    public String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {

        return isTooEarlyForUser(playerInfo, executionTime, overrideTime);

    }




}
