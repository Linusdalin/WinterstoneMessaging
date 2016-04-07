package raf;

import remoteData.dataObjects.GenericTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
public class FriendTable extends GenericTable {

    private static final String getRemote =
            "select *" +
            "     from friend where 1=1" +
            "      -RESTRICTION-  -LIMIT-";

    private Connection connection;
    //ConnectionPool connectionPool = new ConnectionPool();
    private PreparedStatement stmt;

    public FriendTable(String restriction, int limit, Connection connection){

        super(getRemote, restriction, limit);
        maxLimit = limit;
        this.connection = connection;

        /*
        try {

            String query = "select sum(count) from Friend where user = ? and campaign = ?";
            stmt = connection.prepareStatement(query);

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
          */
    }

    public FriendTable(Connection connection){


        this( "", -1, connection);
        String query = "select sum(count) from friend where user = ? and campaign = ?";

        /*
        try {

            stmt = connection.prepareStatement(query);

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        */
    }

    public Friend getNext(){

        try {
            if(!resultSet.next())
                return null;

            return new Friend(resultSet.getString(1), resultSet.getString(2), resultSet.getTimestamp(3), resultSet.getInt(4), resultSet.getTimestamp(5), resultSet.getInt(6));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Friend> getAll(){

        List<Friend> Friends = new ArrayList<>();
        Friend Friend = getNext();

        while(Friend != null){

            Friends.add(Friend);
            Friend = getNext();
        }

        return Friends;

    }



    public int getFriends(String userId, String campaign, int messageId){

        String query = "select sum(count) from Friend where user = '"+ userId+"'"+(campaign != null ? " and campaign = '"+ campaign+"'" : "");

        if(messageId != -1){
            query += " and messageId = " + messageId;
        }

        int Friends = 0;

        try{

            statement  = connection.createStatement();
            resultSet = statement.executeQuery(query);

            if(resultSet != null){

                if(resultSet.next())
                    Friends =  resultSet.getInt( 1 );
            }

        }catch(SQLException e){

            System.out.println("Error accessing data in database with the prepared query");
            e.printStackTrace();
        }
        finally{

            close();
        }

        return Friends;

    }


}
