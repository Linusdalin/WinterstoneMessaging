package campaigns;

import action.ActionInterface;
import action.MobilePushAction;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;
import response.ResponseStat;
import rewards.Reward;
import rewards.RewardRepository;

import java.sql.Timestamp;


/************************************************************************'
 *
 *          Giving free spins to players that have not tried a specific game
 *
 */

public class SuperTuesdayCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "superTuesday";
    private static final int CoolDown_Days = 300;            // Just once

    private static final int DailyCap = 1000;


    // Trigger specific config data
    private static final int Min_Sessions       =  5;
    private static final int Max_Age            =  400;              // inclusive
    private static final int Min_Age            =  0;              // inclusive

    private static final int MAX_INACTIVITY     =  50;              // inclusive


    private int count = 0;
    private String day = null;

    SuperTuesdayCampaign(int priority, CampaignState active){

        super(Name, priority, active);
        setCoolDown(CoolDown_Days);
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


        if(user.email == null || user.email.equals("")){

            System.out.println("    -- Campaign " + Name + " not applicable. No email address" );
            return null;

        }


        int age = getDaysBetween(user.created, executionDay );

        if(age < Min_Age || age > Max_Age){

            System.out.println("    -- Campaign " + Name + " not applicable. Age "+ age+" is outside range (" + Min_Age + " - " + Max_Age + ")" );
            return null;

        }

        if(user.sessions < Min_Sessions){

            System.out.println("    -- Campaign " + Name + " not applicable. User is not frequent enough (" + user.sessions + " < " + Min_Sessions + ")" );
            return null;

        }


        if(isHighSpender(user)){

            System.out.println("    -- Campaign " + Name + " not applicable. Not for high spenders." );
            return null;

        }


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



        count++;


        if(playerInfo.getUsageProfile().isMobilePlayer()){

            if(playerInfo.fallbackFromMobile()){

                System.out.println("    -- Campaign " + Name + " NOT firing Email" );
                return null;

            }
            else{

                System.out.println("    -- Campaign " + Name + " firing. Mobile push" );
                return new MobilePushAction("We here at SlotAmerica are missing you! The thrilling slot machines are awaiting and you can use the FREE bonus to find your favorite game! Click here to get started",
                        user, executionTime, getPriority(), getTag(), Name, 301, getState(), responseFactor);


            }
        }

        if(!isPaying(user)){

            System.out.println("    -- Campaign " + Name + " NOT firing Notification" );
            return null;

        }

        System.out.println("    -- Campaign " + Name + " firing. Notification" );
        return new NotificationAction("It is Super Tuesday. Play your favorite game to nominate for SlotAmerica best game. Click here to play",
                user, executionTime, getPriority(), getTag(), Name, 1, getState(), responseFactor);


    }





    /*********************************************************************
     *
     *              Campaign timing restrictions
     *
     * @param executionTime     - time of execution
     * @return                  - messgage or null if ok.
     */

    public String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {

        String specificWeekDay = isSpecificDay(executionTime, false, day);

        if(specificWeekDay != null)
            return specificWeekDay;


        return isTooEarly(executionTime, overrideTime);

    }

    protected Reward decideReward(User user) {

        if(isHighSpender(user))
            return RewardRepository.OS2345High;

        if(isPaying(user))
            return RewardRepository.OS2345Paying;

        if(isFrequent(user))
            return RewardRepository.OS2345Frequent;

        return RewardRepository.OS2345Rest;
    }




}
