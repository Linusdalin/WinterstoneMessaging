package campaigns;

import action.ActionInterface;
import core.PlayerInfo;
import localData.Exposure;

import java.sql.Timestamp;

/************************************************************
 *
 *          General interface for campaigns
 *
 */
public interface CampaignInterface {

    ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime);
    String getName();

    String testFailCalendarRestriction(Timestamp executionTime, boolean overrideTime);
    int getCoolDown();

    boolean failCoolDown(Exposure lastExposure, Timestamp executionTime);
}
