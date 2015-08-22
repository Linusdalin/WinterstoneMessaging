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

      /*
                       timeStamp datetime
                       sessionId varchar(45)
                       game varchar(45)
                       facebookId varchar(45)
                       name varchar(45)
                       promoCode varchar(80)
                       fbSource varchar(45)
                       actionTime datetime
                       totalWager int(11)
                       totalWin int(11)
                       endBalance int(11)
                       spins int(11)
                       totalSpins int(11)
                        */

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

    GameSession(Timestamp timeStamp, String sessionId, String game, String facebookId, String name, String promocode, String fbSource, Timestamp actionTime, int totalWager, int totalWin, int endBalance, int spins){
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
    }

    public String toString(){

        return "(" + timeStamp.toString() +", "+ sessionId + ", " +game + ", " +facebookId + ", " +name + ", " +promocode +", " +fbSource +", " +actionTime.toString() +", " +
                    totalWager +", " +totalWin +", " +endBalance +", " +spins +", " +")";

    }

    public String toSQLValues() {

        return "'" + timeStamp.toString() +"', '"+ sessionId + "', '" +game + "', '" +facebookId + "', '" +name + "', '" +promocode +"', '" +fbSource +"', '" +actionTime.toString() +"', " +
                    totalWager +", " +totalWin +", " +endBalance +", " +spins;

    }
}
