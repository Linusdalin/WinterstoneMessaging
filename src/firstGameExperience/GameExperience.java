package firstGameExperience;

import remoteData.dataObjects.GameSession;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-12-09
 * Time: 15:36
 * To change this template use File | Settings | File Templates.
 */
public class GameExperience {

    public final GameSession startSession;
    public final int totalGamePayments;
    public final int totalGameSessions;

    public GameExperience(GameSession startSession, int totalGamePayments, int totalGameSessions){

        this.startSession = startSession;
        this.totalGamePayments = totalGamePayments;
        this.totalGameSessions = totalGameSessions;
    }

}
