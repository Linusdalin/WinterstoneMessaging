package core;

import action.Action;
import action.ActionInterface;
import campaigns.CampaignInterface;
import campaigns.CampaignRepository;
import dbManager.ConnectionHandler;
import remoteData.dataObjects.User;
import remoteData.dataObjects.UserTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/***************************************************************************'
 *
 *              Core campaign engine going through all players and deciding which actions to perform
 */
public class CampaignEngine {

    private List<CampaignInterface> activeCampaigns = null;
    private Connection dbConnection = null;
    private UserTable allPlayers = null;
    private int threshold;

    /******************************************************''
     *
     *          Create the campaign engine. Get all campaigns and
     *          load all the players from the database
     *
     *
     * @param dataSource         - database connection
     * @param threshold          - trigger threshold
     *
     */

    CampaignEngine(ConnectionHandler.Location dataSource, int threshold){

        this.threshold = threshold;

        try{

            // Get all active campaigns
            activeCampaigns = CampaignRepository.getActiveCampaigns();
            allPlayers = new UserTable();
            dbConnection = ConnectionHandler.getConnection(dataSource);

        }catch(Exception e){

            e.printStackTrace();

        }

    }


    /*****************************************************************************
     *
     *          Execute a run over all players
     *
     *
     */


    public void executeRun() {

        allPlayers.load(dbConnection);

        User user = allPlayers.getNext();
        int count = 0;

        while(user != null){

            System.out.println(" ----------------------------------------------------------\n -- Evaluating User "+ user.toString());
            evaluateUser(user);
            user = allPlayers.getNext();
            count++;
        }


        System.out.print("Executed " + count + " users");
    }

    /************************************************************************''
     *
     *          Evaluate if and what to send to the player
     *
     * @param user
     */


    private void evaluateUser(User user) {

        ActionInterface selectedAction = null;

        for (CampaignInterface activeCampaign : activeCampaigns) {

            ActionInterface action = activeCampaign.evaluate(user);

            if(isPrefered(action, selectedAction))
                selectedAction = action;

        }

        if(selectedAction == null){

            System.out.println(" -- No action found");
            return;
        }

        if(selectedAction.getSignificance() < threshold){

            System.out.println(" -- Selected action significance "+ selectedAction.getSignificance() + "not above threshold " + threshold);
            return;
        }

        selectedAction.execute();

    }

    private boolean isPrefered(ActionInterface action, ActionInterface selectedAction) {

        if(selectedAction == null)
            return true;

        if(action.getSignificance() > selectedAction.getSignificance())
            return true;

        return false;
    }
}
