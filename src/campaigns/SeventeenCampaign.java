package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;
import response.ResponseStat;
import rewards.Reward;

import java.sql.Timestamp;


/************************************************************************'
 *
 *          Sending out a message to the mobile players about a new game release
 */

public class SeventeenCampaign extends AbstractMobileCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Seventeen";
    private static final int CoolDown_Days = 5;     // Once in a while

    private static final int DAILY_CAP = 1000;
    private int count = 0;

    private static final int MIN_SESSIONS = 100;
    private static final int MIN_AGE = 150;                      // 150 days old players. Lost cause....
    private static final int MIN_INACTIVITY_FREE = 20;
    private static final int MAX_INACTIVITY_FREE = 40;            // Normally 0 - 3 for active players
    private static final int MAX_INACTIVITY_PAYING = 0;
    private final Reward reward;

    SeventeenCampaign(int priority, CampaignState activation, Reward reward){

        super(Name, priority, activation);
        this.reward = reward;
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

        if(count > DAILY_CAP){

            System.out.println("    -- Campaign " + Name + " not firing. Daily cap reached for campaign." );
            return null;

        }

        //System.out.println(" --- Responses: " + response.toString());


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

        if(inactivity < MIN_INACTIVITY_FREE){

            System.out.println("    -- Campaign " + Name + " not firing. User is already active.");
            return null;

        }

        if(playerInfo.hasClaimed(reward)){

            System.out.println("    -- Campaign " + Name + " not firing. Player already claimed the reward " );
            return null;
        }


        System.out.println("    -- Campaign " + Name + " firing");
        count++;

        System.out.println(" --- Responses: " + response.toString());


        return new NotificationAction( user.name +", your loyalty free coin bonus is here. Click now to claim your coins!",
                user, executionTime, getPriority(), getTag(),  Name, 2, getState(), responseFactor)
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

        return isTooEarly(executionTime, overrideTime);

    }




}
