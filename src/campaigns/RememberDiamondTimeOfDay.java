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

public class RememberDiamondTimeOfDay extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Remember Diamond";
    private static final int CoolDown_Days = 9;
    private static final int[] MessageIds = {   12    };


    // Trigger specific config data
    private static final int MIN_DIAMONDS = 5;
    private static final int MAX_DIAMONDS = 14;

    private static final int MIN_SESSIONS = 20;



    RememberDiamondTimeOfDay(int priority, CampaignState activation){

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


        if(!hoursBefore(lastSession, executionTime, 34) || hoursBefore(lastSession, executionTime, 44)) {

            System.out.println("    -- Campaign " + Name + " not firing. Not in time range." );
            return null;


        }

        if(playerInfo.getReceptivityForPlayer().getFavouriteTimeOfDay(ReceptivityProfile.SignificanceLevel.SPECIFIC) != ReceptivityProfile.NIGHT){

            System.out.println("    -- Campaign " + Name + " not firing. Not a night player." );
            return null;

        }


        return new NotificationAction("Don't forget your diamond pick today, it will soon expire! The 15 day diamond bonus is waiting! Click here to make your daily pick",
                user, executionTime, getPriority(), getTag(), Name, 12, getState(), responseFactor);


    }


    /*********************************************************************
     *
     *              Campaign timing restrictions
     *
     * @param executionTime     - time of execution
     * @return                  - messgage or null if ok.
     */

    public String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {

        if(true || executionTime.getHours() > 10 || executionTime.getHours() < 6)
                return "Only for testing in the morning";
            else{

                return null;

            }

    }


}
