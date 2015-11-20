package action;

import campaigns.CampaignState;
import net.sf.json.JSONObject;
import output.DeliveryException;
import output.GiveAwayHandler;
import remoteData.dataObjects.User;

import java.sql.Connection;
import java.sql.Timestamp;

/*******************************************************************
 *
 *          Give a user coins
 *
 *          NOTE:   This provides no information for the user.
 *                  He/she may actually miss that the coins are given
 */

public class GiveCoinAction extends Action implements ActionInterface{

    private static final String message = "Coins for player";
    private int amount;


    public GiveCoinAction(int amount, User user, Timestamp timeStamp, int significance, String campaignName, int messageId, CampaignState state, double responseFactor){

        this(0, amount, new ActionParameter(user.name, user.facebookId, user.email), timeStamp, significance, campaignName, messageId, state, responseFactor);
    }


    public GiveCoinAction(int id, int amount, ActionParameter parameter, Timestamp timeStamp, int significance, String campaignName, int messageId, CampaignState state, double responseFactor){

        super(id, ActionType.COIN_ACTION, parameter, timeStamp, message, significance, campaignName, messageId, state, responseFactor );
        this.amount = amount;
    }

    /**************************************************************
     *
     *              Execute the action
     *
     *
     *
     *
     *
     * @param dryRun                - do not send (just testing)
     * @param testUser              - override user with dummy
     * @param executionTime         - time to store for the execution
     * @param localConnection       - connection to the crmDatabase to store xposure and outcomes
     * @param count
     *@param size @return                      - the response from executing action
     */

    public ActionResponse execute(boolean dryRun, String testUser, Timestamp executionTime, Connection localConnection, int count, int size) {

        if(!isLive()){

            System.out.println("--------------------------------------------------------");
            System.out.println("%% Skipping (reason: "+ state.name()+") " + type.name() + " for player " + actionParameter.facebookId + "("+message+ ": " + amount + ")" );
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Message sent - (reason: "+ state.name()+") " );

        }

        System.out.println("--------------------------------------------------------");
        System.out.println("! Perform " + type.name() + " for player " + actionParameter.facebookId);

        GiveAwayHandler handler = new GiveAwayHandler(testUser)
                .toRecipient(actionParameter.facebookId)
                .withAmount(amount);

        try {

            if(!dryRun){

                if(handler.send()){
                    return new ActionResponse(ActionResponseStatus.OK,   "Coins given sent");
                }
                else
                    return new ActionResponse(ActionResponseStatus.FAILED,   "Coin delivery failed");

            }
            else{
                System.out.println("  %%%Dryrun: Ignoring giving "+ amount + "coins to user "+ actionParameter.name );
                return new ActionResponse(ActionResponseStatus.IGNORED,   "No Coins given - dry run");
            }

        } catch (DeliveryException e) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new ActionResponse(ActionResponseStatus.FAILED,   "Coin delivery failed");
        }


    }

    /*************************************************************'
     *
     *      Appending specific data and storing
     *
     * @param connection    - database
     */

    public void store(Connection connection){

        JSONObject data = actionAsJSON()
                .put("amount", amount);

        super.store(connection, data);

    }



}
