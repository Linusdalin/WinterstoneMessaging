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

public class ValentinesCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Valentines";
    private static final int CoolDown_Days = 20;            // Send out multiple times during the weekend
    private static final int[] MessageIds = { };


    // Trigger specific config data
    private static final int MAX_INACTIVITY = 9;

    ValentinesCampaign(int priority, CampaignState activation){

        super(Name, priority, activation);
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

            System.out.println("    -- Campaign " + Name + " not firing. User has been inactive too long. (" + inactivity + " days )" );
            return null;

        }

        if(user.payments == 0){

            if(user.sessions > 100 && inactivity < 2){

                // Very active player - entice to buy...   TEST

                System.out.println("    -- Sending a BlackFriday offer to active free player" );
                return new NotificationAction("Happy Valentines Sale! 100% extra on all purchases now. Click here to enter!",
                        user, executionTime, getPriority(), getTag(),  Name, 1, getState(), responseFactor);

            }



            System.out.println("    -- Campaign " + Name + " not firing. Only real money players (or very active)" );
            return null;
        }

        if(user.payments > 0){

            // Paying payer

            System.out.println("    -- Sending a happy hour reminder to paying payer" );
            return new NotificationAction("Happy Valentines Sale! 100% extra on all purchases now. Click here to enter!",
                    user, executionTime, getPriority(), getTag(),  Name, 2, getState(), responseFactor);


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


        String tooEarlyCheck = isTooEarly(executionTime, overrideTime);

        return tooEarlyCheck;

    }


}
