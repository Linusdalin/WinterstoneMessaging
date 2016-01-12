package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
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
    private static final int CoolDown_Days = 9;
    private static final int[] MessageIds = {   2,   3,   4,
                                               32,  33,  34
    };


    // Trigger specific config data
    private static final int MIN_DIAMONDS = 5;
    private static final int MAX_DIAMONDS = 13;

    private static final int MIN_SESSIONS = 5;



    RememberDiamondCampaign(int priority, CampaignState activation){

        super(Name, priority, activation);
        setCoolDown(CoolDown_Days);
        registerMessageIds( MessageIds );
    }

    /********************************************************************
     *
     *              Decide on the campaign
     *
     *              The output could be one of 4 different messages depending on the day
     *
     * @param playerInfo             - the user to evaluate
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor) {

        User user = playerInfo.getUser();

        if(user.sessions < MIN_SESSIONS){

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


        Timestamp lastSession = playerInfo.getLastSession();

        if(lastSession == null){

            // This should not really happen. The sessions is greater than 5 as of above
            System.out.println("    -- Campaign " + Name + " not checking. The user has not played enough" );
            return null;

        }


        if(!hoursBefore(lastSession, executionTime, 24) || hoursBefore(lastSession, executionTime, 44)) {

            System.out.println("    -- Campaign " + Name + " not firing. Not in time range." );
            return null;


        }

        int messageId = 3;

        if(user.nextNumberOfPicks < 7)
            messageId = 2;
        if(user.nextNumberOfPicks > 9)
            messageId = 4;

        // Last session was Between 24 and 42 hours ago and diamond pick is correct. Send the message


        if(playerInfo.getUsageProfile().isMobilePlayer()){

            System.out.println("    -- Campaign " + Name + " not firing for mobile player. This is handled automatically" );
            return null;
        }

        System.out.println("    -- Campaign " + Name + " fire notification" );
        return new NotificationAction("Don't forget your diamond pick today, it will soon expire! The 15 day bonus is waiting! Click here to claim it",
                user, executionTime, getPriority(), getTag(), Name, messageId, getState(), responseFactor);


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
