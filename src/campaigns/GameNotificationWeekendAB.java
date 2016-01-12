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
    private static final int[] MessageIds = { 1, 2, 3, 4, 5 , 6, 7, 8, 9, 10, 11, 12, 13};


    private static final String Day1 = "torsdag";   // Swedish due to locale on test computer
    private static final String Day2 = "fredag";   // Swedish due to locale on test computer
    private static final String Day3 = "lördag";   // Swedish due to locale on test computer
    private static final String Day4 = "söndag";   // Swedish due to locale on test computer


    private static final int INACTIVITY_LIMIT_FREE      = 17;   // Max days inactivity to get message
    private static final int INACTIVITY_LIMIT_PAYING    = 90;   // Max days inactivity to get message
    private static final int ACTIVITY_MIN   = 12;               // Min sessions to be active

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

        if(playerInfo.getUsageProfile().isAnonymousMobile()){

            System.out.println("    -- Campaign " + Name + " not firing. Mobile anonymous player should not have facebook message");
            return null;
        }


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

        // Automatically expand base when there is a reward.

        int maxInactivityFree = INACTIVITY_LIMIT_FREE;
        int maxInactivityPaying = INACTIVITY_LIMIT_PAYING;

        if(reward != null){

            maxInactivityFree += 20;
            maxInactivityPaying += 10;

        }


        if(user.payments == 0 && inactivity > maxInactivityFree){

            System.out.println("    -- Campaign " + Name + " not active. Free player is inactive. ("+ inactivity+" days >  " + maxInactivityFree);
            return null;

        }

        if(user.payments > 0 && inactivity > maxInactivityPaying){

            System.out.println("    -- Campaign " + Name + " not active. Paying player is inactive. ("+ inactivity+" days >  " + maxInactivityPaying);
            return null;

        }

        String thursdayRestriction  = isSpecificDay(executionTime, false, Day1);
        String fridayRestriction    = isSpecificDay(executionTime, false, Day2);
        String saturdayRestriction  = isSpecificDay(executionTime, false, Day3);
        String sundayRestriction    = isSpecificDay(executionTime, false, Day4);

        ReceptivityProfile profileForPlayer =  playerInfo.getReceptivityForPlayer( );
        int favouriteDay = profileForPlayer.getFavouriteDay(ReceptivityProfile.SignificanceLevel.SPECIFIC);

        NotificationAction action;

        if(thursdayRestriction == null){

            action = handleThursday(playerInfo, favouriteDay, responseFactor, executionTime);

        }else if(fridayRestriction == null){

            action = handleFriday(playerInfo, favouriteDay, responseFactor, executionTime);

        } else if(saturdayRestriction == null){

            action = handleSaturday(playerInfo, favouriteDay, responseFactor, executionTime);
            //action = handleSaturdaySpecial(playerInfo, favouriteDay, responseFactor, executionTime);

        }else if(sundayRestriction == null){

            action = handleSunday(playerInfo, favouriteDay, responseFactor, executionTime);

        }else{

            // It is another day

            System.out.println("    -- Campaign (Sat/Sun)" + Name + " firing. (Sending a notification on a regular day)");
            action = new NotificationAction(message, user, executionTime, getPriority(), getTag(), Name,  1, getState(), responseFactor)
                    .withGame(gameCode);


        }

        if(action != null && reward != null)
            action.withReward(reward);
        return action;
    }

    /******************************************************************************************
     *
     *              On the thursday, we will send messages only to
     *               - thursday players
     *
     *
     *
     * @param playerInfo              - info
     * @param favouriteDay            - players favourite day (or -1 as undefined)
     * @param responseFactor          - the responses for the player
     * @return                        - a potential action
     */


    private NotificationAction handleThursday(PlayerInfo playerInfo, int favouriteDay, double responseFactor, Timestamp executionTime) {


        if(favouriteDay == ReceptivityProfile.Not_significant ){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a Thursday to a player without specific day"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  2, getState(), responseFactor)
                    .withGame(gameCode);

        }

        if(favouriteDay == 4){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on thursday for thursday players " + playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime,  getPriority(), getTag(), Name,  3, getState(), responseFactor)
                    .withGame(gameCode);

        }
        else{

            System.out.println("    -- Campaign (weekend)" + Name + " not firing. On a Thursday we only send messages to Thursday players (and some weekend players for reference) "+ favouriteDay+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return null;

        }
    }

    /********************************************************************************************************************************'
     *
     *          On a friday we are sending a message to:
     *
     *           - friday players
     *           - thursday players (if they were missed or cooling down on thursday
     *
     *
     * @param playerInfo
     * @param favouriteDay
     * @param responseFactor
     * @param executionTime
     * @return
     */

    private NotificationAction handleFriday(PlayerInfo playerInfo, int favouriteDay, double responseFactor, Timestamp executionTime) {


        if(favouriteDay == ReceptivityProfile.Not_significant ){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a Friday to a player without specific day"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  4, getState(), responseFactor)
                    .withGame(gameCode);

        }


        if(favouriteDay == 1  || favouriteDay == 2  || favouriteDay == 3 ){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a friday to a weekday player"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  5, getState(), responseFactor)
                    .withGame(gameCode);

        }


        if(favouriteDay == 5){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a friday to a friday player"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  6, getState(), responseFactor)
                    .withGame(gameCode);

        }

        if(favouriteDay == 4){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a friday to a MISSED thursday player"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  7, getState(), responseFactor)
                    .withGame(gameCode);

        }

        if(favouriteDay == 6){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a friday to a saturday player"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  8, getState(), responseFactor)
                    .withGame(gameCode);

        }


        System.out.println("    -- Campaign (weekend)" + Name + " not firing. It is Friday"+ "  " +playerInfo.getReceptivityForPlayer().toString());
        return null;
    }

    /*********************
     *
     *          Handle saturday. This is not a good day to send notifications, so we don't
     *
     * @param playerInfo
     * @param favouriteDay
     * @param responseFactor
     * @param executionTime
     * @return
     */

    private NotificationAction handleSaturday(PlayerInfo playerInfo, int favouriteDay, double responseFactor, Timestamp executionTime) {

        if(favouriteDay == ReceptivityProfile.Not_significant ){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a Saturday to a player without specific day"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  9, getState(), responseFactor)
                    .withGame(gameCode);

        }


        System.out.println("    -- Campaign (weekend)" + Name + " not firing. No messages at all on Saturday"+ "  " +playerInfo.getReceptivityForPlayer().toString());
        return null;

    }


    private NotificationAction handleSunday(PlayerInfo playerInfo, int favouriteDay, double responseFactor, Timestamp executionTime) {


        if(favouriteDay == 0 ){

            // The other half
            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a sunday to sunday player"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  10, getState(), responseFactor)
                    .withGame(gameCode);


        }

        System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification to ANY player not addressed yet "+ favouriteDay+")"+ "  " +playerInfo.getReceptivityForPlayer().toString());
        return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  11, getState(), responseFactor)
                .withGame(gameCode);


    }

    private NotificationAction handleSaturdaySpecial(PlayerInfo playerInfo, int favouriteDay, double responseFactor, Timestamp executionTime) {

        if(favouriteDay == 6 ){

            // The other half
            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a saturday to a saturday player"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  12, getState(), responseFactor)
                    .withGame(gameCode);


        }

        System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification to a saturday to ANY player not addressed yet "+ favouriteDay+")"+ "  " +playerInfo.getReceptivityForPlayer().toString());
        return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  13, getState(), responseFactor)
                .withGame(gameCode);


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
