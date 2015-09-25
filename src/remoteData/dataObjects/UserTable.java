package remoteData.dataObjects;

import java.sql.*;

/*******************************************************************************
 *
 *
 *                  Accessing a user in the database
 *
 */

public class UserTable extends GenericTable {

    private static final String getRemote =
            "select * from users where 1=1 $(RESTRICTION) order by created $(LIMIT)";


    public UserTable(String restriction, int limit){

        super(getRemote, restriction, limit);
        maxLimit = limit;
    }

    public UserTable(){

        this( "", -1);
    }



    public User getNext(){

        try {
            if(!resultSet.next())
                return null;

            //    public User(String facebookId, String name, String email, String promoCode, String lastgamePlayed,Timestamp created, int totalWager, int balance, int nextNumberOfPicks){

            return new User(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(28), resultSet.getString(35), resultSet.getTimestamp(29),
                    resultSet.getInt(27), resultSet.getInt(26), resultSet.getInt(30),
                    resultSet.getInt(13),resultSet.getInt(8),resultSet.getInt(36), resultSet.getInt(34), resultSet.getInt(40),
                    resultSet.getString(31), resultSet.getString(25));


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public User getUserById(String userId) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}
