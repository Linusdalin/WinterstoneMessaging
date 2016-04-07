package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import email.EmailInterface;
import email.NotificationEmail;
import receptivity.ReceptivityProfile;
import remoteData.dataObjects.User;
import response.ResponseStat;

import java.sql.Timestamp;


/************************************************************************'
 *
 *              Sending a message to players that stop playing
 *
 *              This is a simple first take, sending a message 3 to all players that have played at lease 10 sessions and then stopped
 *
 */

public class ChurnPokeMorningCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "ChurnPoke";
    private static final int CoolDown_Days = 5;
    private int[] MessageIds = {    };


    // Trigger specific config data
    private static final int Min_Sessions = 18;
    private static final int MAX_IDLE = 100;

    ChurnPokeMorningCampaign(int priority, CampaignState active){

        super(Name, priority, active);
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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {

        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        // In reduced mode we only send to the most frequent group

        int activeMinSessionLimit = Min_Sessions;

        if(state == CampaignState.REDUCED)
            activeMinSessionLimit +=10;


        if(user.sessions < activeMinSessionLimit){

            System.out.println("    -- Campaign " + Name + " not applicable. User is not frequent enough (" + user.sessions + " < " +
                    activeMinSessionLimit + ")" );
            return null;

        }


        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }

        int idleDays = getDaysBetween(lastSession, executionDay);

        if(idleDays > MAX_IDLE){

            System.out.println("    -- Campaign " + Name + " not applicable. User idle too long. (" + idleDays + " > " + MAX_IDLE + ")");
            return null;

        }



        if(idleDays == 3 || idleDays == 4){

            System.out.println("    -- Sending a three day churn warning poke" );

            if(playerInfo.getUsageProfile().hasTriedMobile()){

                System.out.println("    -- Campaign " + Name + " not firing. Not mobile");
                return null;

            }

            // Adding a time of day separation test

            if(playerInfo.getReceptivityForPlayer().getFavouriteTimeOfDay(ReceptivityProfile.SignificanceLevel.GENERAL) == ReceptivityProfile.DAY){

                if(randomize4(user, 0)|| randomize4(user, 1)){

                    // Day players in the morning

                    System.out.println("    -- Campaign " + Name + " firing. Morning players in the morning" );
                    return new NotificationAction("Hello "+ user.name+", you now have new free coins to collect! Click here to claim it NOW!",
                            user, executionTime, getPriority(), getTag(),  Name, 85, getState(), responseFactor);
                }
            }


            if(playerInfo.getReceptivityForPlayer().getFavouriteTimeOfDay(ReceptivityProfile.SignificanceLevel.GENERAL) == ReceptivityProfile.EVENING){

                if(randomize4(user, 0)|| randomize4(user, 1)){

                    // Day players in the morning

                    System.out.println("    -- Campaign " + Name + " firing. Evening players in the morning" );
                    return new NotificationAction("Hello "+ user.name+", you now have new free coins to collect! Click here to claim it NOW!",
                            user, executionTime, getPriority(), getTag(),  Name, 86, getState(), responseFactor);
                }
            }

        }

        System.out.println("    -- Campaign " + Name + " not firing. Not three day churn warning (last:" + lastSession.toString() );
        return null;

    }

    /*************************************************************************************************
     *
     *              Time of day scheduling test
     *
     *
     *
     * @param playerInfo
     * @param executionTime
     * @param responseFactor
     * @return
     */



    private ActionInterface timeSchedulingTest(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor) {

        User user = playerInfo.getUser();
        int favouriteTime = playerInfo.getReceptivityForPlayer().getFavouriteTimeOfDay(ReceptivityProfile.SignificanceLevel.SPECIFIC);

        switch(favouriteTime){

            case ReceptivityProfile.DAY:

                if(randomize3(user, 0)){

                    // Schedule correctly
                    return new NotificationAction("Hello "+ user.name+", your daily bonus is waiting for you at Slot America. Click here to claim it NOW!",
                            user, executionTime, getPriority(), getTag(),  Name, 1000, getState(), responseFactor)
                            .scheduleInTime( ReceptivityProfile.DAY );
                }

                if(randomize3(user, 1)){

                    // Schedule later
                    return new NotificationAction("Hello "+ user.name+", your daily bonus is waiting for you at Slot America. Click here to claim it NOW!",
                            user, executionTime, getPriority(), getTag(),  Name, 1001, getState(), responseFactor)
                            .scheduleInTime( ReceptivityProfile.EVENING );
                }

                // Schedule earlier
                return new NotificationAction("Hello "+ user.name+", your daily bonus is waiting for you at Slot America. Click here to claim it NOW!",
                        user, executionTime, getPriority(), getTag(),  Name, 1002, getState(), responseFactor)
                        .scheduleInTime( ReceptivityProfile.NIGHT );

            case ReceptivityProfile.EVENING:

                if(randomize3(user, 0)){

                    // Schedule correctly
                    return new NotificationAction("Hello "+ user.name+", your daily bonus is waiting for you at Slot America. Click here to claim it NOW!",
                            user, executionTime, getPriority(), getTag(),  Name, 1011, getState(), responseFactor)
                            .scheduleInTime( ReceptivityProfile.EVENING );
                }

                if(randomize3(user, 1)){

                    // Schedule later
                    return new NotificationAction("Hello "+ user.name+", your daily bonus is waiting for you at Slot America. Click here to claim it NOW!",
                            user, executionTime, getPriority(), getTag(),  Name, 1012, getState(), responseFactor)
                            .scheduleInTime( ReceptivityProfile.NIGHT );
                }

                // Schedule earlier
                return new NotificationAction("Hello "+ user.name+", your daily bonus is waiting for you at Slot America. Click here to claim it NOW!",
                        user, executionTime, getPriority(), getTag(),  Name, 1010, getState(), responseFactor)
                        .scheduleInTime( ReceptivityProfile.DAY );

            case ReceptivityProfile.NIGHT:

                if(randomize3(user, 0)){

                    // Schedule correctly
                    return new NotificationAction("Hello "+ user.name+", your daily bonus is waiting for you at Slot America. Click here to claim it NOW!",
                            user, executionTime, getPriority(), getTag(),  Name, 1022, getState(), responseFactor)
                            .scheduleInTime( ReceptivityProfile.DAY );
                }

                if(randomize3(user, 1)){

                    // Schedule later
                    return new NotificationAction("Hello "+ user.name+", your daily bonus is waiting for you at Slot America. Click here to claim it NOW!",
                            user, executionTime, getPriority(), getTag(),  Name, 1020, getState(), responseFactor)
                            .scheduleInTime( ReceptivityProfile.EVENING );
                }

                // Schedule earlier
                return new NotificationAction("Hello "+ user.name+", your daily bonus is waiting for you at Slot America. Click here to claim it NOW!",
                        user, executionTime, getPriority(), getTag(),  Name, 1021, getState(), responseFactor)
                        .scheduleInTime( ReceptivityProfile.NIGHT );


            default:

                // No specific time of day for user.

                return new NotificationAction("Hello "+ user.name+", your daily bonus is waiting for you at Slot America. Click here to claim it NOW!",
                        user, executionTime, getPriority(), getTag(),  Name, 3, getState(), responseFactor)
                        .scheduleInTime( favouriteTime );

        }





    }

    /****************************************************************************************
     *
     *              Email
     *
     *
     * @param user
     * @return
     *
     *          TODO: Add more random mail texts
     *
     */

    private EmailInterface churnPokeEmail(User user, String promoCode) {
        return new NotificationEmail("where did you go?", "<p>Don't miss out on all the new game releases here at Slot America. We try to put out a new prime game for you every week and you have some new games to check out!</p>" +
                "<p> Why don't you come in and use your free bonus to try them? Click <a href=\"https://apps.facebook.com/slotAmerica/?promocode="+promoCode+"\">here</a> to test it out :-) </p>",
                "Hello "+ user.name+" Don't miss out on all the new game releases here at Slot America. We try to put out a new prime game for you every week and you have some new games to check out." +
        "Why don't you come in and use your free bonus to try them?");
    }


    /*********************************************************************
     *
     *              Campaign timing restrictions
     *
     * @param executionTime     - time of execution
     * @return                  - messgage or null if ok.
     */

    public String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {

        return isTooLate(executionTime, overrideTime);

    }



}
