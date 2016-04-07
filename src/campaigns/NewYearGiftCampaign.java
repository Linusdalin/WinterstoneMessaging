package campaigns;

import action.ActionInterface;
import action.EmailAction;
import action.MobilePushAction;
import action.NotificationAction;
import core.PlayerInfo;
import core.UsageProfileClassification;
import email.EmailInterface;
import email.NotificationEmail;
import remoteData.dataObjects.User;
import response.ResponseStat;
import rewards.Reward;
import rewards.RewardRepository;

import java.sql.Timestamp;


/************************************************************************'
 *
 *          Sending out a message to the mobile players about a new game release
 */

public class NewYearGiftCampaign extends AbstractMobileCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "NewYear";
    private static final int CoolDown_Days = 360;     // Only Once per year
    private int[] MessageIds = { 1, 2,
            22, 23,
            31};


    private static final int MIN_SESSIONS = 10;
    private static final int MAX_INACTIVITY_FREE = 20;
    private static final int MAX_INACTIVITY_PAYING = 90;

    NewYearGiftCampaign(int priority, CampaignState activation){

        super(Name, priority, activation);
        setCoolDown(CoolDown_Days);
        registerMessageIds( MessageIds );

    }



    /**************************************************************************
     *
     *              Decide on the campaign
     *
     * @param playerInfo             - the user to evaluate
     * @param executionTime          - when
     * @param responseFactor
     * @return                       - resulting action. (or null)
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {


        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();



        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }

        if(user.sessions < MIN_SESSIONS){

            System.out.println("    -- Campaign " + Name + " not firing. Not enough sessions");
            return null;

        }



        int inactivity = getDaysBetween(lastSession, executionDay);

        if(inactivity > MAX_INACTIVITY_PAYING){

            System.out.println("    -- Campaign " + Name + " not firing. User is inactive.");
            return null;

        }


        UsageProfileClassification classification = playerInfo.getUsageProfile();
        Reward reward = getNewYearRewardForUser(user);

        if(classification.isMobilePlayer()){

            System.out.println("    -- Campaign " + Name + " firing for mobil )");
            return  new MobilePushAction("2015 is coming to a close! Here is a final " + reward.getCoins() + " free coins before we move into 2016!",
                    user, executionTime, getPriority(), getTag(), Name,  301, getState(), responseFactor)
                    .withReward(reward);

        }

        if(isPaying(user)){


            System.out.println("    -- Campaign " + Name + " firing");
            return new NotificationAction( user.name +", 2015 is coming to a close! Here is a final " + reward.getCoins() + " free coins before we move into 2016!",
                    user, executionTime, getPriority(), getTag(),  Name, 1, getState(), responseFactor)
                    .withReward(RewardRepository.freeCoinAcitivation);


        }
        else{

            if(inactivity < MAX_INACTIVITY_FREE){

                System.out.println("    -- Campaign " + Name + " firing");

                return new NotificationAction( user.name +", 2015 is coming to a close! Here is a final " + reward.getCoins() + " free coins before we move into 2016!",
                        user, executionTime, getPriority(), getTag(),  Name, 2, getState(), responseFactor)
                        .withReward(RewardRepository.freeCoinAcitivation);

            }
            else{

                return new EmailAction(newYearEmail(user, createPromoCode(201), reward), user, executionTime, getPriority(), getTag(), 201, getState(), responseFactor);

            }

        }

    }

    public static EmailInterface newYearEmail(User user, String tag, Reward reward) {
        return new NotificationEmail( reward.getCoins() + " coins to celebrate the great SlotAmerica 2015",
                "<p>2015 is coming to a close. It has been a super year for SlotAmerica and we really want to take this opportunity to thank our our players.</p>" +
                "<p>A lot of games have passed through the SlotAmerica app during the year, and of course more will come in 2016. Some new and of course some old favourites will also return during the next year!</p>" +
                "<p>If anyone has missed it, the SlotAmerica games are also available on iPhone and iPad. To download it, you can go to the App Store and search for SlotAmerica or simply click <a href=\""+ GameLink+tag+"\">here</a></p>" +
                "There are still some coins left in the 2015 treasure chest here are Slot America. Why don't you come in and use them to re-experience your favourite games from 2015. " +
                    "Click <a href=\"https://apps.facebook.com/slotAmerica/?promoCode="+tag+"&reward="+reward.getCode()+"\">here</a> to get your "+ reward.getCoins()+" coins :-) </p>",
                "now 2015 comes to a close. It has been a super year for SlotAmerica and we really want to take this opportunity to thank our our players." +
                        "Why don't you come in and use your free bonus to try them?");
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


    protected Reward getNewYearRewardForUser(User user) {


        if(isPaying(user))
            return RewardRepository.newYearPaying;


        return RewardRepository.newYearFree;

    }



}
