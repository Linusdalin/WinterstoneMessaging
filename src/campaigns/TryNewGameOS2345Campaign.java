package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import recommendation.GameRecommender;
import remoteData.dataObjects.User;
import rewards.Reward;
import rewards.RewardRepository;

import java.sql.Timestamp;


/************************************************************************'
 *
 *          Giving free spins to players that have not tried a specific game
 *
 */

public class TryNewGameOS2345Campaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Game = "os2345x";
    private static final String GameName = "Old School 2x3x4x5x";
    private static final String Name = "TryNewGame"+Game;
    private static final int CoolDown_Days = 9999;            // Just once per game
    private int[] MessageIds = {1, 2};


    // Trigger specific config data
    private static final int Min_Sessions = 30;
    private static final int Min_Inactivity = 5;
    private static final int Max_Inactivity = 32;
    private static final int SwitchToEmail = 9999;   //TODO: Optimize this

    TryNewGameOS2345Campaign(int priority, CampaignState active){

        super(Name, priority, active);
        setCoolDown(CoolDown_Days);
        registerMessageIds( MessageIds );
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


    public ActionInterface  evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor) {

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

            System.out.println("    -- Campaign " + Name + " not firing. User is active (" + inactivity + " >" + Min_Inactivity + ")" );
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

        if(!RewardRepository.hasClaimed(user, reward)){

            System.out.println("    -- Campaign " + Name + " not firing. Player already claimed freespin reward for " + Game );
            return null;

        }


         if(inactivity < SwitchToEmail){

            System.out.println("    -- Sending freespin offer for game " + Game + "\n" );
            return new NotificationAction("We have added " + reward.getCoins() + " free spins for you in the game " + GameName + " click here to claim and play for free!",
                    user, executionTime, getPriority(), getTag(),  Name, 1, getState(), responseFactor)
                    .withGame(Game)
                    .withReward(reward);

        }
        else{

            // Sending a mail instead

            System.out.println("    -- NO EMAIL IMPLEMENTED for freespin offer \"" + Game + "\n" );
            return null;


        }

    }

    /*

    private EmailInterface gameActivationEmail(User user, GameRecommendation recommendation) {
        return new NotificationEmail("we have a recommendation for you", "<p>Don't miss out the new game we released here at Slot America. We think you will like it...</p>" +
                "<p> Check out <a href=\"https://apps.facebook.com/slotAmerica/?game="+recommendation.getCode()+"promocode=EGameActivation-2\">"+ recommendation.getRecommendation()+"</a></p>",
                "Hello "+ user.name+" Don't miss out the new game we released here at Slot America. We think you will like it...");
    }

*/


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
