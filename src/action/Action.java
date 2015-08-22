package action;

import campaigns.CampaignInterface;
import remoteData.dataObjects.User;

/*************************************************************************
 *
 *          Abstract common functionality of actions
 */

public abstract class Action implements ActionInterface{


    protected final ActionType type;
    protected User user;
    protected final String message;

    private int significance;
    private String campaignName;
    protected String promoCode;

    public Action(ActionType type, User user, String message, int significance, String campaignName){

        this.type = type;
        this.user = user;
        this.message = message;
        this.significance = significance;
        this.campaignName = campaignName;
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


    public String getCampaign(){

        return campaignName;
    }


    protected void setPromoCode(String promoCode) {

        this.promoCode = promoCode;
    }



    public boolean isFiredBy(CampaignInterface campaign) {

        return campaign.getName().equals(campaignName);

    }

}
