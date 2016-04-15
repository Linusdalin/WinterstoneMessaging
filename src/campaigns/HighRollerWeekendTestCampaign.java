package campaigns;

import action.ActionInterface;
import action.TriggerEventAction;
import core.PlayerInfo;
import events.EventRepository;
import remoteData.dataObjects.User;
import response.ResponseStat;

import java.sql.Timestamp;


/************************************************************************'
 *
 *              Sending a message to players that stop playing
 *
 *              This is a simple first take, sending a message 3 to all players that have played at lease 10 sessions and then stopped
 *
 */

public class HighRollerWeekendTestCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "HighRollerWeekend";
    private static final int CoolDown_Days = 365000;


    // Trigger specific config data
    private static final int Min_Sessions = 1;
    private static final int TARGET_AGE = 6;             // The whole week

    HighRollerWeekendTestCampaign(int priority, CampaignState active){

        super(Name, priority, active);
        setCoolDown(CoolDown_Days);
    }


    /********************************************************************
     *
     *              Decide on the campaign
     *
     *
     *
     * @param playerInfo             - the user to evaluate
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {

        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        int age = getDaysBetween(user.created, executionDay);

        // Now running all new players in the week

        if( age > TARGET_AGE){

            System.out.println("    -- Campaign " + Name + " not firing. Not right day. (Age = " + age + ")");
            return null;

        }

        if(user.payments > 0){

            System.out.println("    -- Campaign " + Name + " not firing. Payer has already paid");
            return null;
        }

        if(user.sessions < Min_Sessions){

            System.out.println("    -- Campaign " + Name + " not applicable. User is not frequent enough (" + user.sessions + " < " + Min_Sessions + ")" );
            return null;

        }

        if(user.group.equals("A") || user.group.equals("D")){

            System.out.println("    -- Campaign " + Name + " not firing for group A or D. Split testing");
            return null;

        }



        System.out.println("    -- Campaign" + Name + " firing. HighRoller weekend test");

        ActionInterface triggerAction = new TriggerEventAction(EventRepository.Conversion1, 48, user, executionTime, getPriority(), Name, 1, getState(), responseFactor);

        return triggerAction;
    }


    public String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {

        return null;  // No restriction. This can be started any time

    }



}
