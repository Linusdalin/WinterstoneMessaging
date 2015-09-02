package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;

import java.sql.Timestamp;
import java.util.Calendar;


/************************************************************************'
 *
 *              The getting started campaign is sending messages to
 *              players that has only installed and played one session
 */

public class GettingStartedCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Getting Started";
    private static final int CoolDown_Days = 1;   // No real cool down. This will anyway only trigger once per message

    GettingStartedCampaign(int priority, CampaignState active){

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
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime) {


        //System.out.println("Registration Date: " + getDay(user.created).toString());
        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        if( user.amount == 0 && user.lastgamePlayed.equals("")){

            int sessions = (user.sessions > 1 ? 2 : 1 );


            if(daysBefore(user.created, executionDay, 1 )){

                System.out.println("    -- Campaign " + Name + " Running message 1 for " + user.name );
                return new NotificationAction("Remember you get an extra diamond pick for every day in a row you are playing. The games are waiting. Click here for your free bonus!",
                        user, getPriority(), "GettingStarted1-" + sessions , Name, 1, getState());

            }

            /*

            if(daysBefore(user.created, executionDay, 2 )){

                System.out.println("    -- Campaign " + Name + " Running message 2 for " + user.name );
                return new NotificationAction("You have friends here at Slot America waiting for you to really get going. Click here to collect your free coins.", user, 90, "GettingStarted1", "GettingStarted1", Name);

            }

            */

            if(daysBefore(user.created, executionDay, 3 )){

                System.out.println("    -- Campaign " + Name + " Running message 3 for " + user.name );
                return new NotificationAction("We here at SlotAmerica are missing you! The thrilling slot machines are awaiting and you can use the FREE bonus to find your favorite game! Click here to get started",
                        user, getPriority(), "GettingStarted2-" + sessions, Name, 3, getState());

            }

            /*
            if(daysBefore(user.created, executionDay, 5 )){

                System.out.println("    -- Campaign " + Name + " Running message 4 for " + user.name );
                return new NotificationAction("SlotAmerica has the best free slot experience on Facebook. Click here to get started", user, 90, "GettingStarted3", "GettingStarted3", Name);

            }

              */

            System.out.println("    -- Campaign " + Name + " not applicable for player" + user.name + ". Timing is not correct" );
            return null;

        }
        else{

            System.out.println("    -- Campaign " + Name + " not applicable for player" + user.name );
            return null;
        }


    }

    /****************************************************************
     *
     *              Test should be exactly n calender days before reference
     *
     * @param test                   - the date to test
     * @param reference              - reference date
     * @param days                   - how many days
     * @return                       - is it on that day
     */

    public static boolean daysBefore(Timestamp test, Timestamp reference, int days) {

        reference = getDay(new Timestamp(reference.getTime() - 24*3600*1000*days));
        test = getDay(test);

        return test.equals(reference);

    }

    public static Timestamp getDay(Timestamp fecha) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime( fecha );
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return new Timestamp(calendar.getTime().getTime());

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
