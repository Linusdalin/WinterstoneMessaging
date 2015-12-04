package action;

import campaigns.CampaignInterface;

import java.sql.Connection;
import java.sql.Timestamp;

/***********************************************************************
 *
 *              Common interfaces for actions
 */


public interface ActionInterface {

    ActionResponse execute(boolean dryRun, String testUser, Timestamp executionTime, Connection localConnection, int count, int size);

    int getSignificance(int eligibility);
    int getSignificance();

    ActionParameter getParameters();
    public String getCampaign();

    boolean isFiredBy(CampaignInterface campaign);
    ActionInterface attach(ActionInterface action);

    ActionType getType();

    ActionInterface getAssociated();

    boolean isLive();

    double getResponseFactor();
    void setResponseFactor(double i);

    void store(Connection connection);
    void updateAsExecuted(Connection connection);

    int getSchedulingTime();

}
