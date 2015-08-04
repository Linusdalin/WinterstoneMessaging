package remoteData.dataObjects;

import java.sql.Timestamp;

/**
 *
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-04-16
 * Time: 13:10
 * To change this template use File | Settings | File Templates.
 */
public class GameSession {


    public final Timestamp timeStamp;
    public final String sessionId;
    public final String game;
    public final String facebookId;
    public final String name;
    public final String promocode;
    public final String fbSource;
    public final Timestamp actionTime;
    public final int totalWager;
    public final int totalWin;
    public final int endBalance;
    public final int spins;
    public final int totalSpins;

    GameSession(Timestamp timeStamp, String sessionId, String game, String facebookId, String name, String promocode, String fbSource, Timestamp actionTime, int totalWager, int totalWin, int endBalance, int spins, int totalSpins){
        this.timeStamp = timeStamp;
        this.sessionId = sessionId;
        this.game = game;
        this.facebookId = facebookId;
        this.name = name;
        this.promocode = promocode;
        this.fbSource = fbSource;
        this.actionTime = actionTime;
        this.totalWager = totalWager;
        this.totalWin = totalWin;
        this.endBalance = endBalance;
        this.spins = spins;
        this.totalSpins = totalSpins;
    }

    public String toString(){

        return "(" + timeStamp.toString() +", "+ sessionId + ", " +game + ", " +facebookId + ", " +name + ", " +promocode +", " +fbSource +", " +actionTime.toString() +", " +
                    totalWager +", " +totalWin +", " +endBalance +", " +spins +", " +totalSpins +")";

    }

    public String toSQLValues() {

        return "'" + timeStamp.toString() +"', '"+ sessionId + "', '" +game + "', '" +facebookId + "', '" +name + "', '" +promocode +"', '" +fbSource +"', '" +actionTime.toString() +"', " +
                    totalWager +", " +totalWin +", " +endBalance +", " +spins +", " +totalSpins;

    }
}
