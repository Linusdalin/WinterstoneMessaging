package core;

import action.ActionInterface;
import action.ActionResponse;
import dbManager.ConnectionHandler;
import localData.ActionTable;

import java.sql.Connection;
import java.sql.Timestamp;

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

    /***********************************************************************************
     *
     *             Executing actions and sending out notifications and other data.
     *
     *
     *
     * @param executionTime        - time for execution
     * @param backTrackDays        - days back to execute (default is 0 i.e. just today)
     *
     * @param timeSlot             - only send actions in a specific timeslot (0,1,2) or all -1
     * @return                     - number of messages
     */

    public int executeSend(Timestamp executionTime, int backTrackDays, int timeSlot) {


        ActionTable allActions = new ActionTable(connection);
        allActions.loadAndRetry(connection, " and status = 'PENDING' and timestamp >= date(date_sub(current_date(), interval "+ backTrackDays+" day))", "ASC", -1);
        ActionInterface action = allActions.getNext();
        int actionCount = 0;
        int skipCount = 0;

        if(action == null)
            return 0;

        while(action != null && actionCount < send_cap){

            System.out.println(" ----------------------------------------------------------\n  " + actionCount + "- Executing " + action.toString());

            if(timeSlot >=0 && action.getSchedulingTime() >= 0 && action.getSchedulingTime() != timeSlot){

                System.out.println("Action to be scheduled in slot " + action.getSchedulingTime() + " only scheduling actions in slot " + timeSlot);
                skipCount++;
                continue;
            }

            ActionResponse response = action.execute(dryRun, testUser, executionTime, connection, actionCount, -1);     //TODO: Get max here for display in the execution

            if(response.isExecuted()){

                action.updateAsExecuted(connection);
            }

            action = allActions.getNext();
            actionCount++;
        }

        System.out.println("Skipped " + skipCount + " scheduled actions.");

        return actionCount;

    }

    public int countActions(int backTrackDays) {

        ActionTable actions = new ActionTable(connection);
        return actions.countPendingActions(backTrackDays);

    }
}
