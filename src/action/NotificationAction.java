package action;

import campaigns.CampaignState;
import localData.Exposure;
import output.DeliveryException;
import output.NotificationHandler;
import remoteData.dataObjects.User;
import rewards.Reward;

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

    /*****************************************************************************************
     *
     *          Create an action
     *
     *
     * @param message                 - the messahe to send
     * @param user                    - the recipient
     * @param significance            - action significance
     * @param ref                     - facebook reference
     * @param campaignName            - The name fro tracking
     * @param messageId               - message id for tracking
     * @param state                   - campaign state
     */


    public NotificationAction(String message, User user, int significance, String ref, String campaignName, int messageId, CampaignState state){

        super(ActionType.NOTIFICATION, user, message, significance, campaignName, messageId, state );
        this.ref = ref;
        setPromoCode(createPromoCode(campaignName, messageId));

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

        if(isTestMode()){

            System.out.println("--------------------------------------------------------");
            System.out.println("%% Skipping (reason: "+ state.name()+") " + type.name() + " for player " + actionParameter.name);
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Message sent - (reason: "+ state.name()+") " );

        }

        if(!isLive())
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Message sent - (reason: "+ state.name()+") " );


        System.out.println("--------------------------------------------------------");
        System.out.println("! Executing " + type.name() + " for player " + actionParameter.name);

        NotificationHandler handler = new NotificationHandler(testUser)
                    .withRecipient(actionParameter.facebookId)
                    .withMessage(message)
                    .withRef(ref)
                    .withPromoCode(promoCode)
                    .withReward(reward)
                    .withGame(game);


        // Now check if we are to send off the message or just log it (dry run)

        int successCount;

        if(!dryRun){

            try{

                if(handler.send()){
                    noteSuccessFulExposure( (testUser == null ? actionParameter.facebookId : testUser ) , executionTime, localConnection );
                    return new ActionResponse(ActionResponseStatus.OK,   "Message sent");
                }

            }catch(DeliveryException e){

                return new ActionResponse(e.getStatus(),   "Message delivery failed");

            }

            return new ActionResponse(ActionResponseStatus.FAILED,   "Message delivery failed");

        }
        else{
            System.out.println("  %%%Dryrun: Ignoring sending message to user "+ actionParameter.name +" " + "-\""+ message+"\" Promocode:" + promoCode);
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Message sent - dry run");
        }


    }


    private void noteSuccessFulExposure(String actualUser, Timestamp executionTime, Connection localConnection) {

        Exposure exposure = new Exposure(actualUser, getCampaign(), getMessageId(), executionTime , promoCode, ActionType.NOTIFICATION.name());
        exposure.store(localConnection);
    }


    public NotificationAction withReward(String reward) {
        this.reward = reward;
        return this;
    }

    public NotificationAction withReward(Reward reward) {

        this.reward = reward.getCode();
        return this;
    }


    public NotificationAction withGame(String game) {
        this.game = game;
        return this;
    }

    protected String createPromoCode(String name, int messageType) {

        String tag = name.replaceAll(" ", "");
        return tag + "-" + messageType;

    }


}
