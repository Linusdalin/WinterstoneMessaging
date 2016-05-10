package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;
import response.ResponseStat;

import java.sql.Timestamp;


/************************************************************************'
 *
 *                  Old players
 *
 *                  This is a one shot campaign addressing all our old
 *                  paying and active players with appropriate bonuses
 *
 *
 */

public class ReactivationCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Reactivation3060";
    private static final int CoolDown_Days = 36500;     // Only once per player

    // Trigger specific config data
    private static final int INACTIVITY_LIMIT   = 20;
    private static final int INACTIVITY_GIVEUP   = 60;
    private static final int MIN_ACTIVITY   = 20;                  // Should be 30

    private static final int DAILY_CAP   = 5000;         // Max per day
    private int count = 0;
    private String game;
    private String dayRestriction;


    ReactivationCampaign(int priority, CampaignState activation, String game, String days){

        super(Name, priority, activation);
        this.game = game;
        this.dayRestriction = days;
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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {

        if(count > DAILY_CAP){

            System.out.println("    -- Campaign " + Name + " not firing. Daily cap reached for campaign." );
            return null;

        }

        if(playerInfo.getUsageProfile().isMobilePlayer()){

            System.out.println("    -- Campaign " + Name + " not firing. Not for mobile players" );
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

        if(inactivity > INACTIVITY_GIVEUP){

            System.out.println("    -- Campaign " + Name + " not firing. Too long inactivity" );
            return null;

        }

        if(inactivity > INACTIVITY_LIMIT){

            if(!isOkDay(playerInfo, executionTime)){

                System.out.println("    -- Campaign " + Name + " not firing. waiting for the right day..." );
                return null;


            }


            if(isHighSpender(user)){

                System.out.println("    -- Campaign " + Name + " firing message1. Creating bonus for player" );
                count++;
                return new NotificationAction("You have 20,000 free coins to play for! We haven't seen you in a while. There are some fabulous new games to try out. ",
                        user, executionTime, getPriority(), getTag(), Name, 1, getState(), responseFactor)
                        .withGame(game)
                        .withReward("cac6b086-189f-4ee6-bb30-7bcfb2a0ecfa");


            }else if(isPaying(user)){

                System.out.println("    -- Campaign " + Name + " firing message2. Creating bonus for player" );
                count++;
                return new NotificationAction("You have 10,000 free coins to play for. We haven't seen you in a while. There are some fabulous new games to try out. ",
                        user, executionTime, getPriority(), getTag(),  Name, 2, getState(), responseFactor)
                        .withGame(game)
                        .withReward("93f00dac-26cf-46e4-8bde-1eb59dd13032");

            }else if(user.sessions > MIN_ACTIVITY){

                System.out.println("    -- Campaign " + Name + " firing message3. Creating bonus for player" );
                count++;

                if(randomize2(user, 0)){

                    return new NotificationAction("You have got 10,000 extra free coins! We haven't seen you in a while and there are some fabulous new games to try out. Click here to claim ",
                            user, executionTime, getPriority(), getTag(),  Name, 3, getState(), responseFactor)
                            .withGame(game)
                            .withReward("93f00dac-26cf-46e4-8bde-1eb59dd13032");

                }

                return new NotificationAction("You have got 3,000 extra free coins! We haven't seen you in a while and there are some fabulous new games to try out. Click here to claim ",
                        user, executionTime, getPriority(), getTag(),  Name, 83, getState(), responseFactor)
                        .withGame(game)
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

    public String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {

        String specificWeekDay = isSpecificDay(executionTime, false, dayRestriction);

        if(specificWeekDay != null)
            return specificWeekDay;


        return isTooEarly(executionTime, overrideTime);

    }



}
