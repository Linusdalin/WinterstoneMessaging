package executionStatistics;

import action.ActionInterface;
import campaigns.CampaignInterface;

import java.util.ArrayList;
import java.util.List;

/***********************************************************************
 *
 *              Execution statistics collects information about executed
 *              and passed actions for all active campaigns
 *
 */

public class ExecutionStatistics {

    CampaignStatistics[] campaignStatistics;              // statistics per campaign
    private List<CampaignInterface> activeCampaigns;

    private int[] totalPlayerOutcome = {0, 0, 0, 0, 0};

    private int[] strikeCount = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};


    // Index for the different outcomes for messaging (indexing the totalPlayerOutcome)

    public static final int REACHED     = 0;      // # players we send a message
    public static final int EXPOSED     = 1;      // # players we don't send because they are fully exposed
    public static final int GIVEUP      = 2;      // # players for which we have given up
    public static final int COOLDOWN    = 3;      // # players we cant reach only because the campaigns are cooling down
    public static final int MISSED      = 4;      // # players there is no message to send to
    private int overLooked;
    private List<String> overLookedPlayers = new ArrayList<>(200);

    /****************************************************************
     *
     *
     *              Create
     *
     * @param activeCampaigns  - campaigns in use
     *
     */


    public ExecutionStatistics(List<CampaignInterface> activeCampaigns) {

        this.activeCampaigns = activeCampaigns;
        campaignStatistics = new CampaignStatistics[activeCampaigns.size()];

    }

    /*****************************************************'
     *
     *          Register that an action is selected and will be used to target a player
     *
     * @param selectedAction            - the action
     */



    public void registerSelected(ActionInterface selectedAction) {

        int campaignIndex = getCampaignIndex(selectedAction.getCampaign());

        if(campaignStatistics[campaignIndex] == null)
            campaignStatistics[campaignIndex] = new CampaignStatistics(selectedAction.getCampaign());

        if(selectedAction.isLive())
            campaignStatistics[campaignIndex].countFired(selectedAction.getType());
        else
            campaignStatistics[campaignIndex].countPotential();

    }


    /*************************************************************************
     *
     *          Register that an action is overrun by another action (otherwise would have been used)
     *
     *
     * @param action          - the action
     */

    public void registerOverrun(ActionInterface action) {

        int campaignIndex = getCampaignIndex(action.getCampaign());

        if(campaignStatistics[campaignIndex] == null)
            campaignStatistics[campaignIndex] = new CampaignStatistics(action.getCampaign());

        campaignStatistics[campaignIndex].countOverrun();

    }

    public void registerCoolDown(CampaignInterface campaign) {

        int campaignIndex = getCampaignIndex(campaign.getName());

        if(campaignStatistics[campaignIndex] == null)
            campaignStatistics[campaignIndex] = new CampaignStatistics(campaign.getName());

        campaignStatistics[campaignIndex].countCoolDown();

    }

    public void registerOutcome(int reason){


        System.out.println("Adding a total to reason " + reason);

        totalPlayerOutcome[ reason ]++;

    }

    private int getCampaignIndex(String campaignName) {

        int campaignIx = 0;
        for (CampaignInterface activeCampaign : activeCampaigns) {

            if(activeCampaign.getName().equals(campaignName))
                return campaignIx;
            campaignIx++;
        }

        throw new RuntimeException("Could not find campaign " + campaignName + " references from action");

    }


    /******************************************************************
     *
     *          Display both campaign and outbox message statistics
     *
     *
     * @return         - text
     */


    public String toString(){

        StringBuilder out = new StringBuilder();
        for (CampaignStatistics statisticsForCampaign : campaignStatistics) {

            if(statisticsForCampaign != null)
                out.append(statisticsForCampaign.getName() + ":" + statisticsForCampaign.toString() + "\n");

        }

        out.append("Reached:   " + totalPlayerOutcome[ REACHED ] + "\n");
        out.append("Exposed:   " + totalPlayerOutcome[ EXPOSED ] + "\n");
        out.append("GivenUp:   " + totalPlayerOutcome[ GIVEUP ] + "\n");
        out.append("Cool down: " + totalPlayerOutcome[ COOLDOWN ] + "\n");
        out.append("Missed:    " + totalPlayerOutcome[ MISSED ] + "\n");

        //for (int i = 6; i < strikeCount.length; i++) {
        //    out.append(" - Strikeout: " + i + ": " + strikeCount[i] + "\n");
        //}

        out.append("\nCompletely overlooked pretty active players: " + overLooked + "\n");


        /*
        for (String overLookedPlayer : overLookedPlayers) {

           out.append("\"" + overLookedPlayer + "\", ");
        }

        */
        out.append("\n");
        return out.toString();

    }

    public void registerStrikeOut(int attempts) {

        if(attempts > 12)
            attempts = 12;

        strikeCount[ attempts ] ++;

    }

    public void registerOverlooked(String userId) {

        // Only store some of them for review
        if(overLooked < 20)
            overLookedPlayers.add(userId);

        overLooked++;


    }
}
