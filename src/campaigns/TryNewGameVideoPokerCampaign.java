package campaigns;

import action.ActionInterface;
import action.EmailAction;
import core.PlayerInfo;
import email.EmailInterface;
import email.NotificationEmail;
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

public class TryNewGameVideoPokerCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Game = "jacks_or_better";
    private static final String GameName = "Jacks or Better";
    private static final String Name = "TryNewGame"+Game;
    private static final int CoolDown_Days = 9999;            // Just once per game
    private int[] MessageIds = {1, 2, 3,
                                10
    };

    private static final int DailyCap = 1000;            // Just once per game


    // Trigger specific config data
    private static final int Min_Sessions = 15;
    private static final int Min_Inactivity1 = 5;                          // Active players
    private static final int Min_Inactivity2 = 15;                         // Lapsing players
    private static final int Min_Inactivity3 = 60;                         // Lapsed players
    private static final int Max_Inactivity = 200;


    private int count = 0;

    TryNewGameVideoPokerCampaign(int priority, CampaignState active){

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


        if(count >= DailyCap){

            System.out.println("    -- Campaign " + Name + " not applicable. Daily Count reach (" + DailyCap + ")" );
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

            System.out.println("    -- Campaign " + Name + " not firing. User is active (" + inactivity + " >" + Min_Inactivity1 + ")" );
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

        if(user.balance < 20000){

            System.out.println("    -- Campaign " + Name + " not firing. Not enough coins (" + user.balance + ")" );
            return null;
        }


        if(inactivity > Min_Inactivity3 && inactivity<= Max_Inactivity){


            count++;
            return new EmailAction(gameActivationEmail(user), user, executionTime, getPriority(), getTag(), 10, getState(), responseFactor);

        }

        /*

        int messageId = 2;
        if(inactivity > Min_Inactivity2 && inactivity <= Min_Inactivity3){

            messageId = 3;
        }

        System.out.println("    -- Sending reminder for game " + Game + "\n" );
        return new NotificationAction("Have you missed the SlotAmerica Video Poker? There is a short strategy guide on the SlotAmerica Page. Click here test it out",
                user, executionTime, getPriority(), getTag(),  Name, messageId, getState(), responseFactor)
                .withGame(Game);

            */

        return null;
    }



    public static EmailInterface gameActivationEmail(User user) {

        return new NotificationEmail("Have you tried our Video Poker?", "<p>Just for fun, there is a Jacks or better Video Poker at SlotAmerica. It is with a real classic style, bringing you back to down town Vegas.</p>" +
                "<p>When playing video poker it is good to understand the strategy. In some cases, the game suggests which cards to hold but in others you have to select it manually. " +
                        "There are different more or less complex strategies for video poker, but with a few very simple rules, you can easily improve your outcome. Just apply the following rules in the given order:</p>" +
                "<li>If the machine suggests which cards to hold, keep them.</li>" +
                "<li>Hold four cards to a flush or a open ended straight. (Four cards in a row)</li>" +
                "<li>If you have a small pair, hold it. You hope to improve your pair</li>" +
                "<li>Hold two or more high suited cards. (Hope to get the straight flush)</li>" +
                "<li>Hold one high card. (Hoping to make a high pair</li>" +
                "<p>With just these simple rules, you will play fairly well in the Video Poker game. </p>" +
                "<p> Just click here <a href=\"https://apps.facebook.com/slotAmerica/?game="+ Game +"&xpromocode="+ Name+"\"> to test out your new skills</a></p>",
                "Just for fun, there is a Jacks or better Video Poker at SlotAmerica. It is with a real classic style, bringing you back to down town Vegas");
    }




    /*********************************************************************
     *
     *              Campaign timing restrictions
     *
     * @param executionTime     - time of execution
     * @return                  - messgage or null if ok.
     */

    public String testFailCalendarRestriction(Timestamp executionTime, boolean overrideTime) {

        String specificWeekDay = isSpecificDay(executionTime, false, "måndag");

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
