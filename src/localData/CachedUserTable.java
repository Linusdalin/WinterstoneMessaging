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

            return new CachedUser(resultSet.getString(1), resultSet.getTimestamp(2), resultSet.getInt(3) , resultSet.getInt(4) , resultSet.getInt(5), resultSet.getInt(6), resultSet.getInt(7), resultSet.getTimestamp(8), resultSet.getTimestamp(9), resultSet.getInt(10));


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

        String firstMobile = null;
        String lastMobile = null;

        if(user.firstMobileSession != null)
            firstMobile = "'" + user.firstMobileSession.toString() + "'";
        if(user.lastMobileSession != null)
            firstMobile = "'" + user.lastMobileSession.toString() + "'";


        String insertQuery = "INSERT INTO user VALUES ('" + user.facebookId + "', '" + user.lastSession.toString() +"', "+ user.failMail+", "+ user.failNotification+ ", " +
                user.failPush +", "+ user.desktopSessions +", "+ user.iosSessions+", "+ firstMobile + ", " + lastMobile + ", " + user.level + ", " + user.level +")";

        try{

            Statement statement = connection.createStatement();
            //System.out.println(insertQuery);

            // execute insert SQL
            statement.execute(insertQuery);

        } catch (SQLException e) {

            System.out.println("Error accessing data in database. SQL:" + insertQuery);
            System.out.println(e.getMessage());

        }

    }

    public void updateTime(CachedUser user, boolean iOsSession, Timestamp sessionTime, Connection connection) {

        if(iOsSession){

            System.out.println(" !! Found iosSession for player " + user.facebookId + " updating user info");
        }


        String updateQuery = "UPDATE user SET lastSession='" + user.lastSession +
                "', "+(iOsSession ? "iosSessions = iosSessions + 1": "desktopSessions = desktopSessions + 1")+
                (iOsSession ? ", lastMobile='"+sessionTime.toString()+"'" : "")+
                 (iOsSession && user.iosSessions == 0 ? ", firstMobile = '"+ sessionTime.toString() +"'": "")+
                " WHERE facebookId='"+user.facebookId+"'";

        try{

            Statement statement = connection.createStatement();
            //System.out.println(updateQuery);

            // execute update SQL stetement
            statement.execute(updateQuery);

        } catch (SQLException e) {


            System.out.println(e.getMessage());

        }

    }

    public void updateLevel(String facebookId, int newLevel, Connection connection) {

        String updateQuery = "UPDATE user SET level = "+ newLevel+"  WHERE facebookId='"+facebookId+"'";

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

    public void updateFailPush(String facebookId, Connection connection) {

        String updateQuery = "UPDATE user SET failPush=failPush+1  WHERE facebookId='"+facebookId+"'";

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
