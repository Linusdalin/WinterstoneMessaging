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
public class Payment {


    public final String facebookId;
    public final int amount;
    public final String game;
    public final Timestamp timeStamp;

    public Payment(String facebookId, int amount, String game, Timestamp timeStamp){

        this.facebookId = facebookId;
        this.amount = amount;
        this.game = game;
        this.timeStamp = timeStamp;

    }

    public String toString(){

        return "(" + timeStamp.toString() +", "+ facebookId + ", " +amount + ",  " +game +  ")";

    }

    public String toSQLValues() {

        return "'" + facebookId + "', " +amount + ", '" + game + "', '" +timeStamp +"','', '0000-00-00'";

    }


    public void store(Connection connection) {

        String insert = "insert into payment values (" + toSQLValues() + ")";

        //System.out.println("Insert with: " + insert);

        try{

            Statement statement = connection.createStatement();
            statement.execute(insert);

        }catch(SQLException e){

            System.out.println("Error accessing data in database. SQL:" + insert);
            e.printStackTrace();
        }

    }



    public void wash() {

    }
}
