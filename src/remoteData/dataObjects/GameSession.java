package remoteData.dataObjects;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
    private int totalSpins;
    public String clientType;

    GameSession(Timestamp timeStamp, String sessionId, String game, String facebookId,
                String name, String promocode, String fbSource, Timestamp actionTime, int totalWager, int totalWin, int endBalance, int spins, int totalSpins,
                String clientType){
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
        this.clientType = clientType;
    }

    public String toString(){

        return "(" + timeStamp.toString() +", "+ sessionId + ", " +game + ", " +facebookId + ", " +name + ", " +promocode +", " +fbSource +", " +actionTime.toString() +", " +
                    totalWager +", " +totalWin +", " +endBalance +", " +spins +", " +spins +", "+ totalSpins +", "+ clientType +")";

    }

    public String toSQLValues() {


        // NOTE: Not storing mobile here:

        return "'" + timeStamp.toString() +"', '"+ sessionId + "', '" +game + "', '" +facebookId + "', '" +name.replaceAll("'", "") + "', '" +truncate(promocode, 20) +"', '" +fbSource +"', '" +actionTime.toString() +"', " +
                    totalWager +", " +totalWin +", " +endBalance +", " +spins +", " +totalSpins + ", '" + clientType + "'";

    }

    private String truncate(String promoCode, int length) {

        if(promocode.length() <= length)
            return promoCode;

        return promoCode.substring(0, length-1);

    }


    public void store(Connection connection) {

        String insert = "insert into game_session values (" + toSQLValues() + ")";

        //System.out.println("Insert with: " + insert);

        try{

            Statement statement = connection.createStatement();
            statement.execute(insert);

        }catch(SQLException e){

            System.out.println("Error accessing data in database. SQL:" + insert);
            e.printStackTrace();
        }

    }


}
