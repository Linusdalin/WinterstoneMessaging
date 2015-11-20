package action;

import campaigns.CampaignInterface;
import campaigns.CampaignState;
import net.sf.json.JSONObject;
import remoteData.dataObjects.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/*************************************************************************
 *
 *          Abstract common functionality of actions
 *
 *          An action can link to subsequent actions. They should be executed atomically
 *          This is used when a campaign is doing more than one thing (e.g. give coins AND send notifications)
 */

public abstract class Action implements ActionInterface{

    protected final ActionType type;
    protected final String message;
    private ActionInterface next = null;             // Associated actions in a chain

    private Timestamp timestamp;
    private double responseFactor = 1;
    private int significance;
    private String campaignName;
    private int messageId;
    protected final CampaignState state;
    protected String promoCode;

    protected ActionParameter actionParameter;


    /*******************************************************************************************
     *
     *          The base information in an action
     *
     *
     * @param type                   - type of action
     * @param user                   - The user to execute for
     * @param message                - A message to communicate
     * @param significance           - the significance (to see if actions overrule others)
     * @param campaignName           - the name of the campaign (for tracking)
     * @param messageId              - message id (within the campaign) for tracking
     * @param state                  - The state of the campaign triggering the action (for dry runs and tests)
     * @param responseFactor         - Responsefactor for this specific campaign
     */

    public Action(ActionType type, User user, Timestamp timestamp, String message, int significance, String campaignName, int messageId, CampaignState state, double responseFactor){

        this.type = type;
        this.timestamp = timestamp;
        this.responseFactor = responseFactor;
        this.actionParameter = new ActionParameter(user.name, user.facebookId, user.email);
        this.message = message;
        this.significance = significance;
        this.campaignName = campaignName;
        this.messageId = messageId;
        this.state = state;
    }

    public int getSignificance(int eligibility) {
        return (significance * eligibility)/100;
    }

    public int getSignificance() {
        return significance;
    }


    public ActionParameter getParameters(){

        return actionParameter;
    }

    public ActionType getType(){

        return type;
    }

    public ActionInterface getAssociated() {
        return next;
    }


    public boolean isLive() {
        return state.isLive();
    }


    public double getResponseFactor() {
        return responseFactor;
    }

    public void setResponseFactor(double responseFactor) {
        this.responseFactor = responseFactor;
    }


    public boolean isTestMode() {
        return state.isTestMode();
    }


    public String getCampaign(){

        return campaignName;
    }


    protected void setPromoCode(String promoCode) {

        this.promoCode = promoCode;
    }



    public boolean isFiredBy(CampaignInterface campaign) {

        return campaign.getName().equals(campaignName);

    }

    /********************************************
     *
     *      Attach subsequent actions in a chain
     *
     * @param action      - subsequent actions
     */

    public ActionInterface  attach(ActionInterface action){

        if(next != null){

            throw new RuntimeException("Accidental overriding of chained actions");
        }

        next = action;
        return this;

    }

    protected String createPromoCode(String name, int messageType) {

        String tag = name.replaceAll(" ", "");
        return tag + "-" + messageType;

    }


    protected int getMessageId() {
        return messageId;
    }


    protected void pause(int sec) {

        try {

            Thread.sleep(1000*sec);

        } catch (InterruptedException e) {

            e.printStackTrace();

        }

    }

    /*****************************************************
     *
     *       serialize  all common attributes
     *
     *
     * @return       - the common attributes as a JSON object
     */

    protected JSONObject actionAsJSON() {

        return new JSONObject()
                .put("type", type.toString())
                .put("actionParameter", actionParameter.toJSON())
                .put("message", message.replaceAll("'", ""))            // TODO: Handle escaping better
                .put("significance", significance)
                .put("campaign", campaignName)
                .put("messageId", messageId)
                .put("state", state.name());
    }


    /********************************************************************
     *
     *          Store an action to the database for later execution
     *
     * @param connection     - database connection
     * @param data           - action specific data
     */

    public void store(Connection connection, JSONObject data){


        String insert = "insert into action values ('" + timestamp.toString() + "', '" + type.name()+ "', 'PENDING', '" + data.toString() + "')";

        System.out.println("Store action with: " + insert);

        try{

            Statement statement = connection.createStatement();
            statement.execute(insert);

        }catch(SQLException e){

            System.out.println("Error accessing data in database. SQL:" + insert);
            e.printStackTrace();
        }


    }


    public void store(Connection connection){

        JSONObject data = actionAsJSON();
        store(connection, data);

    }



}
