package campaigns;

import action.ActionInterface;
import action.EmailAction;
import action.NotificationAction;
import core.PlayerInfo;
import email.EmailInterface;
import email.NotificationEmail;
import recommendation.GameRecommendation;
import recommendation.GameRecommender;
import remoteData.dataObjects.User;

import java.sql.Timestamp;


/************************************************************************'
 *
 *              Sending a message to players that have not played the new game
 *
 */

public class GameActivationCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "GameActivationPoke";
    private static final int CoolDown_Days = 15;
    private int[] MessageIds = {1, 2};


    // Trigger specific config data
    private static final int Min_Sessions = 12;
    private static final int Min_Inactivity = 6;
    private static final int Max_Inactivity = 100;
    private static final int Max_Inactivity_Notification = 32;

    GameActivationCampaign(int priority, CampaignState active){

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
        GameRecommendation gameRecommendation = recommender.getGameRecommendation();

        if(gameRecommendation == null){

            System.out.println("    -- Campaign " + Name + " not firing. No game to promote" );
            return null;

        }

        if(inactivity < Max_Inactivity_Notification){

            System.out.println("    -- Sending a game recommendation \"" + gameRecommendation.getRecommendation() + "\n" );
            return new NotificationAction("We have a new game for you! Check out " + gameRecommendation.getRecommendation(),
                    user, executionTime, getPriority(), getTag(),  Name, 1, getState(), responseFactor)
                    .withGame(gameRecommendation.getCode());

        }
        else{

            // Sending a mail instead

            System.out.println("    -- Sending an EMAIL  game recommendation \"" + gameRecommendation.getRecommendation() + "\n" );
            return new EmailAction(gameActivationEmail(user, gameRecommendation), user, executionTime, getEmailPriority(), getTag(), 2, getState(), responseFactor);



        }

    }

    private EmailInterface gameActivationEmail(User user, GameRecommendation recommendation) {
        return new NotificationEmail("we have a recommendation for you", "<p>Don't miss out the new game we released here at Slot America. We think you will like it...</p>" +
                "<p> Check out <a href=\"https://apps.facebook.com/slotAmerica/?game="+recommendation.getCode()+"promocode=EGameActivation-2\">"+ recommendation.getRecommendation()+"</a></p>",
                "Hello "+ user.name+" Don't miss out the new game we released here at Slot America. We think you will like it...");
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
