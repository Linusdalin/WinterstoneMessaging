package action;

import campaigns.CampaignState;
import output.GiveAwayHandler;
import output.NotificationHandler;
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

    public GiveCoinAction(int amount, User user, int significance, String campaignName, int messageId, CampaignState state){

        super(ActionType.COIN_ACTION, user, message, significance, campaignName, messageId, state );
        this.amount = amount;
    }

    /**************************************************************
     *
     *              Execute the action
     *
     *
     *
     *
     * @param dryRun                - do not send (just testing)
     * @param testUser              - override user with dummy
     * @param executionTime         - time to store for the execution
     * @param localConnection       - connection to the crmDatabase to store xposure and outcomes
     * @return                      - the response from executing action
     */

    public ActionResponse execute(boolean dryRun, String testUser, Timestamp executionTime, Connection localConnection) {

        if(!isLive()){

            System.out.println("--------------------------------------------------------");
            System.out.println("%% Skipping (reason: "+ state.name()+") " + type.name() + " for player " + user.facebookId + "("+message+ ": " + amount + ")" );
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Message sent - (reason: "+ state.name()+") " );

        }

        System.out.println("--------------------------------------------------------");
        System.out.println("! Perform " + type.name() + " for player " + user.facebookId);

        GiveAwayHandler handler = new GiveAwayHandler(testUser)
                .withRecipient(user)
                .withAmount(amount);

        handler.send();



        if(!dryRun){
            if(handler.send()){
                return new ActionResponse(ActionResponseStatus.OK,   "Coins given sent");
            }
            else
                return new ActionResponse(ActionResponseStatus.FAILED,   "Coin delivery failed");

        }
        else{
            System.out.println("  %%%Dryrun: Ignoring giving "+ amount + "coins to user "+ user );
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Coins given - dry run");
        }



    }



}
