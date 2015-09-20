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
 *                  Old players
 */

public class GameNotification extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "GameNotification";
    private static final int CoolDown_Days = 5;     // Avoid duplicate runs

    // Trigger specific config data
    private static final int INACTIVITY_LIMIT_FREE      = 15;   // Max days inactivity to get message
    private static final int INACTIVITY_LIMIT_PAYING    = 60;   // Max days inactivity to get message
    private static final int ACTIVITY_MIN   = 10;               // Min sessions to be active


    private final String message;
    private String gameCode;
    private final EmailInterface email;
    private String reward;

    GameNotification(int priority, CampaignState activation, String gameCode, String message, EmailInterface email, String code){

        super(Name, priority, activation);
        this.gameCode = gameCode;
        this.email = email;
        this.reward = code;
        setCoolDown(CoolDown_Days);
        this.message = message;
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

        // First generate a generic email action

        ActionInterface emailAction = null;

        if(email != null)
            emailAction = new EmailAction(email, user, getPriority(), Name,  1, getState());


        if(user.sessions < ACTIVITY_MIN){

            System.out.println("    -- Campaign " + Name + " not active. Player has not been active enough ("+ user.sessions +" sessions <  " + ACTIVITY_MIN);
            return emailAction;

        }

        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return emailAction;

        }
        int inactivity = getDaysBetween(lastSession, executionDay);

        if(user.payments == 0 && inactivity > INACTIVITY_LIMIT_FREE){

            System.out.println("    -- Campaign " + Name + " not active. Free player is inactive. ("+ inactivity+" days >  " + INACTIVITY_LIMIT_FREE);
            return emailAction;

        }

        if(user.payments > 0 && inactivity > INACTIVITY_LIMIT_PAYING){

            System.out.println("    -- Campaign " + Name + " not active. Paying player is inactive. ("+ inactivity+" days >  " + INACTIVITY_LIMIT_PAYING);
            return emailAction;

        }


        System.out.println("    -- Campaign " + Name + " firing. ");

        NotificationAction action =  new NotificationAction(message, user, getPriority(), getTag(), Name,  1, getState())
                .withGame(gameCode);

        if(reward != null)
            action.withReward(reward);

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
