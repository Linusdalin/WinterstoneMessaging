package action;

import campaigns.CampaignInterface;
import campaigns.CampaignState;
import remoteData.dataObjects.User;

/*************************************************************************
 *
 *          Abstract common functionality of actions
 *
 *          An action can link to subsequent actions. They should be executed atomically
 *          This is used when a campaign is doing more than one thing (e.g. give coins AND send notifications)
 */

public abstract class Action implements ActionInterface{

    protected final ActionType type;
    protected User user;
    protected final String message;
    private ActionInterface next = null;             // Associated actions in a chain

    private int significance;
    private String campaignName;
    private int messageId;
    protected final CampaignState state;
    protected String promoCode;

    public Action(ActionType type, User user, String message, int significance, String campaignName, int messageId, CampaignState state){

        this.type = type;
        this.user = user;
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


    public User getUser(){

        return user;
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





}
