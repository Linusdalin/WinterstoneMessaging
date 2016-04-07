package output;

import action.ActionInterface;
import action.ActionResponse;
import action.ActionResponseStatus;
import action.ActionType;
import localData.CachedUserTable;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/********************************************************************''''
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-08-06
 * Time: 12:46
 * To change this template use File | Settings | File Templates.
 */

public class Outbox {

    private List<ActionInterface> queue = new ArrayList<ActionInterface>(8000);
    private final int cap;
    private boolean dryRun;
    private String testUser;

    public Outbox(int cap, boolean dryRun, String testUser){

        this.cap = cap;
        this.dryRun = dryRun;
        this.testUser = testUser;
    }


    public void queue(ActionInterface action){

        queue.add(action);
    }

    public void purge(Timestamp executionTime, Connection connection){

        System.out.println(" -- Executing "+ queue.size() + " actions in queue with the timestamp " + executionTime.toString());

        int count   = 0;
        int success = 0;

        for (ActionInterface action : queue) {


            ActionResponse response = action.execute(dryRun, testUser, executionTime, connection, (count + 1), queue.size());

            if(response.isExecuted()){
                success++;
                action.updateAsExecuted(connection);

            }
            else
                noteFailedMessageDelivery(action.getParameters().facebookId, action.getType(), response.getStatus(), connection);
            count++;

            if(count >= cap)
                break;


        }

        System.out.println(" -- In total: " + count + " actions executed. " + success + " successfully delivered.");


    }


    /**************************************************************'
     *
     *          If the delivery of a message did not work we should note this and avoid sending more messages in the future.
     *
     *
     * @param user       - the user which we tried to contact
     * @param type       - type of message that failed. It will define how to note the action.
     * @param status     - status of the message. (Temporary or permanent errors)
     */

    private void noteFailedMessageDelivery(String user, ActionType type, ActionResponseStatus status, Connection connection) {

        CachedUserTable table = new CachedUserTable();

        if(status.isPermanentError()){

            switch (type) {

                case NOTIFICATION:
                    table.updateFailNotification(user, connection);
                    System.out.println(" --- Updating failed Notification for player " + user);
                    break;
                case PUSH:
                    table.updateFailPush(user, connection);
                    System.out.println(" --- Updating failed Push for player " + user);
                    break;
                case MANUAL_ACTION:
                    break;
                case EMAIL:
                    table.updateFailMail(user, connection);
                    System.out.println(" --- Updating failed EMAIL for player " + user);
                    break;
                case IN_GAME:
                    break;
                case COIN_ACTION:
                    break;
            }

        }
        else{

            System.out.println("  -- Temporary error sending message. Ignoring for now");
        }


    }

    public int size() {

        return queue.size();
    }

    /*************************************************************************
     *
     *              Remove all actions from the queue that matches:
     *
     *                  - campaign
     *                  - messageId
     *
     *
     * @param campaign             - the name
     * @param messageId            - int id
     * @return                     - number of affected actions
     */

    public int removeAll(String campaign, int messageId) {

        int deleted = 0;

        for (ActionInterface queuedAction : queue) {

            if(queuedAction.getCampaign().equalsIgnoreCase(campaign) && queuedAction.getMessageId() == messageId){

                queue.remove(queuedAction);
                deleted++;
            }
        }

        return deleted;

    }
}
