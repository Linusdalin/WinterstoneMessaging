package localData;

import remoteData.dataObjects.GenericTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-04-16
 * Time: 13:17
 * To change this template use File | Settings | File Templates.
 */
public class ResponseTable extends GenericTable {

    private static final String getRemote =
            "select *" +
            "     from response where 1=1" +
            "      -RESTRICTION-  -LIMIT-";

    private Connection connection;


    public ResponseTable(String restriction, int limit, Connection connection){

        super(getRemote, restriction, limit);
        maxLimit = limit;
        this.connection = connection;
    }

    public ResponseTable(Connection connection){

        this( "", -1, connection);
    }

    public Response getNext(){

        try {
            if(!resultSet.next())
                return null;

            return new Response(resultSet.getString(1), resultSet.getString(2), resultSet.getInt(3), resultSet.getInt(4), resultSet.getTimestamp(5));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Response> getAll(){

        List<Response> responses = new ArrayList<Response>();
        Response response = getNext();

        while(response != null){

            responses.add(response);
            response = getNext();
        }

        return responses;

    }

    /***************************************************************************
     *
     *              Get the number of responses for a user for a specific campaign
     *
     * @param userId              - the user
     * @param campaign          - the campaign
     * @return                  - The count of responses
     */


    public int getResponses(String userId, String campaign){

        return getResponses(userId, campaign, -1);
    }

    public int getResponses(String userId){

        return getResponses(userId, null, -1);
    }

    public int getResponses(String userId, String campaign, int messageId){

        String query = "select sum(count) from response where user = '"+ userId+"'"+(campaign != null ? " and campaign = '"+ campaign+"'" : "");

        if(messageId != -1){
            query += " and messageId = " + messageId;
        }

        try{

            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            if(resultSet == null)
                return 0;

            if(!resultSet.next())
                return 0;

            return resultSet.getInt( 1 );

        }catch(SQLException e){

            System.out.println("Error accessing data in database with the query:\n" + query);
            e.printStackTrace();
        }


        return 0;

    }



}
