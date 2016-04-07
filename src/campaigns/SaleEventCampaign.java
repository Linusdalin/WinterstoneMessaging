package campaigns;

import action.ActionInterface;
import action.MobilePushAction;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;
import response.ResponseStat;

import java.sql.Timestamp;


/************************************************************************'
 *
 *              For ongoing happy hour campaigns send info to paying players
 */

public class SaleEventCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "SaleEvent";
    private static final int CoolDown_Days = 6;            // Never more than one happy hour per week


    // Trigger specific config data
    private static final int MAX_INACTIVITY = 180;
    private static final int Min_Sessions = 10;

    private String message;

    SaleEventCampaign(int priority, CampaignState activation, String message){

        super(Name, priority, activation);
        this.message = message;
        setCoolDown(CoolDown_Days);
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


        if(user.sessions < Min_Sessions){

            System.out.println("    -- Campaign " + Name + " not applicable. User is not frequent enough (" + user.sessions + " < " + Min_Sessions + ")" );
            return null;

        }


        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }
        int inactivity = getDaysBetween(lastSession, executionDay);
        int age = getDaysBetween(user.created, executionDay);


        if(inactivity > MAX_INACTIVITY){

            // Previously active player
            // This is resurrection

            if(user.payments > 0 && user.sessions > 50 && inactivity < MAX_INACTIVITY + 10 ){


                System.out.println("    -- Sending a sale message to previously highly active player" );
                return new NotificationAction(message,
                        user, executionTime, getPriority(), getTag(),  Name, 1, getState(), responseFactor);

            }

            System.out.println("    -- Campaign " + Name + " not firing. User has been inactive too long. (" + inactivity + " days )" );
            return null;

        }

        // Active mobile players


        if(playerInfo.getUsageProfile().isMobilePlayer()){

            if(user.sessions > 20 && inactivity < 5 ){

                System.out.println("    -- Sending a sale message to mobile" );
                return new MobilePushAction(message, user, executionTime, getPriority(), getTag(),  Name, 301, getState(), responseFactor);
            }
        }


        if(user.payments == 0){

            if(user.sessions > 100 && inactivity < 1 ){

                // Very active player - entice to buy...   TEST

                System.out.println("    -- Sending a sale message to active free player" );
                return new NotificationAction(message,
                        user, executionTime, getPriority(), getTag(),  Name, 2, getState(), responseFactor);


            }

            // New players this week that has played on average 3 game sessions per day
            // This is for conversion

            if(user.sessions > 3*age && age < 7 && inactivity < 2 ){

                // Very active player - entice to buy...   TEST

                System.out.println("    -- Sending a happy hour reminder to new active player" );
                return new NotificationAction(message,
                        user, executionTime, getPriority(), getTag(),  Name, 3, getState(), responseFactor);


            }



            System.out.println("    -- Campaign " + Name + " not firing. Only real money players (or very active)" );
            return null;
        }

        if(user.payments > 0 && inactivity <= 2){

            if(user.payments > 15){

                System.out.println("    -- Sending a happy hour reminder to repeat payer" );
                return new NotificationAction(message,
                        user, executionTime, getPriority(), getTag(),  Name, 5, getState(), responseFactor);


            }

            System.out.println("    -- Sending a happy hour reminder to not so frequent payer" );
            return new NotificationAction(message,
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

    public String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {

        /*
        String specificWeekDay = isSpecificDay(executionTime, dryRun, "måndag");

        if(specificWeekDay != null)
            return specificWeekDay;

        */

        String tooEarlyCheck = isTooEarly(executionTime, overrideTime);

        return tooEarlyCheck;

    }


}
