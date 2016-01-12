package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;

import java.sql.Timestamp;


/************************************************************************'
 *
 *              For ongoing happy hour campaigns send info to paying players
 */

public class BlackFridayCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "BlackFriday";
    private static final int CoolDown_Days = 2;            // Send out multiple times during the weekend
    private static final int[] MessageIds = {
            1, 2,                                       // Black friday
            3, 4 };                                     // Christmas sale


    // Trigger specific config data
    private static final int MAX_INACTIVITY = 15;
    private int percentage;

    BlackFridayCampaign(int priority, CampaignState activation, int percentage){

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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor) {

        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();


        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }
        int inactivity = getDaysBetween(lastSession, executionDay);


        if(inactivity > MAX_INACTIVITY){

            System.out.println("    -- Campaign " + Name + " not firing. User has been inactive too long. (" + inactivity + " days )" );
            return null;

        }

        if(user.payments == 0){

            if(user.sessions > 40 ){

                // Very active player - entice to buy...   TEST

                System.out.println("    -- Sending a BlackFriday offer to active free player" );
                return new NotificationAction("New Year ‘3-for-1’-coin Sale is still ongoing here on SlotAmerica. Enter and grab your chance!",
                        user, executionTime, getPriority(), getTag(),  Name, 3, getState(), responseFactor);

            }



            System.out.println("    -- Campaign " + Name + " not firing. Only real money players (or very active)" );
            return null;
        }

        if(user.payments > 0){

            // Paying payer

            System.out.println("    -- Sending a happy hour reminder to paying payer" );
            return new NotificationAction("New Year ‘3-for-1’-coin Sale is still ongoing here on SlotAmerica. Enter and grab your chance!",
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


        String tooEarlyCheck = isTooEarly(executionTime, overrideTime);

        return tooEarlyCheck;

    }


}
