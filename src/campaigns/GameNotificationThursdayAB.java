package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import receptivity.ReceptivityProfile;
import remoteData.dataObjects.User;
import rewards.Reward;

import java.sql.Timestamp;


/************************************************************************'
 *
 *                  This is a AB test for sending the notification on the correct day
 *
 *                  It will target Thursday-players and send the notification to half of
 *                  them on the thursday and half on another day
 *
 *                  This campaign will not handle sending messages to other players, so this should be
 *                  run together with the normal GameNotification campaign, that will add all other players.
 *
 */

public class GameNotificationThursdayAB extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "GameNotification";
    private static final int CoolDown_Days = 5;     // Avoid duplicate runs
    private static final int[] MessageIds = { 5, 6 };


    private static final String Day = "torsdag";   // Swedish due to locale on test computer


    private static final int INACTIVITY_LIMIT_FREE      = 17;   // Max days inactivity to get message
    private static final int INACTIVITY_LIMIT_PAYING    = 62;   // Max days inactivity to get message
    private static final int ACTIVITY_MIN   = 19;               // Min sessions to be active

    private String gameCode;
    private Reward reward;
    private String message;


    GameNotificationThursdayAB(int priority, CampaignState activation, String game_code, String message, Reward reward){

        super(Name, priority, activation);
        this.gameCode = game_code;
        this.reward = reward;
        this.message = message;
        setCoolDown(CoolDown_Days);
        registerMessageIds( MessageIds );
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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor) {


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




        String specificDayRestriction = isSpecificDay(executionTime, false, Day);

        NotificationAction action = null;

        if(specificDayRestriction == null){

            // It is Thursday

            ReceptivityProfile profileForPlayer =  playerInfo.getReceptivityForPlayer( );

            if(profileForPlayer.getFavouriteDay(ReceptivityProfile.SignificanceLevel.SPECIFIC) == 4){

                // Specifically a thursday player. In 50% of the cases, send a message today.

                if(abSelect1(user)){

                    System.out.println("    -- Campaign (Thursday)" + Name + " firing. (Sending a notification on Thursday to a Thursday player");
                    action = new NotificationAction(message, user, getPriority(), getTag(), Name,  5, getState(), responseFactor)
                            .withGame(gameCode);

                }
                else{

                    System.out.println("    -- Campaign (Thursday)" + Name + " not firing. (Ignoring a Thursday player for split test");
                    return null;

                }

            }
            else{

                System.out.println("    -- Campaign (Thursday)" + Name + " not firing. (Not a Thursday player");
                return null;

            }

        }
        else{

            // It is not thursday. Try a message to the thursday players

            System.out.println(" !! Restriction: " + specificDayRestriction);

            ReceptivityProfile profileForPlayer =  playerInfo.getReceptivityForPlayer( );

            if(profileForPlayer.getFavouriteDay(ReceptivityProfile.SignificanceLevel.SPECIFIC) == 4){      //  TODO: Use specific. This is just for testing volume

                // Specifically a thursday player. In 50% of the cases, send a message today.

                if(!abSelect1(user)){

                    System.out.println("    -- Campaign (Thursday)" + Name + " firing. (Sending a notification on another day to the rest of the Thursday players");
                    action = new NotificationAction(message, user, getPriority(), getTag(), Name,  6, getState(), responseFactor)
                            .withGame(gameCode);

                }
                else{

                    System.out.println("    -- Campaign (Thursday)" + Name + " not firing. (Ignoring a Thursday player that already got the message");
                    return null;

                }

            }
            else{

                System.out.println("    -- Campaign (Thursday)" + Name + " not firing. (Not a Thursday player!");
                return null;

            }
        }

        if(action != null && reward != null)
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
