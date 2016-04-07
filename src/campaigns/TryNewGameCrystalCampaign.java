package campaigns;

import action.ActionInterface;
import action.EmailAction;
import action.NotificationAction;
import core.PlayerInfo;
import email.EmailInterface;
import email.NotificationEmail;
import recommendation.GameRecommender;
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

public class TryNewGameCrystalCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Game = "crystal";
    private static final String GameName = "Crystal";
    private static final String Name = "TryNewGame"+Game;
    private static final int CoolDown_Days = 9999;            // Just once per game

    // Trigger specific config data
    private static final int Min_Sessions    =  20;
    private static final int Min_Inactivity1 =   3;                          // Active players
    private static final int Min_Inactivity2 =  15;                         // Lapsing players
    private static final int Min_Inactivity3 =  50;                         // Lapsed players
    private static final int Max_Inactivity  = 150;
    private String day;

    TryNewGameCrystalCampaign(int priority, CampaignState active, String day){

        super(Name, priority, active);
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


        if(playerInfo.getUsageProfile().isMobilePlayer()){

            System.out.println("    -- Campaign " + Name + " not firing. Not for mobile players");
            return null;
        }

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

        if(inactivity <  Min_Inactivity1){

            System.out.println("    -- Campaign " + Name + " not firing. User is active (" + inactivity + " <" + Min_Inactivity1 + ")" );
            return null;
        }

        if(inactivity >  Max_Inactivity){

            System.out.println("    -- Campaign " + Name + " not firing. User inactive too long. (" + inactivity + " >" + Max_Inactivity + ")" );
            return null;
        }



        GameRecommender recommender = new GameRecommender(playerInfo, executionTime);
        boolean hasTried = recommender.hasTried(Game);

        if(hasTried){

            System.out.println("    -- Campaign " + Name + " not firing. Player already tried " + Game );
            return null;

        }

        // Decide on a reasonable Freespin promotion

        Reward reward = decideReward(playerInfo.getUser());

        if(playerInfo.hasClaimed(reward)){

            System.out.println("    -- Campaign " + Name + " not firing. Player already claimed the reward " + Game );
            return null;
        }


        if(inactivity > Min_Inactivity3 && inactivity<= Max_Inactivity){

            return new EmailAction(gameActivationEmail(user, reward, createPromoCode(201)), user, executionTime, getPriority(), getTag(), 201, getState(), responseFactor);

        }

        int messageId = 2;
        if(inactivity > Min_Inactivity2 && inactivity <= Min_Inactivity3){

            messageId = 3;
        }

        messageId = tagMessageIdTimeOfDay(messageId, executionTime);


        System.out.println("    -- Sending freespin offer for game " + Game + "\n" );
        return new NotificationAction("We have added " + reward.getCoins() + " free spins for you in our favourite game " + GameName + ". click here to claim and try it out for free!",
                user, executionTime, getPriority(), getTag(),  Name, messageId, getState(), responseFactor)
                .withGame(Game)
                .withReward(reward);

    }



    public static EmailInterface gameActivationEmail(User user, Reward reward, String promoCode) {

        return new NotificationEmail("We have a special game for you", "<p>Don't miss out one of the most liked games at SlotAmerica. It is a sparkling version of our Old School games.  " +
                "We really think you will like it. We have added "+ reward.getCoins()+" free spins for you to try it out!</p>" +
                "<p> Just click here <a href=\"https://apps.facebook.com/slotAmerica/?game="+Game+"&promoCode="+ promoCode+"&reward="+reward.getCode() + "\"> to claim your spins</a></p>" +
                "<p>Please also note that the Crystal game is availaible on mobile too!</p>",
                        "Hello "+ user.name+" Don't miss out the Old School Crystal game we released here at Slot America. We think you will like it...");
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


        return isTooEarlyForUser(playerInfo, executionTime, overrideTime);

    }

    protected Reward decideReward(User user) {

        if(isHighSpender(user))
            return RewardRepository.CrystalHigh;

        if(isPaying(user))
            return RewardRepository.CrystalPaying;

        if(isFrequent(user))
            return RewardRepository.CrystalFrequent;

        return RewardRepository.CrystalRest;
    }




}
