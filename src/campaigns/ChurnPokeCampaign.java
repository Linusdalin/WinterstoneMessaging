package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.User;

import java.sql.Timestamp;


/************************************************************************'
 *
 *              Sending a message to players that stop playing
 *
 *              This is a simple first take, sending a message 3 to all players that have played at lease 10 sessions and then stopped
 *
 */

public class ChurnPokeCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "ChurnPoke";
    private static final int CoolDown_Days = 7;

    // Trigger specific config data
    private static final int Min_Sessions = 8;

    ChurnPokeCampaign(int priority, CampaignState active){

        super(Name, priority, active);
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

        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        if(user.sessions < Min_Sessions){

            System.out.println("    -- Campaign " + Name + " not applicable. User is not frequent enough (" + user.sessions + " < " + Min_Sessions + ")" );
            return null;

        }

        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }

        if(isDaysBefore(lastSession, executionDay, 3)){


            System.out.println("    -- Sending a three day churn warning poke" );
            return new NotificationAction("Hello, you have new bonuses to collect at Slot America. Click here for more free slot FUN!",
                    user, getPriority(), getTag(),  Name, 3, getState());


        }
        else if(isDaysBefore(lastSession, executionDay, 8)){


            System.out.println("    -- Sending a EIGHT day churn warning poke" );
            return new NotificationAction("Hello, don't miss out the latest slot game release at SlotAmerica. Click here to check it out!",
                    user, getPriority(), getTag(),  Name,  8, getState());


        }
        else{

            System.out.println("    -- Campaign " + Name + " not firing. Not three day churn warning (last:" + lastSession.toString() );
            return null;

        }




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
