package action;

import campaigns.CampaignInterface;
import remoteData.dataObjects.User;

import java.sql.Connection;
import java.sql.Timestamp;

/***********************************************************************
 *
 *              Common interfaces for actions
 */


public interface ActionInterface {

    ActionResponse execute(boolean dryRun, String testUser, Timestamp executionTime, Connection localConnection);

    int getSignificance(int eligibility);
    int getSignificance();

    User getUser();
    public String getCampaign();

    boolean isFiredBy(CampaignInterface campaign);
    ActionInterface attach(ActionInterface action);

    ActionType getType();

    ActionInterface getAssociated();

    boolean isLive();
}
