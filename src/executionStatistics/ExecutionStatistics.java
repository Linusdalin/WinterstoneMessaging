package executionStatistics;

import action.ActionInterface;
import campaigns.CampaignInterface;

import java.util.List;

/***********************************************************************
 *
 *              Execution statistics collects information about executed
 *              and passed actions for all active campaigns
 *
 */

public class ExecutionStatistics {

    CampaignStatistics[] campaignStatistics;
    private List<CampaignInterface> activeCampaigns;

    public ExecutionStatistics(List<CampaignInterface> activeCampaigns) {
        this.activeCampaigns = activeCampaigns;

        campaignStatistics = new CampaignStatistics[activeCampaigns.size()];

    }

    public void registerSelected(ActionInterface selectedAction) {

        int campaignIndex = getCampaignIndex(selectedAction);

        if(campaignStatistics[campaignIndex] == null)
            campaignStatistics[campaignIndex] = new CampaignStatistics(selectedAction.getCampaign());

        if(selectedAction.isLive())
            campaignStatistics[campaignIndex].countFired(selectedAction.getType());
        else
            campaignStatistics[campaignIndex].countPotential();

    }

    public void registerOverrun(ActionInterface action) {

        int campaignIndex = getCampaignIndex(action);

        if(campaignStatistics[campaignIndex] == null)
            campaignStatistics[campaignIndex] = new CampaignStatistics(action.getCampaign());

        campaignStatistics[campaignIndex].countOverrun();

    }


    private int getCampaignIndex(ActionInterface action) {

        int campaignIx = 0;
        for (CampaignInterface activeCampaign : activeCampaigns) {

            if(activeCampaign.getName().equals(action.getCampaign()))
                return campaignIx;
            campaignIx++;
        }

        throw new RuntimeException("Could not find campaign " + action.getCampaign() + " references from action");

    }

    public String toString(){

        StringBuffer out = new StringBuffer();
        for (CampaignStatistics statisticsForCampaign : campaignStatistics) {

            if(statisticsForCampaign != null)
                out.append(statisticsForCampaign.getName() + ":" + statisticsForCampaign.toString() + "\n");

        }

        return out.toString();

    }

}
