package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import receptivity.ReceptivityProfile;
import remoteData.dataObjects.User;
import response.ResponseStat;
import rewards.Reward;

import java.sql.Timestamp;


/************************************************************************'
 *
 *                             Thursday:    All Thursday players and players with an undefined day
 *
 */

public class GameNotificationWeekendAB extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "GameNotification";
    private static final int CoolDown_Days = 4;     // Avoid duplicate runs     TODO: Should be 5
    private static final int[] MessageIds = { };

    private static final int DAILY_CAP   = 4000;         // Max per execution
    private int count = 0;


    private static final String Day1 = "torsdag";   // Swedish due to locale on test computer
    private static final String Day2 = "fredag";   // Swedish due to locale on test computer
    private static final String Day3 = "lördag";   // Swedish due to locale on test computer
    private static final String Day4 = "söndag";   // Swedish due to locale on test computer


    private static final int INACTIVITY_LIMIT_FREE      =  1;   // Max days inactivity to get message
    private static final int INACTIVITY_LIMIT_PAYING    =  30;   // Max days inactivity to get message      TODO: Should be 50
    private static final int ACTIVITY_MIN   = 100;               // Min sessions to be active               TODO: Should be 35

    private String gameCode;
    private Reward reward;
    private String message;

    int messageIdBase = 0;      // Distinguish with and without reward

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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {

        if(count > DAILY_CAP){

            System.out.println("    -- Campaign " + Name + " not firing. Daily cap reached for campaign." );
            return null;

        }


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

            if(state != CampaignState.REDUCED){

                maxInactivityFree += 1;
                maxInactivityPaying += 10;
            }
            messageIdBase = 50;

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

            if(state == CampaignState.REDUCED && user.payments == 0){

                System.out.println("    -- Campaign (Sat/Sun)" + Name + " not firing. (Only paying players for reduced mode)");
                return null;
            }


            System.out.println("    -- Campaign (Sat/Sun)" + Name + " firing. (Sending a notification on a regular day)");
            count++;
            action = new NotificationAction(message, user, executionTime, getPriority(), getTag(), Name,  1 + messageIdBase, getState(), responseFactor)
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


        if(favouriteDay == ReceptivityProfile.Not_significant && (playerInfo.getUser().group.equals("A") || playerInfo.getUser().group.equals("B"))){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a Thursday to a player without specific day"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  2 + messageIdBase, getState(), responseFactor)
                    .withGame(gameCode);

        }

        if(favouriteDay == 4){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on thursday for thursday players " + playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime,  getPriority(), getTag(), Name,  3 + messageIdBase, getState(), responseFactor)
                    .withGame(gameCode);

        }
        else{

            System.out.println("    -- Campaign (weekend)" + Name + " not firing. On a Thursday we only send messages to Thursday players (and undefined) "+ favouriteDay+ "  " +playerInfo.getReceptivityForPlayer().toString());
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
     * @param favouriteDay                 - for player
     * @param responseFactor
     * @param executionTime
     * @return
     */

    private NotificationAction handleFriday(PlayerInfo playerInfo, int favouriteDay, double responseFactor, Timestamp executionTime) {


        if(favouriteDay == ReceptivityProfile.Not_significant && (playerInfo.getUser().group.equals("C"))){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a Friday to a player without specific day"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  4 + messageIdBase, getState(), responseFactor)
                    .withGame(gameCode);

        }


        if(favouriteDay == 1  || favouriteDay == 2  || favouriteDay == 3 ){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a friday to a weekday player"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  5 + messageIdBase, getState(), responseFactor)
                    .withGame(gameCode);

        }


        if(favouriteDay == 5){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a friday to a friday player"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  6 + messageIdBase, getState(), responseFactor)
                    .withGame(gameCode);

        }

        if(favouriteDay == 4){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a friday to a MISSED thursday player"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  7 + messageIdBase, getState(), responseFactor)
                    .withGame(gameCode);

        }

        if(favouriteDay == 6 && (playerInfo.getUser().group.equals("A"))){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a friday to a saturday player"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  8 + messageIdBase, getState(), responseFactor)
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

        if(favouriteDay == ReceptivityProfile.Not_significant && (playerInfo.getUser().group.equals("D"))){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a Saturday to a player without specific day"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  9 + messageIdBase, getState(), responseFactor)
                    .withGame(gameCode);

        }

        if(favouriteDay == 6 ){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a saturday to a saturday player"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  14 + messageIdBase, getState(), responseFactor)
                    .withGame(gameCode);

        }


        System.out.println("    -- Campaign (weekend)" + Name + " not firing. No messages at all on Saturday"+ "  " +playerInfo.getReceptivityForPlayer().toString());
        return null;

    }


    private NotificationAction handleSunday(PlayerInfo playerInfo, int favouriteDay, double responseFactor, Timestamp executionTime) {


        if(favouriteDay == 0 ){

            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a sunday to sunday player"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  10 + messageIdBase, getState(), responseFactor)
                    .withGame(gameCode);


        }

        System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification to ANY player not addressed yet "+ favouriteDay+")"+ "  " +playerInfo.getReceptivityForPlayer().toString());
        return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  11 + messageIdBase, getState(), responseFactor)
                .withGame(gameCode);


    }

    private NotificationAction handleSaturdaySpecial(PlayerInfo playerInfo, int favouriteDay, double responseFactor, Timestamp executionTime) {

        if(favouriteDay == 6 ){

            // The other half
            System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification on a saturday to a saturday player"+ "  " +playerInfo.getReceptivityForPlayer().toString());
            return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  12 + messageIdBase, getState(), responseFactor)
                    .withGame(gameCode);


        }

        System.out.println("    -- Campaign (weekend)" + Name + " firing. (Sending a notification to a saturday to ANY player not addressed yet "+ favouriteDay+")"+ "  " +playerInfo.getReceptivityForPlayer().toString());
        return new NotificationAction(message, playerInfo.getUser(), executionTime, getPriority(), getTag(), Name,  13 + messageIdBase, getState(), responseFactor)
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
