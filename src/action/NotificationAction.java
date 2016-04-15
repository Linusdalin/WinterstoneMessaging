package action;

import campaigns.CampaignState;
import net.sf.json.JSONObject;
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
     *          Create an action during execution
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


    public NotificationAction(String message, User user, Timestamp timestamp, int significance, String ref, String campaignName, int messageId, CampaignState state, double responseFactor){

        this(0, message, new ActionParameter(user.name, user.id, user.email), timestamp, significance, ref, campaignName, messageId, state, responseFactor);

    }


    /*********************************************************************************************''
     *
     *
     *              Create an action from data in the database
     *
     * @param id                      - id from database (or 0 for creating
     * @param message
     * @param parameter
     * @param timestamp
     * @param significance
     * @param ref
     * @param campaignName
     * @param messageId
     * @param state
     * @param responseFactor
     */


    public NotificationAction(int id, String message, ActionParameter parameter, Timestamp timestamp, int significance, String ref, String campaignName, int messageId, CampaignState state, double responseFactor){

        super(id, ActionType.NOTIFICATION, parameter, timestamp, message, significance, campaignName, messageId, state, responseFactor );
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
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Message sent - (reason: "+ state.name()+") " );

        }

        if(!isLive())
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Message sent - (reason: "+ state.name()+") " );


        System.out.println("--------------------------------------------------------");
        System.out.println("! Executing " + type.name() + "("+count+"/"+size+") for player " + actionParameter.name);

        NotificationHandler handler = new NotificationHandler(testUser)
                    .withRecipient(actionParameter.facebookId)
                    .withMessage(message)
                    .withRef(ref)
                    .withPromoCode(promoCode)
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

            //TODO: Storing failed notification should go here
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

    public ActionInterface withReward(Reward reward) {

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
