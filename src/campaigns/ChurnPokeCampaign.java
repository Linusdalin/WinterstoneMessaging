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
import response.ResponseStat;
import rewards.RewardRepository;

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
    private static final int CoolDown_Days = 5;


    // Trigger specific config data
    private static final int Min_Sessions = 18;
    private static final int MAX_IDLE = 100;

    ChurnPokeCampaign(int priority, CampaignState active){

        super(Name, priority, active);
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

                if(!playerInfo.fallbackFromMobile()){

                    return new MobilePushAction("Hello, your daily bonus is waiting for you. Click to claim it NOW!",
                            user, executionTime, getPriority(), getTag(),  Name, 301, getState(), responseFactor);
                }
                else{

                    System.out.println("    -- Sending a churn warning poke mail" );
                    return new EmailAction(churnPokeEmail(user, createPromoCode(201)), user, executionTime, getPriority(), getTag(), 207, getState(), responseFactor);

                }


            }

            // Adding a time of day separation test

            if(playerInfo.getReceptivityForPlayer().getFavouriteTimeOfDay(ReceptivityProfile.SignificanceLevel.GENERAL) == ReceptivityProfile.DAY){

                if(randomize4(user, 2)|| randomize4(user, 3)){

                    System.out.println("    -- Campaign " + Name + " firing. Morning players in the evening" );
                    return new NotificationAction("Hello "+ user.name+", you now have new free coins to collect! Click here to claim it NOW!",
                            user, executionTime, getPriority(), getTag(),  Name, 87, getState(), responseFactor);
                }
            }
            else if(playerInfo.getReceptivityForPlayer().getFavouriteTimeOfDay(ReceptivityProfile.SignificanceLevel.GENERAL) == ReceptivityProfile.EVENING){

                if(randomize4(user, 2)|| randomize4(user, 3)){

                    System.out.println("    -- Campaign " + Name + " firing. Evening players in the evening" );
                    return new NotificationAction("Hello "+ user.name+", you now have new free coins to collect! Click here to claim it NOW!",
                            user, executionTime, getPriority(), getTag(),  Name, 88, getState(), responseFactor);
                }
            }


            // Other players (not part of the test)

                System.out.println("    -- Campaign " + Name + " firing. Other players in the evening" );
                return new NotificationAction("Hello "+ user.name+", you now have new free coins to collect! Click here to claim it NOW!",
                        user, executionTime, getPriority(), getTag(),  Name, 1, getState(), responseFactor);


            }

            else if(idleDays == 5 || idleDays == 6){

                if(playerInfo.getUsageProfile().hasTriedMobile()){


                    System.out.println("    -- Sending a FIVE day churn warning poke on mobile" );
                    return new MobilePushAction("Hello, the games are hot and the bonus ready to pick!",
                            user, executionTime, getPriority(), getTag(),  Name, 302, getState(), responseFactor);

                }

                System.out.println("    -- Campaign " + Name + " firing. Other players in the evening" );
                return new NotificationAction("Hello "+ user.name+", the games are hot and the bonus ready to pick!!",
                        user, executionTime, getPriority(), getTag(),  Name, 2, getState(), responseFactor);



            }
            else if((idleDays == 7 || idleDays == 8)
                    && playerInfo.getUsageProfile().hasTriedMobile()){

                System.out.println("    -- Sending a SEVEN day churn warning poke on mobile" );
                return new MobilePushAction("Hi, the luck is awaiting you at SlotAmerica. Come in and experience the thrill.!",
                        user, executionTime, getPriority(), getTag(),  Name, 303, getState(), responseFactor);

            }
            else if(idleDays == 9 && !betterTomorrow(playerInfo, executionTime)){

                if(state == CampaignState.REDUCED){

                    System.out.println("    -- Campaign " + Name + " not firing. Reduced mode remove low priority messages" );
                    return null;

                }


                System.out.println("    -- Sending a NINE day churn warning poke" );
                return new NotificationAction("Hello, don't miss out the latest slot game release at SlotAmerica. Click here to check it out!",
                    user, executionTime, getPriority(), getTag(),  Name,  4, getState(), responseFactor);

            }
            else if(idleDays == 9 || idleDays == 10){


                System.out.println("    -- Sending a NINE day churn warning poke" );
                return new NotificationAction("Hello, don't miss out the latest slot game release at SlotAmerica. Click here to check it out!",
                        user, executionTime, getPriority(), getTag(),  Name,  7, getState(), responseFactor);





        }
        else if(idleDays == 14 && !betterTomorrow(playerInfo, executionTime)){

            if(state == CampaignState.REDUCED){

                System.out.println("    -- Campaign " + Name + " not firing. Reduced mode remove low priority messages" );
                return null;

            }

            System.out.println("    -- Sending a 14 day churn warning poke" );
            return new NotificationAction("Hello, there are some new exiting releases at Slot America. Click here to check it out!",
                    user, executionTime, getPriority(), getTag(),  Name,  5, getState(), responseFactor);


        }
        else if(idleDays == 14 || idleDays == 15){

            if(state == CampaignState.REDUCED){

                System.out.println("    -- Campaign " + Name + " not firing. Reduced mode remove low priority messages" );
                return null;

            }

            System.out.println("    -- Sending a Fifteen day churn warning poke" );
            return new NotificationAction("Hello, there are some new exiting releases at Slot America. Click here to check it out!",
                    user, executionTime, getPriority(), getTag(),  Name,  6, getState(), responseFactor);


        }
        else if(idleDays > 25 && idleDays <= 30 && user.payments == 0){


            System.out.println("    -- Sending a Fifteen day churn warning poke" );
            return new NotificationAction("There are new games and you have some surprise coins to try them out for free! Just click here!",
                    user, executionTime, getPriority(), getTag(),  Name,  9, getState(), responseFactor)
                    .withReward(RewardRepository.MAU_FREECOINS);


        }
        else if(idleDays > 15  && isOkDay(playerInfo, executionTime)){


            System.out.println("    -- Sending a churn warning poke mail" );
            return new EmailAction(churnPokeEmail(user, createPromoCode(207)), user, executionTime, getPriority(), getTag(), 207, getState(), responseFactor);

        }
        else{

            System.out.println("    -- Campaign " + Name + " not firing. Not churn warning (last:" + lastSession.toString() );
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

    public static EmailInterface churnPokeEmail(User user, String promoCode) {
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

        return isTooEarly(executionTime, overrideTime);

    }



}
