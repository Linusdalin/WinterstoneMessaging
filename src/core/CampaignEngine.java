package core;

import action.ActionInterface;
import action.ActionType;
import campaigns.CampaignInterface;
import campaigns.CampaignRepository;
import dbManager.ConnectionHandler;
import executionStatistics.ExecutionStatistics;
import localData.Exposure;
import localData.ExposureTable;
import output.Outbox;
import remoteData.dataObjects.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Calendar;

/***************************************************************************'
 *
 *              Core campaign engine going through all players and
 *              deciding which actions to perform
 *
 *               This is a test
 */

public class CampaignEngine {

    CampaignRepository repository = new CampaignRepository();
    private Connection dbConnection = null;
    private Connection cacheConnection = null;
    private Connection localConnection = null;
    private UserTable allPlayers = null;
    private int threshold;
    private boolean dryRun;
    private boolean overrideTime;
    private int analysis_cap;
    private Outbox notificationOutbox;
    private Outbox manualActionOutbox;          // Manual messages
    private Outbox emailOutbox;


    /******************************************************''
     *
     *          Create the campaign engine. Get all campaigns and
     *          load all the players from the database
     *
     *
     * @param dataSource         - database connection
     * @param threshold          - trigger threshold
     * @param send_cap           - max messages
     * @param analysis_cap       - max users to analyse
     * @param testUser           - A test user to trigger and send a message that is controllable
     *
     */

    CampaignEngine(ConnectionHandler.Location dataSource, int threshold, boolean dryRun, boolean overrideTime, int send_cap, int analysis_cap, String testUser){

        this.threshold = threshold;
        this.dryRun = dryRun;
        this.overrideTime = overrideTime;
        this.analysis_cap = analysis_cap;

        try{

            allPlayers = new UserTable();
            dbConnection    = ConnectionHandler.getConnection(dataSource);
            cacheConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);
            localConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);


            // Create an outbox for all messages

