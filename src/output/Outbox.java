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

    private List<ActionInterface> queue = new ArrayList<ActionInterface>(7000);
    private final int cap;
    private boolean dryRun;
    private String testUser;
    private Connection connection;

    public Outbox(int cap, boolean dryRun, String testUser, Connection localConnection){

        this.cap = cap;
        this.dryRun = dryRun;
        this.testUser = testUser;
        this.connection = localConnection;
    }


    public void queue(ActionInterface action){

        queue.add(action);
    }

    public void purge(Timestamp executionTime){

        System.out.println(" -- Executing "+ queue.size() + " actions in queue with the timestamp " + executionTime.toString());

        int count   = 0;
        int success = 0;

        for (ActionInterface action : queue) {

            if(count > cap)
                break;

            ActionResponse response = action.execute(dryRun, testUser, executionTime, connection, (count + 1), queue.size());

            if(response.isExecuted()){
                success++;
            }
            else
                noteFailedMessageDelivery(action.getParameters().facebookId, action.getType(), response.getStatus());
            count++;


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

    private void noteFailedMessageDelivery(String user, ActionType type, ActionResponseStatus status) {

        CachedUserTable table = new CachedUserTable();

        if(status.isPermanentError()){

            switch (type) {

                case NOTIFICATION:
                    table.updateFailNotification(user, connection);
                    System.out.println(" --- Updating failed Notification for player " + user);
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
}
