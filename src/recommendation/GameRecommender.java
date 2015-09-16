package recommendation;

import campaigns.AbstractCampaign;
import core.PlayerInfo;
import java.sql.Timestamp;

/*****************************************************************'
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-09-13
 * Time: 15:46
 * To change this template use File | Settings | File Templates.
 */

public class GameRecommender {

    //Games to recommend in priority order


    GameRecommendation[] games = {
            new GameRecommendation("the new Wild Cherries game. Click here to experience the cherry blossom!", "wild_cherries", "2015-09-03 12:00:00"),
    };

    private PlayerInfo playerInfo;
    private Timestamp executionTime;

    public GameRecommender(PlayerInfo playerInfo, Timestamp executionTime){

        this.playerInfo = playerInfo;
        this.executionTime = executionTime;
    }


    /************************************************************************************
     *
     *              Get a game recommendation.
     *
     *              Now this is only based on the player not being active since the game release.
     *              There is also a cap of 40 days. After that retention should rather be resurrection with free coins (could be increased)
     *
     *
     *
     *
     *
     * @return    - a recommendation of a game (or null for no recommendation)
     */

    public GameRecommendation getGameRecommendation() {

        Timestamp lastSession = playerInfo.getLastSession();

        if(lastSession == null){
            return null;

        }

        // Go through all games to see if there is one that fits.

        for (GameRecommendation recommendation : games) {

            int daysSinceGameLaunch = AbstractCampaign.getDaysBetween(recommendation.getLaunchDate(), executionTime);
            int inactivitySinceGameLaunch = AbstractCampaign.getDaysBetween(lastSession, recommendation.getLaunchDate());
            System.out.println("  ( Getting days between game launch and last session: " + inactivitySinceGameLaunch + ")");

            if(daysSinceGameLaunch < 4){

                System.out.println("  -- Not generating recommendation as the game is so resently launched (" + daysSinceGameLaunch + " days )");
                return recommendation;

            }

            if(inactivitySinceGameLaunch > 0 && inactivitySinceGameLaunch < 40){

                System.out.println("  -- Found recommendation " + recommendation.getRecommendation());
                return recommendation;

            }

        }

        return null;
    }
}