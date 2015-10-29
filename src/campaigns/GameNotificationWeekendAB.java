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
 *                   - All the thursday players will get the message on thursday ( 7 )
 *                   - half the friday, saturday and sunday players will get the message on the correct day ( 8, 9, 10 )
 *                   - The other half of the players will get the message on thursday (as a reference) ( 11 )
 *                   - All other players will get the message randomly on friday - sunday to test the days themselves and a reference to group 8, 9, 10 ( 12, 13, 14 )
 *
 */

public class GameNotificationWeekendAB extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "GameNotification";
    private static final int CoolDown_Days = 5;     // Avoid duplicate runs
    private static final int[] MessageIds = { 1, 7, 8, 9, 10, 11, 12, 13, 14 };


    private static final String Day1 = "torsdag";   // Swedish due to locale on test computer
    private static final String Day2 = "fredag";   // Swedish due to locale on test computer
    private static final String Day3 = "lördag";   // Swedish due to locale on test computer
    private static final String Day4 = "söndag";   // Swedish due to locale on test computer


    private static final int INACTIVITY_LIMIT_FREE      = 18;   // Max days inactivity to get message
    private static final int INACTIVITY_LIMIT_PAYING    = 65;   // Max days inactivity to get message
    private static final int ACTIVITY_MIN   = 18;               // Min sessions to be active

    private String gameCode;
    private Reward reward;
    private String message;


    GameNotificationWeekendAB(int priority, CampaignState activation, String game_code, String message, Reward reward){

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

        String thursdayRestriction  = isSpecificDay(executionTime, false, Day1);
        String fridayRestriction    = isSpecificDay(executionTime, false, Day2);
        String saturdayRestriction  = isSpecificDay(executionTime, false, Day3);
        String sundayRestriction    = isSpecificDay(executionTime, false, Day4);

        ReceptivityProfile profileForPlayer =  playerInfo.getReceptivityForPlayer( );
        int favouriteDay = profileForPlayer.getFavouriteDay(ReceptivityProfile.SignificanceLevel.GENERAL);

        NotificationAction action;

        if(thursdayRestriction == null){

            action = handleThursday(playerInfo, favouriteDay, responseFactor);

        }else if(fridayRestriction == null){

            action = handleFriday(playerInfo, favouriteDay, responseFactor);

        } else if(saturdayRestriction == null){

            action = handleSaturday(playerInfo, favouriteDay, responseFactor);

        }else if(sundayRestriction == null){

            action = handleSunday(playerInfo, favouriteDay, responseFactor);

        }else{

            // It is another day

            System.out.println("    -- Campaign (Sat/Sun)" + Name + " firing. (Sending a notification on a regular day)");
            action = new NotificationAction(message, user, getPriority(), getTag(), Name,  7, getState(), responseFactor)
                    .withGame(gameCode);


        }

        if(action != null && reward != null)
            action.withReward(reward);
        return action;
    }

    /******************************************************************************************
     *
     *              On the thursday, we will send messages to
     *               - thursday players and
     *               - half of the friday, saturday and sunday players    (not abselect1)
     *
     *
     *
     * @param playerInfo              - info
     * @param favouriteDay            - players favourite day (or -1 as undefined)
     * @param responseFactor          - the responses for the player
     * @return                        - a potential action
     */


    private NotificationAction handleThursday(PlayerInfo playerInfo, int favouriteDay, double responseFactor) {

        if(favouriteDay == 4){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on thursday for thursday players " + playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), getPriority(), getTag(), Name,  7, getState(), responseFactor)
                    .withGame(gameCode);

        }else if(favouriteDay == 5 || favouriteDay == 6 || favouriteDay == 0){

            if(!abSelect1(playerInfo.getUser())){

            // The other half
            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on thursday for fri/sat/sun players as reference "+ favouriteDay + "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), getPriority(), getTag(), Name,  11, getState(), responseFactor)
                    .withGame(gameCode);

            }else{

                System.out.println("    -- Campaign (weekend)" + Name + " not firing. Saving weekend players to the weekend " + favouriteDay+ "  " +playerInfo.getReceptivityForPlayer().toString());
                return null;

            }
        }else if(favouriteDay == -1){

            System.out.println("    -- Campaign (weekend)" + Name + " not firing for unspecific players "+ favouriteDay+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return null;

        }
        else{

            System.out.println("    -- Campaign (weekend)" + Name + " not firing. On a Thursday we only send messages to Thursday players (and some weekend players for reference) "+ favouriteDay+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return null;

        }
    }

    private NotificationAction handleFriday(PlayerInfo playerInfo, int favouriteDay, double responseFactor) {

        if(favouriteDay == 5 && abSelect1(playerInfo.getUser())){

            // The other half
            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a friday to a friday player"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), getPriority(), getTag(), Name,  8, getState(), responseFactor)
                    .withGame(gameCode);

        }

        if(favouriteDay != 4 && favouriteDay != 5 &&favouriteDay != 6 &&favouriteDay != 0 && randomize3(playerInfo.getUser(), 0)){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification to other player "+ favouriteDay+")"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), getPriority(), getTag(), Name,  12, getState(), responseFactor)
                    .withGame(gameCode);

        }


        System.out.println("    -- Campaign (weekend)" + Name + " not firing. It is Friday"+ "  " +playerInfo.getReceptivityForPlayer().toString());
        return null;
    }

    private NotificationAction handleSaturday(PlayerInfo playerInfo, int favouriteDay, double responseFactor) {

        if(favouriteDay == 6 && abSelect1(playerInfo.getUser())){

            // The other half
            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a saturday to a saturday player"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), getPriority(), getTag(), Name,  9, getState(), responseFactor)
                    .withGame(gameCode);

        }

        if(favouriteDay != 4 && favouriteDay != 5 &&favouriteDay != 6 &&favouriteDay != 0 && randomize3(playerInfo.getUser(), 1)){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification to other player "+favouriteDay+")"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), getPriority(), getTag(), Name,  13, getState(), responseFactor)
                    .withGame(gameCode);

        }

        System.out.println("    -- Campaign (weekend)" + Name + " not firing. It is Saturday"+ "  " +playerInfo.getReceptivityForPlayer().toString());
        return null;
    }


    private NotificationAction handleSunday(PlayerInfo playerInfo, int favouriteDay, double responseFactor) {

        if(favouriteDay == 0 && abSelect1(playerInfo.getUser())){

            // The other half
            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a sunday to sunday player"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), getPriority(), getTag(), Name,  10, getState(), responseFactor)
                    .withGame(gameCode);


        }

        if(favouriteDay != 4 && favouriteDay != 5 &&favouriteDay != 6 &&favouriteDay != 0 && randomize3(playerInfo.getUser(), 2)){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification to other player "+ favouriteDay+")"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), getPriority(), getTag(), Name,  14, getState(), responseFactor)
                    .withGame(gameCode);

        }


        System.out.println("    -- Campaign (weekend)" + Name + " not firing. It is Sunday "+ favouriteDay +"  " +playerInfo.getReceptivityForPlayer().toString());
        return null;
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
