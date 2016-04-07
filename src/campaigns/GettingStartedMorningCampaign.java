package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;
import response.ResponseStat;

import java.sql.Timestamp;
import java.util.Calendar;


/************************************************************************'
 *
 *              The getting started campaign is sending messages to
 *              players that has only installed and played one session
 */

public class GettingStartedMorningCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "GettingStarted";
    private static final int CoolDown_Days = 99999;   // Only once


    GettingStartedMorningCampaign(int priority, CampaignState active){

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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {


        //System.out.println("Registration Date: " + getDay(user.created).toString());
        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        if(user.amount != 0 || user.lastgamePlayed== null || user.lastgamePlayed.equals("")){

            System.out.println("    -- Campaign " + Name + " not Firing. Player already got going");
            return null;

        }


        int age = getDaysBetween(user.created, executionDay );



        if(age == 1 || age == 2){

            System.out.println("    -- Campaign " + Name + " Running message 1 for " + user.name );
            int messageId = 81;
            if(!registeredInTheMorning(user))
                messageId = 82;


            if(playerInfo.getUsageProfile().isMobilePlayer()){

                System.out.println("    -- Campaign " + Name + " not Firing. Mobile player");
                return null;
            }

            if(randomize4(user, 1) || randomize4(user, 3)){


                return new NotificationAction("The free coins are waiting. You get an extra diamond pick for every day in a row you are playing. Click here now!",
                    user, executionTime, getPriority(), getTag() , Name, messageId, getState(), responseFactor);

            }
            else{

                System.out.println("    -- Campaign " + Name + " not Firing. Player not in morning test");
                return null;

            }

        }
        return null;
    }



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

    public String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {

        return isTooLate(executionTime, overrideTime);
    }




}
