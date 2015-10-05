package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;

import java.sql.Timestamp;


/************************************************************************'
 *
 *              Sending a message to players early in the progress to get them going
 *
 *          Sending a 2 day activation poke and a 7 day activation poke with a
 *          8 day cool down will ensure that the message is only sent once per player
 */

public class ActivationPokeCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "ActivationPoke";
    private static final int CoolDown_Days = 8;

    // Trigger specific config data
    private static final int Min_Sessions = 3;
    private static final int Max_Sessions = 12;
    private static final int Max_Age = 12;

    ActivationPokeCampaign(int priority, CampaignState active){

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

        if(getDaysBetween(user.created, executionDay) > Max_Age){

            System.out.println("    -- Campaign " + Name + " not firing. Player too old (created: " + user.created );
            return null;

        }

        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }

        if(isDaysBefore(lastSession, executionDay, 2)){


            System.out.println("    -- Sending a day two activation poke" );
            return new NotificationAction("You haven't missed the level up bonuses at SlotAmerica? Check out the rewards by clicking on the level bar.!",
                    user, getPriority(), getTag(),  Name, 2, getState());


        }


        if(isDaysBefore(lastSession, executionDay, 7)){


            System.out.println("    -- Sending a seven day activation poke" );
            return new NotificationAction("Let's stat climbing the reward stairs at SlotAmerica? Check out the rewards by clicking on the level bar.!",
                    user, getPriority(), getTag(),  Name, 3, getState());


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
