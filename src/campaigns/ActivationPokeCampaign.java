package campaigns;

import action.ActionInterface;
import action.GiveCoinAction;
import action.MobilePushAction;
import action.NotificationAction;
import core.PlayerInfo;
import receptivity.ReceptivityProfile;
import remoteData.dataObjects.User;
import response.ResponseStat;

import java.sql.Timestamp;


/************************************************************************'
 *
 *              Sending a message to players early in the progress to get them going
 *
 *          Sending a 2 day activation poke and a 7 day activation poke with a
 *          8 day cool down will ensure that the message is only sent once per player
 */

public class ActivationPokeCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "ActivationPoke";
    private static final int CoolDown_Days = 4;

    // Trigger specific config data
    private static final int Min_Sessions = 3;
    private static final int Max_Sessions = 12;
    private static final int Max_Age = 20;
    private int[] MessageIds = {2, 3, 4, 5,

                                31, 32, 33};

    ActivationPokeCampaign(int priority, CampaignState active){

        super(Name, priority, active);
        setCoolDown(CoolDown_Days);
        registerMessageIds(MessageIds);
    }


    /********************************************************************
     *
     *              Decide on the campaign
     *
     *
     *
     *
     * @param playerInfo             - the user to evaluate
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {

        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        if(user.sessions < Min_Sessions){

            System.out.println("    -- Campaign " + Name + " not applicable. User is not frequent enough (" + user.sessions + " < " + Min_Sessions + ")" );
            return null;

        }

        if(user.sessions > Max_Sessions){

            System.out.println("    -- Campaign " + Name + " not applicable. User is already active (" + user.sessions + " > " + Max_Sessions + ")" );
            return null;

        }

        if(getDaysBetween(user.created, executionDay) > Max_Age){

            System.out.println("    -- Campaign " + Name + " not firing. Player too old (created: " + user.created );
            return null;

        }

        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }

        int idleDays = getDaysBetween(lastSession, executionDay);

        System.out.println("    -- Sending a day two activation poke" );

        if(idleDays == 2){

            if(playerInfo.getUsageProfile().isMobileExclusive()){

                return new MobilePushAction("Don't miss the level up bonuses on SlotAmerica! Open the app to get going!", user, executionTime, getPriority(), getTag(), Name,  302, getState(), responseFactor);

            }

            return new NotificationAction("You haven't missed the level up bonuses at SlotAmerica, have you? Check out the rewards by clicking on the level bar.!",
                    user, executionTime, getPriority(), getTag(),  Name, 2, getState(), responseFactor);

        }

        if(idleDays == 5 || idleDays == 6 || idleDays == 7 && isRightDay(playerInfo, executionTime, ReceptivityProfile.SignificanceLevel.GENERAL)){

            System.out.println("    -- Sending a six  day activation poke on the correct day" );

            if(playerInfo.getUsageProfile().isMobileExclusive()){

                return new MobilePushAction("Let's start climbing the reward stairs at SlotAmerica! Touch here to get started with your daily bonus!", user, executionTime, getPriority(), getTag(), Name,  304, getState(), responseFactor);

            }


            if(state == CampaignState.REDUCED){

                System.out.println("    -- Campaign " + Name + " not firing. Reduced mode remove low priority messages" );
                return null;

            }

            return new NotificationAction("Let's start climbing the reward stairs at SlotAmerica! Click here to get started with your daily bonus!",
                    user, executionTime, getPriority(), getTag(),  Name, 4, getState(), responseFactor);

        }


        if(idleDays == 7){

            System.out.println("    -- Sending a seven day activation poke" );
            if(playerInfo.getUsageProfile().isMobileExclusive()){

                return new MobilePushAction("Let's start climbing the reward stairs at SlotAmerica! Touch here to get started with your daily bonus!", user, executionTime, getPriority(), getTag(), Name,  303, getState(), responseFactor);

            }

            if(state == CampaignState.REDUCED){

                System.out.println("    -- Campaign " + Name + " not firing. Reduced mode remove low priority messages" );
                return null;

            }

            return new NotificationAction("Let's start climbing the reward stairs at SlotAmerica! Click here to get started with your daily bonus!",
                    user, executionTime, getPriority(), getTag(),  Name, 3, getState(), responseFactor);


        }

        // Added a desperation notification here to get players back

        if(idleDays > 12  && isRightDay(playerInfo, executionTime, ReceptivityProfile.SignificanceLevel.GENERAL)){


            //System.out.println("    -- Sending a twelve day activation poke" );
            //return new NotificationAction("You have got 2000 coins extra to check out the cool level bonus system. Check out the rewards by clicking on the level bar.!",
            //        user, executionTime, getPriority(), getTag(),  Name, 5, getState(), responseFactor)
            //        .attach(new GiveCoinAction(2000, user, executionTime, getPriority(), Name, 5, getState(), responseFactor));




            System.out.println("    -- Sending a twelve day activation poke" );

            if(playerInfo.getUsageProfile().isMobileExclusive()){

                System.out.println("    -- Sending a seven day activation poke" );
                return new MobilePushAction(user.name + ", you have got 6,000 coins extra to check out the cool level bonus system.", user, executionTime, getPriority(), getTag(), Name,  305, getState(), responseFactor)
                        .attach(new GiveCoinAction(6000, user, executionTime, getPriority(), Name, 33, getState(), responseFactor));

            }

            return new NotificationAction(user.name + ", you have got 6,000 coins extra to check out the cool level bonus system. Check out the rewards by clicking on the level bar.!",
                    user, executionTime, getPriority(), getTag(),  Name, 5, getState(), responseFactor)
                    .attach(new GiveCoinAction(6000, user, executionTime, getPriority(), Name, 5, getState(), responseFactor));


        }


        System.out.println("    -- Campaign " + Name + " not firing. Not the right day. (last:" + lastSession.toString() );
        return null;



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
