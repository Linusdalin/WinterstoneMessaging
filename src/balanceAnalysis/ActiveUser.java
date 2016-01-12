package balanceAnalysis;

import remoteData.dataObjects.GameSession;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2016-01-05
 * Time: 13:14
 * To change this template use File | Settings | File Templates.
 */
public class ActiveUser {


    private final String facebookId;
    private GameSession last;

    ActiveUser(String facebookId, GameSession last){


        this.facebookId = facebookId;
        this.last = last;
    }

    public GameSession getLast() {
        return last;
    }

    public void setLast(GameSession last) {
        this.last = last;
    }

    public String getFacebookId() {
        return facebookId;
    }
}
