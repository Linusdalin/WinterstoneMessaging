package campaigns;

import action.ActionInterface;
import action.MobilePushAction;
import action.NotificationAction;
import action.TriggerEventAction;
import core.PlayerInfo;
import email.EmailInterface;
import email.NotificationEmail;
import events.EventRepository;
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

public class ConversionOfferCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "ConversionOffer";
    private static final int CoolDown_Days = 365000;


    // Trigger specific config data
    private static final int Min_Sessions = 1;
    private static final int Min_Sessions_For_Message = 4;
    private static final int TARGET_AGE = 10;             // The whole week
    private static final int MAX_IDLE = 5;
    private int hours;
    private boolean verbose;                             // Actually tell the users or just quietly trigger the campaign

    ConversionOfferCampaign(int priority, CampaignState active, int hours, boolean verbose){

        super(Name, priority, active);
        this.hours = hours;
        this.verbose = verbose;
        setCoolDown(CoolDown_Days);
    }


    /********************************************************************
     *
     *              Decide on the campaign
     *
     *
     *
     * @param playerInfo             - the user to evaluate
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {

        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        int age = getDaysBetween(user.created, executionDay);

        // Now running all new playes in the week

        if( age > TARGET_AGE){

            System.out.println("    -- Campaign " + Name + " not firing. Not right day. (Age = " + age + ")");
            return null;

        }

        if(user.payments > 0){

            System.out.println("    -- Campaign " + Name + " not firing. Payer has already paid");
            return null;
        }

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

        if(idleDays > MAX_IDLE){

            System.out.println("    -- Campaign " + Name + " not applicable. User idle too long. (" + idleDays + " > " + MAX_IDLE + ")");
            return null;

        }

        if(user.group.equals("A") || user.group.equals("B")){

            System.out.println("    -- Campaign " + Name + " not firing for group A or B. Split testing");
            return null;

        }



        System.out.println("    -- Campaign" + Name + " firing. Triggering conversion offer");

        ActionInterface triggerAction = new TriggerEventAction(EventRepository.Conversion1, hours, user, executionTime, getPriority(), Name, 1, getState(), responseFactor);

        if(user.sessions > Min_Sessions_For_Message && verbose){

            if(playerInfo.getUsageProfile().isMobilePlayer()){

                return new MobilePushAction("Join the high roller action - the high roller weekend!",//"You have a 48 hour special offer",
                        user, executionTime, getPriority(), getTag(), Name, 1, getState(), responseFactor)
                        .attach(triggerAction);

            }

            return new NotificationAction("Welcome to SlotAmerica. Here is a special 48 hour offer to get even more excitement out of the games - the high roller weekend!",//"You have a 48 hour special offer",
                    user, executionTime, getPriority(), getTag(), Name, 1, getState(), responseFactor)
                    .attach(triggerAction);
        }
        else{

            return triggerAction;
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

        return null;  // No restriction. This can be started any time

    }



}
