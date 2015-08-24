package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.User;

import java.sql.Timestamp;



/************************************************************************'
 *
 *              Reminding players about diamond clicks
 *              Sending message to players between min and max diamonds. (inclusive)
 */

public class RememberDiamondCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Remember Diamond";
    private static final int CoolDown_Days = 10;

    // Trigger specific config data
    private static final int MIN_DIAMONDS = 5;
    private static final int MAX_DIAMONDS = 11;

    RememberDiamondCampaign(int priority){

        super(Name, priority);
        setCoolDown(CoolDown_Days);

    }

    /********************************************************************
     *
     *              Decide on the campaign
     *
     *              The output could be one of 4 different messages depending on the day
     *
     * @param info             - the user to evaluate
     */


    public ActionInterface evaluate(PlayerInfo info, Timestamp executionTime) {

        User user = info.getUser();

        if(user.sessions <5){

            System.out.println("    -- Campaign " + Name + " not checking. The user has not played enough" );
            return null;
        }

        if(user.nextNumberOfPicks < MIN_DIAMONDS){

            System.out.println("    -- Campaign " + Name + " not checking. The user has only " + user.nextNumberOfPicks + " diamond picks. Less than " + MIN_DIAMONDS );
            return null;
        }

        if(user.nextNumberOfPicks > MAX_DIAMONDS){

            System.out.println("    -- Campaign " + Name + " not checking. The user has already " + user.nextNumberOfPicks + " diamond picks. More than " + MAX_DIAMONDS );
            return null;
        }


        GameSession lastSession = info.getLastSession();

        if(lastSession == null){

            // This should not really happen. The sessions is greater than 5 as of above
            System.out.println("    -- Campaign " + Name + " not checking. The user has not played enough" );
            return null;

        }

        int inactivity = getDaysBetween(lastSession.timeStamp, executionTime);


        if(!hoursBefore(lastSession.timeStamp, executionTime, 24) || hoursBefore(lastSession.timeStamp, executionTime, 42)) {

            System.out.println("    -- Campaign " + Name + " not firing. Not in time range." );
            return null;


        }

        // Last session was Between 24 and 42 hours ago and diamond pick is correct. Send the message

        System.out.println("    -- Campaign " + Name + " fire notification" );
        return new NotificationAction("Don't forget your diamond pick today, it will soon expire! The 15 day bonus is waiting! Click here to claim it", user, getPriority(), createTag(Name), createPromoCode(Name, user, inactivity), Name);


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
