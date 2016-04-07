package campaigns;

import action.ActionInterface;
import action.EmailAction;
import action.GiveCoinAction;
import action.MobilePushAction;
import core.PlayerInfo;
import email.NotificationEmail;
import remoteData.dataObjects.User;
import response.ResponseStat;

import java.sql.Timestamp;


/************************************************************************'
 *
 *                  This is pumping inactive mobile players with actions
 */

public class ReactivationMobileCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "ReactivationMobile";
    private static final int CoolDown_Days = 11;                    // Every once in a while

    // Trigger specific config data
    private static final int INACTIVITY_LIMIT_FREE      = 150;   // Max days inactivity to get message
    private static final int MIN_INACTIVITY_FREE        = 5;     // Starting point
    private static final int INACTIVITY_LIMIT_PAYING    = 0;   // Max days inactivity to get message
    private static final int MIN_INACTIVITY_PAYING      = 99999;     // Starting point
    private static final int ACTIVITY_MIN   = 3;               // Min sessions to be active

    private static final int DAILY_CAP   = 1000;         // Max per day
    private int count = 0;


    ReactivationMobileCampaign(int priority, CampaignState activation){

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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {

        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        if(!playerInfo.getUsageProfile().hasTriedMobile()){

            System.out.println("    -- Campaign " + Name + " not firing. Only mobile players");
            return null;

        }


        if(count > DAILY_CAP){

            System.out.println("    -- Campaign " + Name + " not firing. Daily cap reached for campaign." );
            return null;

        }

        if(user.sessions < ACTIVITY_MIN){

            System.out.println("    -- Campaign " + Name + " not active. Player has not been active enough ("+ user.sessions +" sessions <  " + ACTIVITY_MIN);
            return null;

        }

        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }
        int inactivity = getDaysBetween(lastSession, executionDay);

        if(user.payments == 0 && inactivity > INACTIVITY_LIMIT_FREE){

            System.out.println("    -- Campaign " + Name + " not active. Free player is inactive. ("+ inactivity+" days >  " + INACTIVITY_LIMIT_FREE);
            return null;

        }

        if(user.payments == 0 && inactivity < MIN_INACTIVITY_FREE){

            System.out.println("    -- Campaign " + Name + " not active. Free player has not been inactive. ("+ inactivity+" days <  " + MIN_INACTIVITY_FREE);
            return null;

        }


        if(user.payments > 0 && inactivity > INACTIVITY_LIMIT_PAYING){

            System.out.println("    -- Campaign " + Name + " not active. Paying player is inactive. ("+ inactivity+" days >  " + INACTIVITY_LIMIT_PAYING);
            return null;

        }

        if(user.payments == 0 && inactivity > MIN_INACTIVITY_PAYING){

            System.out.println("    -- Campaign " + Name + " not active. Paying player has not been inactive. ("+ inactivity+" days <  " + MIN_INACTIVITY_PAYING);
            return null;

        }

        /*

        if(playerInfo.fallbackFromMobile()){

            return getRandomEmail(user, executionTime, responseFactor);

        }

          */

        System.out.println("    -- Campaign " + Name + " firing." );
        count++;


        return getRandomAction(user, executionTime, responseFactor);



    }

    //TODO: Add more actions here


    private ActionInterface getRandomAction(User user, Timestamp executionTime, double responseFactor) {

        int NO_ACTIONS = 4;

        int action = (int)(Math.random() * NO_ACTIONS) + 1;

        switch(action){

            case 1:
                return new MobilePushAction("2,000 free coins extra today. We haven't seen you in a while.",
                        user, executionTime, getPriority(), getTag(),  Name, 300 + action, getState(), responseFactor)
                        .attach(new GiveCoinAction(2000, user, executionTime, getPriority(), Name, 300 + action, getState(), responseFactor)

                        );
            case 2:

                return new MobilePushAction("The big win is out there...",
                        user, executionTime, getPriority(), getTag(),  Name, 300 + action, getState(), responseFactor);

            case 3:

                return new MobilePushAction("Which is your favourite game? Have you tried them all yet?",
                        user, executionTime, getPriority(), getTag(),  Name, 300 + action, getState(), responseFactor);

            case 4:
                return new MobilePushAction("1,000 free coins extra today. Come on and play!",
                        user, executionTime, getPriority(), getTag(),  Name, 300 + action, getState(), responseFactor)
                        .attach(new GiveCoinAction(1000, user, executionTime, getPriority(), Name, 300 + action, getState(), responseFactor)

                        );

            default:
                return null;
        }



    }



    private ActionInterface getRandomEmail(User user, Timestamp executionTime, double responseFactor) {

        int NO_ACTIONS = 1;

        int action = (int)(Math.random() * NO_ACTIONS) + 1;
        String promoCode;

        switch(action){


            case 1:
                promoCode = createPromoCode(201);
                return new EmailAction(

                        new NotificationEmail("where did you go?", "<p>Don't miss out on all the new game releases here at Slot America. We try to put out a new prime game for you every week and you have some new games to check out!</p>" +
                        "<p> Why don't you come in and use your free bonus to try them? Click <a href=\"https://apps.facebook.com/slotAmerica/?promocode="+promoCode+"\">here</a> to test it out :-) </p>",

                        "Hello "+ user.name+" Don't miss out on all the new game releases here at Slot America. We try to put out a new prime game for you every week and you have some new games to check out." +
                        "Why don't you come in and use your free bonus to try them?"),

                        user, executionTime, getPriority(), getTag(), 201, getState(), responseFactor);

            default:
                return null;
        }



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
