package action;

import localData.Exposure;
import output.NotificationHandler;
import remoteData.dataObjects.User;

import java.sql.Connection;
import java.sql.Timestamp;

/*******************************************************************
 *
 *          Notification
 *
 *          An instance of the abstract Action resulting in a notification sent to the user
 */

public class NotificationAction extends Action implements ActionInterface{

    String ref;   // Reference for facebook tracking
    private String reward;
    private String game;

    public NotificationAction(String message, User user, int significance, String ref, String promoCode, String campaignName){

        super(ActionType.NOTIFICATION, user, message, significance, campaignName );
        this.ref = ref;
        setPromoCode(promoCode);

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

        System.out.println("--------------------------------------------------------");
        System.out.println("! Executing " + type.name() + " for player " + user.name);

        NotificationHandler handler = new NotificationHandler(testUser)
                    .withCap(1)
                    .withRecipient(user.facebookId)
                    .withMessage(message)
                    .withRef(ref)
                    .withPromoCode(promoCode)
                    .withReward(reward)
                    .withGame(game);


        // Now check if we are to send off the message or just log it (dry run)

        int successCount = 0;

        if(!dryRun){
            successCount =  handler.send();
            if(successCount > 0){
                noteSuccessFulExposure( (testUser == null ? user.facebookId: testUser ) , executionTime, localConnection );
                return new ActionResponse(ActionResponseStatus.OK,   "Message sent");
            }
            else
                return new ActionResponse(ActionResponseStatus.FAILED,   "Message delivery failed");

        }
        else{
            System.out.println("  %%%Dryrun: Ignoring sending message to user " + user.name + "("+ user.facebookId+") " + "-\""+ message+"\" Promocode:" + promoCode);
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Message sent - dry run");
        }


    }


    private void noteSuccessFulExposure(String actualUser, Timestamp executionTime, Connection localConnection) {

        Exposure exposure = new Exposure(actualUser, getCampaign(), getMessageId(), executionTime , promoCode);
        exposure.store(localConnection);
    }

    private int getMessageId() {
        return 0;  //TODO: Not implemented message id
    }

    public NotificationAction withReward(String reward) {
        this.reward = reward;
        return this;
    }

    public NotificationAction withGame(String game) {
        this.game = game;
        return this;
    }



}
