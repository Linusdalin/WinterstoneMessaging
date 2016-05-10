package balanceAnalysis;

import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2016-04-19
 * Time: 16:05
 * To change this template use File | Settings | File Templates.
 */
public class Game {

    public final String playerId;
    public final Timestamp startTime;
    public final Timestamp endTime;
    public final int startBalance;
    public final int endBalance;
    public final int outcome;

    Game(String playerId, Timestamp startTime, Timestamp endTime, int startBalance,int endBalance, int outcome){

        this.playerId = playerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startBalance = startBalance;
        this.endBalance = endBalance;
        this.outcome = outcome;
    }
}