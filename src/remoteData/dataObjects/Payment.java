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
public class Payment {


    public final String facebookId;
    public final int amount;
    public final String game;
    public final Timestamp timeStamp;
    public final String promoCode;
    public final Timestamp firstLogin;

    Payment(String facebookId, int amount, String game, Timestamp timeStamp, String promoCode, Timestamp firstLogin){

        if(firstLogin.toString().equals("1970-01-01 00:00:00.0")){

            this.firstLogin = Timestamp.valueOf("2015-01-01 00:00:00");
        }
        else
            this.firstLogin = firstLogin;


        this.facebookId = facebookId;
        this.amount = amount;
        this.game = game;
        this.timeStamp = timeStamp;
        this.promoCode = promoCode;

    }

    public String toString(){

        return "(" + timeStamp.toString() +", "+ facebookId + ", " +amount + ", " +promoCode + ", " +game +  ")";

    }

    public String toSQLValues() {

        return "'" + facebookId + "', " +amount + ", '" + game + "', '" +timeStamp +"', '" +promoCode + "', '" +firstLogin + "'";

    }

    public void wash() {

    }
}
