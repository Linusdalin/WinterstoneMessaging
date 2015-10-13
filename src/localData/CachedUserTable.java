package localData;

import remoteData.dataObjects.GenericTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/*******************************************************************************
 *
 *
 *                  Accessing a user in the database
 *
 */

public class CachedUserTable extends GenericTable {

    private static final String getRemote =
            "select * from user where 1=1 -RESTRICTION- order by lastSession -ORDER- -LIMIT-";


    public CachedUserTable(String restriction, int limit){

        super(getRemote, restriction, limit);
        maxLimit = limit;
    }

    public CachedUserTable(){

        this( "", -1);
    }



    public CachedUser getNext(){

        try {
            if(!resultSet.next())
                return null;

            //    public User(String facebookId, String name, String email, String promoCode, String lastgamePlayed,Timestamp created, int totalWager, int balance, int nextNumberOfPicks){

            return new CachedUser(resultSet.getString(1), resultSet.getTimestamp(2), resultSet.getInt(3) , resultSet.getInt(4));


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public Timestamp getLastSession(Connection connection) {

        String query = "select max(lastSession) from user";


        try{

            //System.out.println("Query: " + queryString);
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            if(resultSet == null)
                return null;

            if(!resultSet.next())
                return null;

            return resultSet.getTimestamp( 1 );

        }catch(SQLException e){

            System.out.println("Error accessing data in database with the query:\n" + query);
            e.printStackTrace();
        }


        return null;

    }

    public void store(CachedUser user, Connection connection) {

        String insertQuery = "INSERT INTO user VALUES ('" + user.facebookId + "', '" + user.lastSession.toString() +"', "+ user.failMail+", "+ user.failNotification+")";

        try{

            Statement statement = connection.createStatement();
            //System.out.println(insertQuery);

            // execute insert SQL
            statement.execute(insertQuery);

        } catch (SQLException e) {


            System.out.println(e.getMessage());

        }

    }

    public void updateTime(CachedUser user, Connection connection) {

        String updateQuery = "UPDATE user SET lastSession='" + user.lastSession + "' WHERE facebookId='"+user.facebookId+"'";

        try{

            Statement statement = connection.createStatement();
            //System.out.println(updateQuery);

            // execute update SQL stetement
            statement.execute(updateQuery);

        } catch (SQLException e) {


            System.out.println(e.getMessage());

        }

    }

    public void updateFailMail(String facebookId, Connection connection) {

        String updateQuery = "UPDATE user SET failMail=failMail+1  WHERE facebookId='"+facebookId+"'";

        try{

            Statement statement = connection.createStatement();
            //System.out.println(updateQuery);

            // execute update SQL stetement
            statement.execute(updateQuery);

        } catch (SQLException e) {


            System.out.println(e.getMessage());

        }

    }


    public void updateFailNotification(String facebookId, Connection connection) {

        String updateQuery = "UPDATE user SET failNotificaton=failNotificaton+1  WHERE facebookId='"+facebookId+"'";

        try{

            Statement statement = connection.createStatement();
            //System.out.println(updateQuery);

            // execute update SQL stetement
            statement.execute(updateQuery);

        } catch (SQLException e) {


            System.out.println(e.getMessage());

        }

    }



}
