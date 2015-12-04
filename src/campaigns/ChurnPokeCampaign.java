package campaigns;

import action.ActionInterface;
import action.EmailAction;
import action.MobilePushAction;
import action.NotificationAction;
import core.PlayerInfo;
import email.EmailInterface;
import email.NotificationEmail;
import receptivity.ReceptivityProfile;
import remoteData.dataObjects.User;

import java.sql.Timestamp;


/************************************************************************'
 *
 *              Sending a message to players that stop playing
 *
 *              This is a simple first take, sending a message 3 to all players that have played at lease 10 sessions and then stopped
 *
 */

public class ChurnPokeCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "ChurnPoke";
    private static final int CoolDown_Days = 6;
    private int[] MessageIds = {3,  8, 9, 15, 14, 29, 30,
                                203,
                                1000, 1001, 1002, 1010, 1011, 1012, 1020, 1021, 1022           // Temp test for time scheduling

    };


    // Trigger specific config data
    private static final int Min_Sessions = 9;
    private static final boolean AB_TEST_ACTIVE = false;

    ChurnPokeCampaign(int priority, CampaignState active){

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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor) {

        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        if(user.sessions < Min_Sessions){

            System.out.println("    -- Campaign " + Name + " not applicable. User is not frequent enough (" + user.sessions + " < " + Min_Sessions + ")" );
            return null;

        }

        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }

        int idleDays = getDaysBetween(lastSession, executionDay);

        if(idleDays == 3 || idleDays == 4){

            System.out.println("    -- Sending a three day churn warning poke" );

            if(playerInfo.getUsageProfile().isAnnymousMobile()){

                return new MobilePushAction("Hello, your daily bonus is waiting for you. Click to claim it NOW!",
                        user, executionTime, getPriority(), getTag(),  Name, 203, getState(), responseFactor);

            }

            if(AB_TEST_ACTIVE)
                return timeSchedulingTest(playerInfo, executionTime, responseFactor);
            else
                return new NotificationAction("Hello "+ user.name+", your daily bonus is waiting for you at Slot America. Click here to claim it NOW!",
                        user, executionTime, getPriority(), getTag(),  Name, 3, getState(), responseFactor);



        }
        else if(idleDays == 9 && !betterTomorrow(playerInfo, executionTime)){

            System.out.println("    -- Sending a NINE day churn warning poke" );
            return new NotificationAction("Hello, don't miss out the latest slot game release at SlotAmerica. Click here to check it out!",
                user, executionTime, getPriority(), getTag(),  Name,  9, getState(), responseFactor);


        }
        else if(idleDays == 9 || idleDays == 10){

            System.out.println("    -- Sending a NINE day churn warning poke" );
            return new NotificationAction("Hello, don't miss out the latest slot game release at SlotAmerica. Click here to check it out!",
                    user, executionTime, getPriority(), getTag(),  Name,  8, getState(), responseFactor);


        }
        else if(idleDays == 14 && !betterTomorrow(playerInfo, executionTime)){


            System.out.println("    -- Sending a 14 day churn warning poke" );
            return new NotificationAction("Hello, there are some new exiting releases at Slot America. Click here to check it out!",
                    user, executionTime, getPriority(), getTag(),  Name,  14, getState(), responseFactor);


        }
        else if(idleDays == 14 || idleDays == 15){


            System.out.println("    -- Sending a Fifteen day churn warning poke" );
            return new NotificationAction("Hello, there are some new exiting releases at Slot America. Click here to check it out!",
                    user, executionTime, getPriority(), getTag(),  Name,  15, getState(), responseFactor);


        }
        else if(idleDays > 10 && idleDays < 14){


            System.out.println("    -- Sending a churn warning poke mail" );

                    return new EmailAction(churnPokeEmail(user), user, executionTime, getPriority(), getTag(), 10, getState(), responseFactor);

        }
        else if(idleDays >= 26 && idleDays < 30){

            if(isRightDay(playerInfo, executionTime, ReceptivityProfile.SignificanceLevel.GENERAL)){

                System.out.println("    -- Sending a final day churn warning poke" );
                return new NotificationAction("It is party time at SlotAmerica today with the new game releases! Click here to check it out!",
                        user, executionTime, getPriority(), getTag(),  Name,  29, getState(), responseFactor);

            }
            else if(idleDays == 30){

                System.out.println("    -- Sending a final day churn warning poke" );
                return new NotificationAction("It is party time at SlotAmerica today with the new game releases! Click here to check it out!",
                        user, executionTime, getPriority(), getTag(),  Name,  30, getState(), responseFactor);

            }

            else{

                System.out.println("    -- Campaign " + Name + " not firing. Waiting for an Ok day for player between 26 and 32 (last:" + lastSession.toString() );
                return null;

            }


        }
        else{

            System.out.println("    -- Campaign " + Name + " not firing. Not three day churn warning (last:" + lastSession.toString() );
            return null;

        }

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


    private EmailInterface churnPokeEmail(User user) {
        return new NotificationEmail("where did you go", "<p>Don't miss out on all the new game releases here at Slot America. We try to put out a new prime game for you every week and you have some new games to check out!</p>" +
                "<p> Why don't you come in and use your free bonus to try them? Click <a href=\"https://apps.facebook.com/slotAmerica/?promocode=EcoinsLeft-30\">here</a> to test it out :-) </p>",
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

    public String testFailCalendarRestriction(Timestamp executionTime, boolean overrideTime) {

        return isTooEarly(executionTime, overrideTime);

    }



}
