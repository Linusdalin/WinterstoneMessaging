package campaigns;

import action.ActionInterface;
import core.FailActionException;
import core.PlayerInfo;
import localData.Exposure;
import response.ResponseStat;

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

    ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor,
                             ResponseStat response) throws FailActionException;
    String getName();
    String getShortName();
    String getTag();

    String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime);
    int getCoolDown();

    boolean failCoolDown(Exposure lastExposure, Timestamp executionTime);
    int[] getAllMessageIds();
}
