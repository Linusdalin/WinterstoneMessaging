package localData;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**********************************************************************
 *
 *          Response contains responses for all different campaigns and message types
 */


public class Response {


    public final String facebookId;
    public final String campaignName;
    private final int messageId;
    public final int count;
    private final Timestamp lastUpdate;

    public Response(String facebookId, String campaignName, int messageId, int count, Timestamp lastUpdate){


        this.facebookId = facebookId;
        this.campaignName = campaignName;
        this.messageId = messageId;
        this.count = count;

        this.lastUpdate = lastUpdate;
    }

    public String toString(){

        return "(" + facebookId + ", " +campaignName + ", " + messageId+ ", " + count+ ", " + lastUpdate.toString()+  ")";

    }

    private String toSQLValues() {

        return "'" + facebookId + "', '" +campaignName + "', " +messageId + ", " +count + ", '" +lastUpdate + "'" ;


    }

    public void store(Connection connection) {

        String insert = "insert into response values (" + toSQLValues() + ")";

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
