package campaigns;

import action.ActionInterface;
import action.EmailAction;
import action.MobilePushAction;
import core.PlayerInfo;
import email.EmailInterface;
import email.NotificationEmail;
import remoteData.dataObjects.User;
import response.ResponseStat;
import rewards.Reward;

import java.sql.Timestamp;


/************************************************************************'
 *
 *          Giving free spins to players that have not tried a specific game
 *
 */

public class MysteryMondayMobileCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "MysteryMonday";
    private static final int CoolDown_Days = 6;            // Just once per week

    // Trigger specific config data
    private static final int Min_Sessions = 10;

    private static final int Min_Inactivity = 3;                          // Active players - no message
    private static final int Max_Inactivity  = 90;
    private Reward reward;
    private final String day;

    MysteryMondayMobileCampaign(int priority, CampaignState active, Reward reward, String day){

        super(Name, priority, active);
        this.reward = reward;
        this.day = day;
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

        if(inactivity <  Min_Inactivity){

            System.out.println("    -- Campaign " + Name + " not firing. User is active (" + inactivity + " <" + Min_Inactivity + ")" );
            return null;
        }

        if(inactivity >  Max_Inactivity){

            System.out.println("    -- Campaign " + Name + " not firing. User inactive too long. (" + inactivity + " >" + Max_Inactivity + ")" );
            return null;
        }


        if(!playerInfo.getUsageProfile().isMobilePlayer()){

            System.out.println("    -- Campaign " + Name + " not firing. Only mobile players" );
            return null;

        }

        if(playerInfo.hasClaimed(reward)){

            System.out.println("    -- Campaign " + Name + " not firing. Player already claimed the reward " );
            return null;
        }


        System.out.println("    -- Campaign " + Name + " firing. Sending mystery monday offer" );

        if(!playerInfo.fallbackFromMobile()){

                return new MobilePushAction("Mystery Monday reward! Freespins!", user, executionTime, getPriority(), getTag(), Name,  301, getState(), responseFactor)
                        .withReward(reward);
        }

        return new EmailAction(mysteryMondayEmail(user, reward), user, executionTime, getPriority(), getTag(), 201, getState(), responseFactor);

    }



    public static EmailInterface mysteryMondayEmail(User user, Reward reward) {

        return new NotificationEmail("It is Mystery Monday",
                "<p>Don't miss out on the mystery free coin rewards at the SlotAmerica app page. Every now and then there are surprise free coins. Remember to like the page not to miss out on all the free play!</p>" +
                "<p> Just click here <a href=\"https://www.facebook.com/slotAmerica/\"> to find more</a></p>",
                "Hello "+ user.name+" Don't miss out on the mystery free coin rewards at the SlotAmerica app page. Every now and then there are surprise free coins. Remember to like the page not to miss out on all the free play!");
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



}
