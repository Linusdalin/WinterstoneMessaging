package output;

import action.ActionInterface;
import action.ActionResponse;

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

    private List<ActionInterface> queue = new ArrayList<ActionInterface>(4000);
    private final int cap;
    private boolean dryRun;
    private String testUser;
    private Connection localConnection;

    public Outbox(int cap, boolean dryRun, String testUser, Connection localConnection){

        this.cap = cap;
        this.dryRun = dryRun;
        this.testUser = testUser;
        this.localConnection = localConnection;
    }


    public void queue(ActionInterface action){

        queue.add(action);
    }

    public void purge(Timestamp executionTime){

        System.out.println(" -- Executing "+ queue.size() + " actions in queue with the timestamp " + executionTime.toString());

        int count = 0;
        int success = 0;

        for (ActionInterface action : queue) {

            if(count >= cap)
                break;

            ActionResponse response = action.execute(dryRun, testUser, executionTime, localConnection);
            if(response.isExecuted()){
                success++;
            }
            else
                noteFailedMessageDelivery(action.getUser().facebookId);

            count++;


        }

        System.out.println(" -- In total: " + count + " actions executed. " + success + " successfully delivered.");


    }


    /**************************************************************'
     *
     *          If the delivery of a message did not work we should note this and avoid sending more messages in the future.
     *
     *
     * @param user
     */

    private void noteFailedMessageDelivery(String user) {

        //TODO: Not implemented - remember that a message delivery did not work

    }

    public void listRecepients() {

        if(dryRun){

            System.out.println(" -- Listing "+ queue.size() + " recipients");

            for (ActionInterface action : queue) {

                System.out.println("\"" + action.getUser().facebookId + "\", ");


            }

            System.out.println("\n\n");

        }


    }
}
