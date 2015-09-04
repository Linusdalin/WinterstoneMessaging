package localData;


import remoteData.dataObjects.User;

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
public class Exposure {


    public final String facebookId;
    public final String campaignName;
    public final int messageId;
    public final Timestamp exposureTime;
    public final String promoCode;
    public final String type;

    public Exposure(String facebookId, String campaignName, int messageId, Timestamp exposureTime, String promoCode, String type){


        this.facebookId = facebookId;
        this.campaignName = campaignName;
        this.messageId = messageId;
        this.exposureTime = exposureTime;
        this.promoCode = promoCode;
        this.type = type;

    }

    public String toString(){

        return "(" + facebookId + ", " +campaignName + ", " +messageId+ ", " +exposureTime+ ", " +promoCode+ ", " + type +  ")";

    }

    private String toSQLValues() {

        return "'" + facebookId + "', '" +campaignName + "', " +messageId+ ", '" +exposureTime+ "', '" +promoCode + "', '" +type +"'";


    }

    public void store(Connection connection) {

        String insert = "insert into exposure values (" + toSQLValues() + ")";

        System.out.println("Insert with: " + insert);

        try{

            Statement statement = connection.createStatement();
            statement.execute(insert);

        }catch(SQLException e){

            System.out.println("Error accessing data in database. SQL:" + insert);
            e.printStackTrace();
        }

    }


}
