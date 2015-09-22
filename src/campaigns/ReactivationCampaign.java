package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.User;
import rewards.RewardRepository;

import java.sql.Timestamp;


/************************************************************************'
 *
 *                  Old players
 *
 *                  This is a one shot campaign addressing all our old
 *                  paying and active players with appropriate bonuses
 *
 *                  //TODO: It would be nice to know if the user has claimed a bonus or not to be able to schedule n messages for reminder
 *
 */

public class ReactivationCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Reactivation";
    private static final int CoolDown_Days = 36500;     // Only once per player

    // Trigger specific config data
    private static final int INACTIVITY_LIMIT   = 240;     // This set very high to test out potential
    private static final int MIN_ACTIVITY   = 20;           // This set very high to test out potential

    private static final int DAILY_CAP   = 100;         // Max per day
    private int count = 0;


    ReactivationCampaign(int priority, CampaignState activation){

        super(Name, priority, activation);
        setCoolDown(CoolDown_Days);
    }


    /********************************************************************
     *
     *              Decide on the campaign
     *
     *              The output could be one of 4 different messages depending on the day
     *
     *
     *
     * @param playerInfo             - the user to evaluate
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime) {

        count++;

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
        int inactivity = getDaysBetween(lastSession, executionDay);


        /**
         3000 'Come Back bonus boost!' https://apps.facebook.com/slotamerica?reward=363526a3-1fb1-499d-bb33-66dd9dcb9259
         10000 'Come Back bonus boost!' https://apps.facebook.com/slotamerica?reward=93f00dac-26cf-46e4-8bde-1eb59dd13032
         20000 'Come Back bonus boost!' https://apps.facebook.com/slotamerica?reward=cac6b086-189f-4ee6-bb30-7bcfb2a0ecfa

         */


        if(inactivity > INACTIVITY_LIMIT){

            if(isHighSpender(user)){

                System.out.println("    -- Campaign " + Name + " firing message1. Creating bonus for player" );
                return new NotificationAction("You have 20,000 free coins to play for! We haven't seen you in a while. There are some fabulous new games to try out. ",
                        user, getPriority(), getTag(), Name, 1, getState())
                        .withGame("wild_cherries")
                        .withReward("cac6b086-189f-4ee6-bb30-7bcfb2a0ecfa");


            }else if(isPaying(user)){

                System.out.println("    -- Campaign " + Name + " firing message2. Creating bonus for player" );
                return new NotificationAction("You have 10,000 free coins to play for. We haven't seen you in a while. There are some fabulous new games to try out. ",
                        user, getPriority(), getTag(),  Name, 2, getState())
                        .withGame("wild_cherries")
                        .withReward("93f00dac-26cf-46e4-8bde-1eb59dd13032");

            }else if(user.sessions > MIN_ACTIVITY){

                    System.out.println("    -- Campaign " + Name + " firing message3. Creating bonus for player" );
                    return new NotificationAction("You have got 3,000 extra free coins! We haven't seen you in a while and there are some fabulous new games to try out. Click here to claim ",
                            user, getPriority(), getTag(),  Name, 3, getState())
                            .withGame("wild_cherries")
                            .withReward("363526a3-1fb1-499d-bb33-66dd9dcb9259");

            }

        }

        System.out.println("    -- Campaign " + Name + " not firing. waiting "+ INACTIVITY_LIMIT+" days ( found "+ inactivity+". last:" + lastSession.toString() );
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
