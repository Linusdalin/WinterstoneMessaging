package campaigns;

import action.ActionInterface;
import action.EmailAction;
import action.NotificationAction;
import core.PlayerInfo;
import email.EmailInterface;
import remoteData.dataObjects.User;

import java.sql.Timestamp;


/************************************************************************'
 *
 *                  This is a gender AB test witch cross permutation test
 *
 *                  MM, MF, FM, FF
 */

public class GameNotificationGenderAB extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "GameNotification";
    private static final int CoolDown_Days = 5;     // Avoid duplicate runs

    // Trigger specific config data
    private static final int INACTIVITY_LIMIT_FREE      = 15;   // Max days inactivity to get message
    private static final int INACTIVITY_LIMIT_PAYING    = 60;   // Max days inactivity to get message
    private static final int ACTIVITY_MIN   = 10;               // Min sessions to be active


    private static final String MessageM = "Our new game has more X’s than Liz Taylor. Click Now!";
    private static final String MessageF = "Old School Casino Classics 2x3x4x5x is a real trip down ‘Memory Strip’. Click here to go back in time!";

    private String gameCode;
    private final EmailInterface email = null;

    GameNotificationGenderAB(int priority, CampaignState activation){

        super(Name, priority, activation);
        this.gameCode = "os2x3x4x5x";
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


        //System.out.println("Registration Date: " + getDay(user.created).toString());
        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();


        if(user.sessions < ACTIVITY_MIN){

            System.out.println("    -- Campaign " + Name + " not active. Player has not been active enough ("+ user.sessions +" sessions <  " + ACTIVITY_MIN);
            return null;

        }

        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }
        int inactivity = getDaysBetween(lastSession, executionDay);

        if(user.payments == 0 && inactivity > INACTIVITY_LIMIT_FREE){

            System.out.println("    -- Campaign " + Name + " not active. Free player is inactive. ("+ inactivity+" days >  " + INACTIVITY_LIMIT_FREE);
            return null;

        }

        if(user.payments > 0 && inactivity > INACTIVITY_LIMIT_PAYING){

            System.out.println("    -- Campaign " + Name + " not active. Paying player is inactive. ("+ inactivity+" days >  " + INACTIVITY_LIMIT_PAYING);
            return null;

        }


        System.out.println("    -- Campaign " + Name + " firing. ");

        boolean abSelect = abSelect2(user);
        NotificationAction action = null;

        if(isMale(user)){

            if(abSelect){
                 action =  new NotificationAction(MessageF, user, getPriority(), getTag(), Name,  1, getState())
                        .withGame(gameCode);

            }
            else{

                action =  new NotificationAction(MessageM, user, getPriority(), getTag(), Name,  2, getState())
                        .withGame(gameCode);

            }
        }else{
                // Female player.

            if(abSelect){

                action =  new NotificationAction(MessageM, user, getPriority(), getTag(), Name,  3, getState())
                        .withGame(gameCode);

            }
            else{

                action =  new NotificationAction(MessageF, user, getPriority(), getTag(), Name,  4, getState())
                        .withGame(gameCode);

            }

        }

        return action;

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
