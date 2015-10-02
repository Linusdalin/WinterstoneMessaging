package campaigns;

import action.ActionInterface;
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
    private static final int INACTIVITY_LIMIT_FREE      = 17;   // Max days inactivity to get message
    private static final int INACTIVITY_LIMIT_PAYING    = 62;   // Max days inactivity to get message
    private static final int ACTIVITY_MIN   = 19;               // Min sessions to be active


    private static final String MessageM = "Fly it like a Fighter Pilot. Our new game Sonic Boom breaks the SlotAmerica sound barrier. Click to Play Now!";
    private static final String MessageF = "Not just a pretty face, show them you’re a Fighter Ace! Play our new barrier-breaking slot Sonic Boom now!";

    private String gameCode;



    GameNotificationGenderAB(int priority, CampaignState activation){

        super(Name, priority, activation);
        this.gameCode = "sonic_boom";
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



        boolean abSelect = abSelect2(user);
        NotificationAction action;

        if(isMale(user)){

            if(abSelect){
                 action =  new NotificationAction(MessageF, user, getPriority(), getTag(), Name,  1, getState())
                        .withGame(gameCode);
                System.out.println("    -- Campaign " + Name + " firing. (Testing Female message to Male audience) ");

            }
            else{

                action =  new NotificationAction(MessageM, user, getPriority(), getTag(), Name,  2, getState())
                        .withGame(gameCode);
                System.out.println("    -- Campaign " + Name + " firing. (Testing Male message to Male audience) ");

            }
        }else{
                // Female player.

            if(abSelect){

                action =  new NotificationAction(MessageM, user, getPriority(), getTag(), Name,  3, getState())
                        .withGame(gameCode);
                System.out.println("    -- Campaign " + Name + " firing. (Testing Male message to Female audience) ");

            }
            else{

                action =  new NotificationAction(MessageF, user, getPriority(), getTag(), Name,  4, getState())
                        .withGame(gameCode);
                System.out.println("    -- Campaign " + Name + " firing. (Testing Female message to Female audience) ");

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
