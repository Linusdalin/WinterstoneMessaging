package campaigns;

import action.ActionInterface;
import core.FailActionException;
import core.PlayerInfo;
import localData.Exposure;
import java.sql.Timestamp;

/************************************************************
 *
 *          General interface for campaigns
 *
 *          The main method is evaluate() that will return an appropriate action
 *          (or throw a FailException to indicate a fail reason)
 *
 *
 */
public interface CampaignInterface {

    ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor) throws FailActionException;
    String getName();
    String getShortName();
    String getTag();

    String testFailCalendarRestriction(Timestamp executionTime, boolean overrideTime);
    int getCoolDown();

    boolean failCoolDown(Exposure lastExposure, Timestamp executionTime);
    int[] getAllMessageIds();
}
