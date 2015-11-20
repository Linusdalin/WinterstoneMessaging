package core;

import action.Action;
import dbManager.ConnectionHandler;
import localData.ActionTable;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Calendar;

/**************************************************************************
 *
 *          Main class for purging data
 *
 *
 */


public class SendEngine {

    private Connection connection = null;
    private final boolean dryRun;
    private final boolean sendEmail;
    private final int send_cap;
    private final String testUser;


    public SendEngine(ConnectionHandler.Location dataSource, boolean dryRun, boolean sendEmail, int send_cap, String testUser) {
        this.dryRun = dryRun;
        this.sendEmail = sendEmail;
        this.send_cap = send_cap;
        this.testUser = testUser;

        try{

            connection    = ConnectionHandler.getConnection(dataSource);

        }catch(Exception e){

            e.printStackTrace();

        }

    }

    public int executeSend(Timestamp executionTime, int backTrackDays) {


        ActionTable allActions = new ActionTable(connection);
        allActions.load(connection, " and timestamp >= date_sub(current_date(), interval "+ backTrackDays+" day)");
        Action action = allActions.getNext();
        int actionCount = 0;

        if(action == null)
            return 0;

        while(action != null && actionCount < send_cap){

            System.out.println(" ----------------------------------------------------------\n  " + actionCount + "- Executing " + action.toString());
            action.execute(dryRun, testUser, executionTime, connection, actionCount, -1);     //TODO: Get max here for display in the execution

            action = allActions.getNext();
        }

        return actionCount;

    }

}
