package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;

import java.sql.Timestamp;


/************************************************************************'
 *
 *              For ongoing happy hour campaigns send info to paying players
 */

public class HappyHourCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Happy Hour";
    private static final int CoolDown_Days = 9;            // A bit more than a week to avoid getting it every day

    // Trigger specific config data
    private static final int MAX_INACTIVITY = 18;

    HappyHourCampaign(int priority, CampaignState activation){

        super(Name, priority, activation);
        setCoolDown(CoolDown_Days);

    }

    /********************************************************************
     *
     *              Decide on the campaign
     *
     *              The output could be one of 4 different messages depending on the day
     *
     * @param playerInfo             - the user to evaluate
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime) {

        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        if(user.payments == 0){

            System.out.println("    -- Campaign " + Name + " not firing. Only real money players" );
            return null;
        }


        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }

        int inactivity = getDaysBetween(lastSession, executionDay);

        if(inactivity > MAX_INACTIVITY){

            System.out.println("    -- Campaign " + Name + " not firing. Yser has been inactive too long. (" + inactivity + " days )" );
            return null;

        }


        System.out.println("    -- Sending a happy hour reminder" );
        return new NotificationAction("Hello, It is now happy hour at SlotAmerica with 25% extra on all coin purchases. Click here to join the thrill!!",
                user, getPriority(), createTag(Name),  Name, 3, getState());


    }


    /*********************************************************************
     *
     *              Campaign timing restrictions
     *
     * @param executionTime     - time of execution
     * @return                  - messgage or null if ok.
     */

    public String testFailCalendarRestriction(Timestamp executionTime, boolean overrideTime) {

        /*
        String specificWeekDay = isSpecificDay(executionTime, dryRun, "måndag");

        if(specificWeekDay != null)
            return specificWeekDay;

        */

        String tooEarlyCheck = isTooEarly(executionTime, overrideTime);

        return tooEarlyCheck;

    }


}