            notificationOutbox  = new Outbox(send_cap, dryRun, testUser, localConnection);
            manualActionOutbox  = new Outbox(send_cap, dryRun, testUser, localConnection);
            emailOutbox         = new Outbox(send_cap, dryRun, testUser, localConnection);

        }catch(Exception e){

            e.printStackTrace();

        }

    }


    /*****************************************************************************
     *
     *          Execute a run over the players
     *
     *          from startdate and limited by analysis_cap in number
     *
     *
     */


    public void executeRun(String startDate) {

        DataCache dbCache = new DataCache(cacheConnection, "2015-01-01", analysis_cap);

        System.out.println(" -- Starting from " + startDate);
        System.out.println(" -- Retrieving "+(analysis_cap > -1 ? analysis_cap : "all")+" players from connection...");
        allPlayers.load(dbConnection, " and users.created > '"+ startDate+"'", "ASC", analysis_cap);      // Restriction for testing
        Calendar calendar = Calendar.getInstance();
        Timestamp executionTime = new java.sql.Timestamp(calendar.getTime().getTime());
        ExposureTable campaignExposures = new ExposureTable(localConnection);
        User user = allPlayers.getNext();
        int count = 0;
        ExecutionStatistics executionStatistics = new ExecutionStatistics(CampaignRepository.activeCampaigns);


        System.out.println("******************************************************\n* Passing over all players...");

        int userCount = 0;

        while(user != null && (userCount++ < analysis_cap || analysis_cap == -1)){

            System.out.println(" ----------------------------------------------------------\n  " + userCount + "- Evaluating User "+ user.toString());

            evaluateUser(user, executionTime, dbCache, campaignExposures, executionStatistics);

            user = allPlayers.getNext();
            count++;
        }


        System.out.println(" ******************************************\n * Evaluated " + count + " users resulting in the following actions:");

        System.out.println(executionStatistics.toString());

        System.out.println("  NOTE! Dry run is " + (dryRun ? "ON" : "OFF"));

        System.out.println("Press key to enter\n>");

        try {

            System.in.read();

        } catch (IOException e) {

            System.out.println("Error getting input. Aborting");
            return;
        }

        System.out.println(" ******************************************\n * Purging Notifications ");

        //notificationOutbox.listRecepients();
        notificationOutbox.purge(executionTime);

        System.out.println(" ******************************************\n * Purging Email ");

        emailOutbox.purge(executionTime);

        System.out.println(" ******************************************\n * Manual Actions ");

        manualActionOutbox.purge(executionTime);
    }


    /************************************************************************''
     *
     *          Evaluate if and what to send to the player
     *
     *
     *
     *
     * @param user                  - the user in question
     * @param executionTime         - time of execution (for time dependent rules
     * @param dbCache               - cached information from the database
     * @param campaignExposures     - Exposure for campaigns
     * @param executionStatistics   - collection of all statistics
     */


    private void evaluateUser(User user, Timestamp executionTime, DataCache dbCache, ExposureTable campaignExposures, ExecutionStatistics executionStatistics) {


        // Package and pre calculate the playerInfo
        // This is used for all the analysis and defines the API to the information in the database

        PlayerInfo playerInfo = new PlayerInfo(user, dbCache);

        TimeAnalyser timeAnalyser = new TimeAnalyser(playerInfo);
        int eligibility = timeAnalyser.eligibilityForCommunication(campaignExposures);


        ActionInterface selectedAction = null;
        System.out.println("    (found " + playerInfo.getUser().sessions + " sessions and "+ playerInfo.getPaymentsForUser().size()+" payments for the user)");

        // Go through all campaigns and see if any of them fire.
        // We store the most significant for this user this time

        for (CampaignInterface campaign : repository.getActiveCampaigns()) {

            String failCalendarReason = campaign.testFailCalendarRestriction(executionTime, overrideTime);
            Exposure lastExposure = campaignExposures.getLastExposure(campaign.getName(), user);

            if(lastExposure != null){

                if(campaign.failCoolDown(lastExposure, executionTime)){

                    System.out.println("    -- Last exposure for campaign "+ campaign.getName()+" is " + lastExposure.exposureTime.toString() + ", less than "+ campaign.getCoolDown()+" days ago. Avoiding over exposure");
                    continue;
                }

            }

            if(failCalendarReason != null){

                System.out.println("    -- Campaign " + campaign.getName() + " not applicable. " + failCalendarReason);

            }
            else{

                ActionInterface action = campaign.evaluate(playerInfo, executionTime);

                if(isPrefered(action, selectedAction)){

                    // The action is preferred over the existing selected action
                    // Register tis and then store a new selected action

                    if(selectedAction != null)
                        executionStatistics.registerOverrun(selectedAction);

                    selectedAction = action;

                }
            }
        }

        if(selectedAction == null){

            System.out.println("    -- No action found for the user.");
            return;
        }

        if(selectedAction.getSignificance( eligibility ) < threshold){

            System.out.println("    -- Selected action significance "+ selectedAction.getSignificance(eligibility) + "not above threshold " + threshold);
            return;
        }

        queueAction(selectedAction);
        executionStatistics.registerSelected(selectedAction);

    }

    private void queueAction(ActionInterface action) {

        if(action.getType() == ActionType.NOTIFICATION )
            notificationOutbox.queue(action);
        if(action.getType() == ActionType.EMAIL )
            emailOutbox.queue(action);
        if(action.getType() == ActionType.MANUAL_ACTION )
            manualActionOutbox.queue(action);

        ActionInterface next = action.getAssociated();

        if(next != null)
            queueAction( next );

    }

    private boolean isPrefered(ActionInterface action, ActionInterface selectedAction) {

        if(action == null)
            return false;

        if(selectedAction == null)
            return true;

        if(!action.isLive())
            return false;


        if(action.getSignificance() > selectedAction.getSignificance())
            return true;

        return false;
    }

}
