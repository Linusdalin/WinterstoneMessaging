package campaigns;

import action.ActionInterface;
import action.ManualAction;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.Payment;
import remoteData.dataObjects.User;

import java.sql.Timestamp;


/************************************************************************'
 *
 *                  Engagement campaign will generate playes that
 *                  should get an facebook engagement ad.
 *
 *                  The idea is to target the campaigns better then the current
 *
 */

public class EngagementCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Engagement";
    private static final int CoolDown_Days = 0;     // This should be possible to run whenever. It just updates the target list


    // Trigger specific config data
    private static final int PAYMENT_INACTIVITY_LIMIT   = 180;      // Only players that has paid since
    private static final int PAYMENT_MIN                = 1;        // Minimum number of payments
    private static final int SESSION_MIN                = 5;       // Minimum number of sessions
    private static final int INACTIVITY_MIN             = 8;        // Minimum inactivity before the campaign starts
    private static final int INACTIVITY_MAX             = 40;       // Max inactivity before the campaign ends

    EngagementCampaign(int priority, CampaignState activation){

        super(Name, priority, activation);
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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime) {


        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }
        int inactivity = getDaysBetween(lastSession, executionDay);


        if(inactivity > INACTIVITY_MAX){

            System.out.println("    -- Campaign " + Name + " not active. " + inactivity + " > " + INACTIVITY_MAX );
            return null;
        }

        if(inactivity < INACTIVITY_MIN){

            System.out.println("    -- Campaign " + Name + " already active. " + inactivity + " < " + INACTIVITY_MIN );
            return null;
        }


        if(user.sessions < SESSION_MIN){

            System.out.println("    -- Campaign " + Name + " not engaged enough. " + user.sessions + " < " + SESSION_MIN );
            return null;
        }

        if(user.payments < PAYMENT_MIN){

            System.out.println("    -- Campaign " + Name + " not engaged enough. " + user.payments + " < " + PAYMENT_MIN );
            return null;
        }

        Payment lastPayment = playerInfo.getLastPayment();
        if(lastPayment == null){

            System.out.println("    -- Campaign " + Name + " no payments found." );
            return null;
        }

        int daySincePayment = getDaysBetween(lastPayment.timeStamp, executionTime);

        if(daySincePayment > PAYMENT_INACTIVITY_LIMIT){

            System.out.println("    -- Campaign " + Name + " payment too lon ago. " + daySincePayment + " > " + PAYMENT_INACTIVITY_LIMIT );
            return null;

        }

        System.out.println("    -- Campaign " + Name + " firing message1. Creating bonus for player" );
        return new ManualAction("Send player id " + user.facebookId + " to facebook reactivation campaign",
                user, getPriority(),  Name, 1, getState());


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
