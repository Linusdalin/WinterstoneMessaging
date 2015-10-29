package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;
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
    private String message;


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

        if(!isPaying(user)){

            System.out.println("    -- Campaign " + Name + " not firing. Only Paying users." );
            return null;

        }

        Timestamp executionDay = getDay(executionTime);

        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }

        // Check exposure and NOT claimed reward

        if(false){

            System.out.println("    -- Campaign " + Name + " firing message 1" );
            return new NotificationAction(message,
                    user, getPriority(), getTag(), Name, 1, getState(), responseFactor)
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

        return isTooEarly(executionTime, overrideTime);

    }



}
