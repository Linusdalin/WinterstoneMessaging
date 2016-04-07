package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import receptivity.ReceptivityProfile;
import remoteData.dataObjects.User;
import response.ResponseStat;

import java.sql.Timestamp;


/************************************************************************'
 *
 *              Reminding players about diamond clicks
 *              Sending message to players between min and max diamonds. (inclusive)
 */

public class RememberDiamondMorningCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "RememberDiamondMorning";
    private static final int CoolDown_Days = 6;
    private static final int[] MessageIds = {  };

    // Trigger specific config data
    private static final int MIN_DIAMONDS = 8;             // Only testing with high frequency clickthrough (many diamonds)
    private static final int MAX_DIAMONDS = 14;

    private static final int MIN_SESSIONS = 10;



    RememberDiamondMorningCampaign(int priority, CampaignState activation){

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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {

        User user = playerInfo.getUser();
        //System.out.println(" --- Responses: " + response.toString());

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


        // Last session was Between 37 and 42 hours ago and diamond pick is correct. Send the message

        if(!hoursBefore(lastSession, executionTime, 37) || hoursBefore(lastSession, executionTime, 44)) {

            System.out.println("    -- Campaign " + Name + " not firing. Not in time range." );
            return null;


        }

        if(playerInfo.getUsageProfile().isMobilePlayer()){

            System.out.println("    -- Campaign " + Name + " not firing for mobile player. This is handled automatically" );
            return null;
        }

        int messageId = 1;       // Default (no specific time of day)

        if(user.nextNumberOfPicks > 10)
            messageId = 2;

        if(playerInfo.getReceptivityForPlayer().getFavouriteTimeOfDay(ReceptivityProfile.SignificanceLevel.SPECIFIC) == ReceptivityProfile.DAY)
            messageId += 10;
        if(playerInfo.getReceptivityForPlayer().getFavouriteTimeOfDay(ReceptivityProfile.SignificanceLevel.SPECIFIC) == ReceptivityProfile.EVENING)
            messageId += 20;
        if(playerInfo.getReceptivityForPlayer().getFavouriteTimeOfDay(ReceptivityProfile.SignificanceLevel.SPECIFIC) == ReceptivityProfile.NIGHT)
            messageId += 30;




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

    public String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {


        String timeCheck = isTooLate(executionTime, overrideTime);

        return timeCheck;

    }


}
