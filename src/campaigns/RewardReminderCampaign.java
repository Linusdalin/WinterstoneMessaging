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
 *                  Send a reminder for a reward
 *
 *                  Remind old paying players that they have a personal discount bonus
 *
 */

public class RewardReminderCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Reminder";
    private static final int CoolDown_Days = 12;
    private static final int[] MessageIds = { 1  };
    private Reward reward;
    private String game;
    private String day;
    private String message;

    private static final int Min_Sessions = 8;
    private static final int Max_Inactivity = 14;



    RewardReminderCampaign(int priority, CampaignState activation, Reward reward, String game, String day, String message){

        super(Name, priority, activation);
        this.reward = reward;
        this.game = game;
        this.day = day;
        this.message = message;
        setCoolDown(CoolDown_Days);
        registerMessageIds( MessageIds );
    }


    /********************************************************************
     *
     *              Decide on the campaign
     *
     *
     *
     *
     * @param playerInfo             - the user to evaluate
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {


        User user = playerInfo.getUser();


        String dayRestriction    = isSpecificDay(executionTime, false, day);

        if(dayRestriction != null){

            System.out.println("    -- Campaign " + Name + " not applicable. "+ dayRestriction);
            return null;
        }

        if(playerInfo.getUsageProfile().isAnonymousMobile()){

            System.out.println("    -- Campaign " + Name + " not applicable to anonymous mobile)" );
            return null;
        }

        if(user.payments == 0){

            System.out.println("    -- Campaign " + Name + " not applicable.Only paying players)" );
            return null;
        }

        if(user.sessions < Min_Sessions){

            System.out.println("    -- Campaign " + Name + " not applicable. User is not frequent enough (" + user.sessions + " < " + Min_Sessions + ")" );
            return null;

        }


        Timestamp executionDay = getDay(executionTime);

        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }
        int inactivity = getDaysBetween(lastSession, executionDay);

        if(inactivity >  Max_Inactivity){

            System.out.println("    -- Campaign " + Name + " not firing. User is inactive (" + inactivity + " >" + Max_Inactivity + ")" );
            return null;
        }

        // Check exposure and NOT claimed reward

        if(!playerInfo.hasClaimed(reward)){

            System.out.println("    -- Campaign " + Name + " firing message 1 for reward " +reward.getName() + "(" + reward.getCoins() + ")" );
            return new NotificationAction(message,
                    user, executionTime, getPriority(), getTag(), Name, 1, getState(), responseFactor)
                    .withGame(game);

        }

        System.out.println("    -- Campaign " + Name + " not firing. Player already claimed");
        return  null;

    }


    /*********************************************************************
     *
     *              Campaign timing restrictions
     *
     * @param executionTime     - time of execution
     * @return                  - messgage or null if ok.
     */

    public String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {

        String specificWeekDay = isSpecificDay(executionTime, false, day);

        if(specificWeekDay != null)
            return specificWeekDay;


        return isTooEarly(executionTime, overrideTime);

    }



}
