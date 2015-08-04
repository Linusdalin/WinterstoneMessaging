package remoteData.dataObjects;

import java.sql.*;

/**
 *
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-04-16
 * Time: 13:17
 * To change this template use File | Settings | File Templates.
 */
public class UserTable extends GenericTable {

    private static final String getRemote =
            "select * " +
            "     from users ";


    public UserTable(String startTime, String restriction, int limit){

        super(getRemote, startTime, restriction, limit);
        maxLimit = limit;
    }

    public UserTable(){

        this("2015-01-01", "", -1);
    }



    public User getNext(){

        try {
            if(!resultSet.next())
                return null;

            //    public User(String facebookId, String name, String email, String promoCode, String lastgamePlayed,Timestamp created, int totalWager, int balance, int nextNumberOfPicks){

            return new User(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(28), resultSet.getString(35), resultSet.getTimestamp(29), resultSet.getInt(26),resultSet.getInt(27),

                    resultSet.getInt(13),resultSet.getInt(8),resultSet.getInt(36));


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void storeLocally(Connection connection, Payment p){

        p.wash();

        String insert = "insert into Payment values (" + p.toSQLValues() + ")";

        //System.out.println("Insert with: " + insert);

        try{

            Statement statement = connection.createStatement();
            statement.execute(insert);

        }catch(SQLException e){

            System.out.println("Error accessing data in database");
            e.printStackTrace();
        }

    }


    public Timestamp getLast(Connection connection){

        String sql = "select timeStamp from payment order by timestamp desc limit 1;";

        try{

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            if(!rs.next())
                return null;  // There are no entries in the database

            return rs.getTimestamp(1);

        }catch(SQLException e){

            System.out.println("Error accessing data in database");
            e.printStackTrace();
        }


        return null;
    }
}
