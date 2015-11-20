package localData;

import action.Action;
import net.sf.json.JSONObject;
import remoteData.dataObjects.GenericTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/****************************************************************''
 *
 *          table to store an action
 *
 *
 */
public class ActionTable extends GenericTable {

    private static final String getRemote =
            "select *" +
            "     from action where 1=1" +
            "      -RESTRICTION-  -LIMIT-";

    private Connection connection;


    public ActionTable(String restriction, int limit, Connection connection){

        super(getRemote, restriction, limit);
        maxLimit = limit;
        this.connection = connection;
    }

    public ActionTable(Connection connection){

        this( "", -1, connection);
    }

    public Action getNext(){

        try {
            if(!resultSet.next())
                return null;

            return createAction(resultSet.getTimestamp(1), resultSet.getString(2), resultSet.getString(2), new JSONObject(resultSet.getString(3)));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Action createAction(Timestamp timestamp, String type, String status, JSONObject jsonObject) {
        return null;  //TODO: Not implemented yet
    }

    public List<Action> getAll(){

        List<Action> actions = new ArrayList<Action>();
        Action response = getNext();

        while(response != null){

            actions.add(response);
            response = getNext();
        }

        return actions;

    }


}
