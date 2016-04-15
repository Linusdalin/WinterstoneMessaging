package action;

import campaigns.CampaignState;
import net.sf.json.JSONObject;
import output.DeliveryException;
import output.PushHandler;
import remoteData.dataObjects.User;
import rewards.Reward;

import java.sql.Connection;
import java.sql.Timestamp;

/*******************************************************************
 *
 *          Push Notification to a mobile device
 *
 *          An instance of the abstract Action resulting in a notification sent to the user
 */

public class MobilePushAction extends Action implements ActionInterface{

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

    public MobilePushAction(String message, User user, Timestamp timestamp, int significance, String ref, String campaignName, int messageId, CampaignState state, double responseFactor){

        this(0, message, new ActionParameter(user.name, user.id, user.email), timestamp, significance, ref, campaignName, messageId, state, responseFactor);

    }


    public MobilePushAction(int id, String message, ActionParameter parameter, Timestamp timestamp, int significance, String ref, String campaignName, int messageId, CampaignState state, double responseFactor){

        super(id, ActionType.PUSH, parameter, timestamp, message, significance, campaignName, messageId, state, responseFactor );
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
     *
     * @param dryRun                - do not send (just testing)
     * @param testUser              - override user with dummy
     * @param executionTime         - time to store for the execution
     * @param localConnection       - connection to the crmDatabase to store xposure and outcomes
     * @param count                 - current count
     * @param size                  - total actions to execute
     * @return                      - the response from executing action
     */

    public ActionResponse execute(boolean dryRun, String testUser, Timestamp executionTime, Connection localConnection, int count, int size) {

        if(isTestMode()){

            System.out.println("--------------------------------------------------------");
            System.out.println("%% Skipping (reason: "+ state.name()+") " + type.name() + " for player " + actionParameter.name);
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Push Notification sent - (reason: "+ state.name()+") " );

        }

        if(!isLive())
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Push Notification sent - (reason: "+ state.name()+") " );


        System.out.println("--------------------------------------------------------");
        System.out.println("! Executing " + type.name() + "("+count+"/"+size+") for player " + actionParameter.name);

        PushHandler handler = new PushHandler(testUser)
                    .toRecipient(actionParameter.facebookId)
                    .withMessage(message)
                    .withReward(promoCode)
                    .withReward(reward)
                    .withGame(game);


        if(!dryRun){

            try{

                if(handler.send()){
                    noteSuccessFulExposure( (testUser == null ? actionParameter.facebookId : testUser ) , executionTime, localConnection );
                    return new ActionResponse(ActionResponseStatus.OK,   "Message sent");
                }

            }catch(DeliveryException e){

                return new ActionResponse(e.getStatus(),   "Message delivery failed");

            }

            noteFailedExposure( (testUser == null ? actionParameter.facebookId : testUser ) , executionTime, localConnection );
            return new ActionResponse(ActionResponseStatus.FAILED,   "Message delivery failed");

        }
        else{
            System.out.println("  %%%Dryrun: Ignoring sending message to user "+ actionParameter.name +" " + "-\""+ message+"\" Promocode:" + promoCode);
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Message sent - dry run");
        }


    }




    public ActionInterface withReward(String reward) {
        this.reward = reward;
        return this;
    }

    public MobilePushAction withReward(Reward reward) {

        this.reward = reward.getCode();
        return this;
    }


    public MobilePushAction withGame(String game) {
        this.game = game;
        return this;
    }

    protected String createPromoCode(String name, int messageType) {

        String tag = name.replaceAll(" ", "");
        return tag + "-" + messageType;

    }

    /*************************************************************'
     *
     *      Appending specific data and storing
     *
     * @param connection
     */

    public void store(Connection connection){

        JSONObject data = actionAsJSON()
                .put("reward", reward)
                .put("game", game);

        super.store(connection, data);

    }

    @Override
    public String getReward() {
        return reward;
    }



}
