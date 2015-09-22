package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;
import rewards.RewardRepository;

import java.sql.Timestamp;


/************************************************************************'
 *
 *          Sending a message to players in the progress to kep them going
 *
 *          //TODO: We want to be able to repeat the message n times provided there is no response
 *
 *
 */

public class ActivationFreeCoinCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "ActivationCoin";
    private static final int CoolDown_Days = 36500;             // Only once

    // Trigger specific config data
    private static final int Min_Sessions = 3;
    private static final int Max_Sessions = 25;
    private static final int Min_Age = 12;
    private static final int Max_Age = 20;

    private static final int IdleDays = 5;

    private static final int DAILY_CAP   = 100;         // Max per day
    private int count = 0;

    ActivationFreeCoinCampaign(int priority, CampaignState active){

        super(Name, priority, active);
        setCoolDown(CoolDown_Days);
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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime) {

        count++;

        if(count > DAILY_CAP){

            System.out.println("    -- Campaign " + Name + " not firing. Daily cap reached for campaign." );
            return null;

        }


        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        if(user.sessions < Min_Sessions){

            System.out.println("    -- Campaign " + Name + " not applicable. User is not frequent enough (" + user.sessions + " < " + Min_Sessions + ")" );
            return null;

        }

        if(user.sessions > Max_Sessions){

            System.out.println("    -- Campaign " + Name + " not applicable. User is already active (" + user.sessions + " > " + Max_Sessions + ")" );
            return null;

        }


        if(getDaysBetween(user.created, executionDay) < Min_Age){

            System.out.println("    -- Campaign " + Name + " not firing. Player not old enough (created: " + user.created );
            return null;

        }

        if(getDaysBetween(user.created, executionDay) > Max_Age){

            System.out.println("    -- Campaign " + Name + " not firing. Player too old (created: " + user.created );
            return null;

        }



        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }

        if(getDaysBetween(lastSession, executionDay) > IdleDays){

            System.out.println("    -- Sending a day "+ IdleDays+" activation poke with coins" );

            if(isPaying(user)){

                return new NotificationAction( user.name +", We have added "+ RewardRepository.freeCoinAcitivationPaying.getCoins()+" coins extra on top of the bonus for you to play with on your account. Click here to collect and play!",
                        user, getPriority(), getTag(),  Name, 1, getState())
                        .withReward(RewardRepository.freeCoinAcitivationPaying);



            }
            else{

                return new NotificationAction( user.name +", We have added "+ RewardRepository.freeCoinAcitivationFree.getCoins()+" free coins for you to play with on your account. Click here to collect and play!",
                        user, getPriority(), getTag(),  Name, 1, getState())
                        .withReward(RewardRepository.freeCoinAcitivationFree);



            }


        }




        System.out.println("    -- Campaign " + Name + " not firing. Not the right day. (last:" + lastSession.toString() );
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
