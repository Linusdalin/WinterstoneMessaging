package remoteData.dataObjects;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*******************************************************************************
 *
 *
 *                  Accessing a user in the database
 *
 */

public class UserTable extends GenericTable {

    private static final String getRemote =
            "select * from players where 1=1 -RESTRICTION- order by created -LIMIT-";


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


            return new User(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(29), resultSet.getString(36), resultSet.getTimestamp(30),
                    resultSet.getInt(28), resultSet.getInt(27), resultSet.getInt(31),
                    resultSet.getInt(14),resultSet.getInt(9),resultSet.getInt(37), resultSet.getInt(35), resultSet.getInt(41),
                    resultSet.getString(32), resultSet.getString(26), resultSet.getTimestamp(11));


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<User> getAll(){

    List<User> users = new ArrayList<>();
    User response = getNext();

    while(response != null){

        users.add(response);
        response = getNext();
    }

    return users;

}


}
