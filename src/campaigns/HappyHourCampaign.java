package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;
import response.ResponseStat;

import java.sql.Timestamp;


/************************************************************************'
 *
 *              For ongoing happy hour campaigns send info to paying players
 */

public class HappyHourCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Happy Hour";
    private static final int CoolDown_Days = 9;            // A bit more than a week to avoid getting it every day
    private static final int[] MessageIds = { 1, 2, 3, 4, 5, 6, 7 };


    // Trigger specific config data
    private static final int MAX_INACTIVITY = 30;
    private int percentage;

    HappyHourCampaign(int priority, CampaignState activation, int percentage){

        super(Name, priority, activation);
        this.percentage = percentage;
        setCoolDown(CoolDown_Days);
        registerMessageIds( MessageIds );
    }

    /********************************************************************
     *
     *              Decide on the campaign
     *
     *              The output could be one of 4 different messages depending on the day
     *
     * @param playerInfo             - the user to evaluate
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {

        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();


        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }
        int inactivity = getDaysBetween(lastSession, executionDay);


        if(inactivity > MAX_INACTIVITY){

            if(user.payments > 0 && user.sessions > 50 && inactivity < MAX_INACTIVITY + 10 ){

                // Previously active player

                System.out.println("    -- Sending a happy hour reminder to previously highly active player" );
                return new NotificationAction("Hello, It is now happy hour at SlotAmerica with "+ this.percentage+"% extra on all purchases. A perfect time to try the real Vegas feeling. Click here to get going!!",
                        user, executionTime, getPriority(), getTag(),  Name, 1, getState(), responseFactor);

            }

            System.out.println("    -- Campaign " + Name + " not firing. User has been inactive too long. (" + inactivity + " days )" );
            return null;

        }

        if(user.payments == 0){

            if(user.sessions > 300 && inactivity < 1 ){

                // Very active player - entice to buy...   TEST

                System.out.println("    -- Sending a happy hour reminder to active free player" );
                return new NotificationAction("Hello, It is now happy hour at SlotAmerica with extra coins on all purchases. A perfect time to try the real Vegas feeling. Click here to get going!!",
                        user, executionTime, getPriority(), getTag(),  Name, 2, getState(), responseFactor);


            }

            System.out.println("    -- Campaign " + Name + " not firing. Only real money players (or very active)" );
            return null;
        }

        if(user.payments > 0 && inactivity <= 2){

            if(user.payments > 20){

                System.out.println("    -- Sending a happy hour reminder to repeat payer" );
                return new NotificationAction("Hello, It is now happy hour at SlotAmerica with "+this.percentage+"% extra on all coin purchases for a limited time. A perfect time to level up. Click here to get going!!",
                        user, executionTime, getPriority(), getTag(),  Name, 5, getState(), responseFactor);


            }

            System.out.println("    -- Sending a happy hour reminder to not so frequent payer" );
            return new NotificationAction("Hello, It is now happy hour at SlotAmerica with "+this.percentage+"% extra on all coin purchases for a limited time. A perfect time to level up. Click here to get going!!",
                    user, executionTime, getPriority(), getTag(),  Name, 4, getState(), responseFactor);


        }

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

        /*
        String specificWeekDay = isSpecificDay(executionTime, dryRun, "måndag");

        if(specificWeekDay != null)
            return specificWeekDay;

        */

        String tooEarlyCheck = isTooEarly(executionTime, overrideTime);

        return tooEarlyCheck;

    }


}
