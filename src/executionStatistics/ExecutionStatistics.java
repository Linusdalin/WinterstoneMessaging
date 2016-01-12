package executionStatistics;

import action.ActionInterface;
import campaigns.CampaignInterface;
import campaigns.CampaignRepository;
import statistics.Display;

import java.util.ArrayList;
import java.util.List;

/***********************************************************************
 *
 *              Execution statistics collects information about executed
 *              and passed actions for all active campaigns
 *
 */

public class ExecutionStatistics {

    CampaignStatistics[][] campaignStatistics;              // statistics per campaign
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
     *              Create.
     *
     *              For each campaign we allow for 100 message ids.
     *
     * @param activeCampaigns  - campaigns in use
     *
     */


    public ExecutionStatistics(List<CampaignInterface> activeCampaigns) {

        this.activeCampaigns = activeCampaigns;
        campaignStatistics = new CampaignStatistics[activeCampaigns.size()][100];

        int index = 0;

        for (CampaignStatistics[] campaignStatistic : campaignStatistics) {

            campaignStatistic[0] = new CampaignStatistics(CampaignRepository.activeCampaigns.get(index++).getName(), 0);
        }


    }

    /*****************************************************'
     *
     *          Register that an action is selected and will be used to target a player
     *
     * @param selectedAction            - the action
     */



    public void registerSelected(ActionInterface selectedAction) {

        int campaignIndex = getCampaignIndex(selectedAction.getCampaign());
        int messageId = selectedAction.getMessageId();

        if(campaignStatistics[campaignIndex][messageId] == null)
            campaignStatistics[campaignIndex][messageId] = new CampaignStatistics(selectedAction.getCampaign(), messageId);

        if(selectedAction.isLive())
            campaignStatistics[campaignIndex][messageId].countFired(selectedAction.getType());
        else
            campaignStatistics[campaignIndex][messageId].countPotential();

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
        int messageId = action.getMessageId();

        if(campaignStatistics[campaignIndex][messageId] == null)
            campaignStatistics[campaignIndex][messageId] = new CampaignStatistics(action.getCampaign(), messageId);

        campaignStatistics[campaignIndex][messageId].countOverrun();

    }

    public void registerCoolDown(CampaignInterface campaign) {

        int campaignIndex = getCampaignIndex(campaign.getName());

        if(campaignStatistics[campaignIndex][0] == null)
            campaignStatistics[campaignIndex][0] = new CampaignStatistics(campaign.getName(), 0);

        campaignStatistics[campaignIndex][0].countCoolDown();

    }

    public void registerOutcome(int reason){


        //System.out.println("Adding a total to reason " + reason);

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
        for (CampaignStatistics[] statisticsForCampaign : campaignStatistics) {

            if(statisticsForCampaign != null)
                out.append(statisticsForCampaign[0].getName() + ":\n");

            for (CampaignStatistics statisticsForMessage : statisticsForCampaign) {

                if(statisticsForMessage != null && statisticsForMessage.getId() != 0)
                    out.append(statisticsForMessage.toString() + "\n");

            }
        }

        out.append("\n*******************************************\nPlayer Demographics statistics:\n\n");
        out.append("No data compiled\n\n");


        out.append("\n*******************************************\nPlayer Exposure statistics:\n\n");
        out.append("No data compiled\n\n");


        out.append("\n*******************************************\nReach statistics:\n\n");


        out.append(" - Reached:   " + Display.fixedLengthRight(totalPlayerOutcome[ REACHED ], 4) + " (Sending to today)\n");
        out.append(" - Exposed:   " + Display.fixedLengthRight(totalPlayerOutcome[ EXPOSED ], 4) + " (Cant reach because the player exposure limit)\n");
        out.append(" - GivenUp:   " + Display.fixedLengthRight(totalPlayerOutcome[ GIVEUP  ], 4) + " (No point - no answer)\n");
        out.append(" - Cool down: " + Display.fixedLengthRight(totalPlayerOutcome[ COOLDOWN], 4) + " (Cant reach because all campaigns are cooling down.\n");
        out.append(" - Missed:    " + Display.fixedLengthRight(totalPlayerOutcome[ MISSED  ], 4) + " (No campaign appropriate for the player)\n");

        out.append("\n - Completely overlooked pretty active players: " + overLooked + "\n");


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
