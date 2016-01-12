package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;
import rewards.Reward;
import rewards.RewardRepository;

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
    private String message;

    private static final int Min_Sessions = 15;
    private static final int Max_Inactivity = 5;



    RewardReminderCampaign(int priority, CampaignState activation, Reward reward, String game, String message){

        super(Name, priority, activation);
        this.reward = reward;
        this.game = game;
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


    public ActionInterface  evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor) {


        User user = playerInfo.getUser();


        String dayRestriction    = isSpecificDay(executionTime, false, "måndag");

        if(dayRestriction != null){

            System.out.println("    -- Campaign " + Name + " not applicable. "+ dayRestriction);
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

        if(!RewardRepository.hasClaimed(user, reward)){

            System.out.println("    -- Campaign " + Name + " firing message 1 for reward " +reward.getName() + "(" + reward.getCoins() + ")" );
            return new NotificationAction(message,
                    user, executionTime, getPriority(), getTag(), Name, 1, getState(), responseFactor)
                    .withGame(game);

        }

        return  null;

    }


    /*********************************************************************
     *
     *              Campaign timing restrictions
     *
     * @param executionTime     - time of execution
     * @return                  - messgage or null if ok.
     */

    public String testFailCalendarRestriction(Timestamp executionTime, boolean overrideTime) {

        String specificWeekDay = isSpecificDay(executionTime, false, "måndag");

        if(specificWeekDay != null)
            return specificWeekDay;


        return isTooEarly(executionTime, overrideTime);

    }



}
