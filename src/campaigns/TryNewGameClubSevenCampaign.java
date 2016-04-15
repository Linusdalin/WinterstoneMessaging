package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
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

public class TryNewGameClubSevenCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Game = "club_seven";
    private static final String GameName = "Club Seven";
    private static final String Name = "TryNewGame"+Game;
    private static final int CoolDown_Days = 9999;            // Just once per game

    // Trigger specific config data
    private static final int Min_Sessions       =  20;
    private static final int Min_Inactivity1    =   2;                          // Non paying players
    private static final int Min_Inactivity2    =  100;                          // paying players
    private String dayRestriction;

    TryNewGameClubSevenCampaign(int priority, CampaignState active, String dayRestriction){

        super(Name, priority, active);
        this.dayRestriction = dayRestriction;
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
        boolean isPaying = isPaying(user);

        if(inactivity >  Min_Inactivity1 && !isPaying){

            System.out.println("    -- Campaign " + Name + " not firing. Non paying user is inactive (" + inactivity + " <" + Min_Inactivity1 + ")" );
            return null;
        }

        if(inactivity >  Min_Inactivity2 && isPaying){

            System.out.println("    -- Campaign " + Name + " not firing. Paying user is inactive (" + inactivity + " <" + Min_Inactivity1 + ")" );
            return null;
        }



        GameRecommender recommender = new GameRecommender(playerInfo, executionTime);
        boolean hasTried = recommender.hasTried(Game);

        if(hasTried){

            System.out.println("    -- Campaign " + Name + " not firing. Player already tried " + Game );
            return null;

        }

        // Decide on a reasonable Freespin promotion

        Reward reward = RewardRepository.clubSeven;

        int messageId = 2;
        messageId = tagMessageIdTimeOfDay(messageId, executionTime);


        System.out.println("    -- Sending freespin offer for game " + Game + "\n" );
        return new NotificationAction("Check out our five reel “Club Seven”, with art inspired by the “Mad Men” era. Royal Wilds and a free spins bonus. Get " + reward.getCoins() + " free spins here!",
                user, executionTime, getPriority(), getTag(),  Name, messageId, getState(), responseFactor)
                .withGame(Game)
                .withReward(reward);

    }



    /*********************************************************************
     *
     *              Campaign timing restrictions
     *
     * @param executionTime     - time of execution
     * @return                  - messgage or null if ok.
     */

    public String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {

        String specificWeekDay = isSpecificDay(executionTime, false, dayRestriction);

        if(specificWeekDay != null)
            return specificWeekDay;


        return isTooEarlyForUser(playerInfo, executionTime, overrideTime);

    }

    protected Reward decideReward(User user) {

        if(isHighSpender(user))
            return RewardRepository.ClockworkHigh;

        if(isPaying(user))
            return RewardRepository.ClockworkPaying;

        if(isFrequent(user))
            return RewardRepository.ClockworkFrequent;

        return RewardRepository.ClockworkRest;
    }




}
