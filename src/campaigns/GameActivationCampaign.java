package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
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
    private static final int CoolDown_Days = 10;

    // Trigger specific config data
    private static final int Min_Sessions = 20;
    private static final int Max_Inactivity = 30;

    GameActivationCampaign(int priority, CampaignState active){

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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime) {

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


        System.out.println("    -- Sending a game recommendation \"" + gameRecommendation.getRecommendation() + "\n" );
        return new NotificationAction("We have a new game for you! Check out " + gameRecommendation.getRecommendation(),
                user, getPriority(), getTag(),  Name, 1, getState())
                .withGame(gameRecommendation.getCode());




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
