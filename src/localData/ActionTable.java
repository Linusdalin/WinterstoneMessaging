package localData;

import action.*;
import campaigns.CampaignState;
import email.EmailInterface;
import email.NotificationEmail;
import net.sf.json.JSONObject;
import remoteData.dataObjects.GenericTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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

    public ActionInterface getNext(){

        try {
            if(!resultSet.next())
                return null;

            return createAction(resultSet.getInt(1), resultSet.getTimestamp(2), resultSet.getString(3), resultSet.getString(4), new JSONObject(resultSet.getString(5)));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*********************************************************************************
     *
     *
     *              Create an action given the type and json data
     *
     *
     *
     * @param id
     * @param timeStamp
     * @param type                 - the type of action
     * @param status
     * @param actionData           - the data of the action
     * @return
     */


    private ActionInterface createAction(int id, Timestamp timeStamp, String type, String status, JSONObject actionData) {

        ActionInterface action = null;

        if(!status.equals("PENDING")){

            System.out.println("Ignoring action with status " + status + " (@"+ timeStamp.toString()+")" );
            return null;
        }

        // Parameterscommon to all actions

        ActionParameter parameter = new ActionParameter(actionData.getJSONObject("actionParameter"));
        int significance = actionData.getInt("significance");
        String campaign = actionData.getString("campaign");
        int messageId = actionData.getInt("messageId");
        CampaignState state = getStateFromString(actionData.getString("state"));
        double responseFactor = actionData.getDouble("responseFactor");



        if(type.equals(ActionType.NOTIFICATION.name())){

            String message = actionData.getString("message");
            String ref = "notification";

            action = new NotificationAction(id, message, parameter, timeStamp, significance, ref, campaign, messageId, state, responseFactor);
        }

        if(type.equals(ActionType.EMAIL.name())){

            EmailInterface email = new NotificationEmail(actionData.getJSONObject("email"));

            action = new EmailAction(id, email, parameter, timeStamp, significance, campaign, messageId, state, responseFactor);
        }

        if(type.equals(ActionType.COIN_ACTION.name())){

            int amount = actionData.getInt("amount");

            action = new GiveCoinAction(id, amount, parameter, timeStamp, significance, campaign, messageId, state, responseFactor);
        }

        if(type.equals(ActionType.MANUAL_ACTION.name())){

            String message = actionData.getString("message");

            action = new ManualAction(id, message, parameter, timeStamp, significance, campaign, messageId, state, responseFactor);
        }

        if(type.equals(ActionType.PUSH.name())){

            String message = actionData.getString("message");
            String ref = "notification";

            action = new MobilePushAction(id, message, parameter, timeStamp, significance, ref, campaign, messageId, state, responseFactor);
        }


        return action;
    }

    private CampaignState getStateFromString(String status) {

            return CampaignState.valueOf(status);
    }


    public List<ActionInterface> getAll(){

        List<ActionInterface> actions = new ArrayList<>();
        ActionInterface response = getNext();

        while(response != null){

            actions.add(response);
            response = getNext();
        }

        return actions;

    }


    public int countPendingActions(int backTrackDays) {

        String queryString = "select count(*) from action where status = 'PENDING' and timestamp >= date(date_sub(current_date(), interval " + backTrackDays + " day))";
        try{

            //System.out.println("Query: " + queryString);
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(queryString);

            if(!resultSet.next())
                return 0;

            return resultSet.getInt( 1 );

        }catch(SQLException e){

            System.out.println("Error accessing data in database with the query:\n" + queryString);
            e.printStackTrace();
        }

        return 0;

    }
}
