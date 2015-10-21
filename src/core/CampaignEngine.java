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
import remoteData.dataObjects.User;
import remoteData.dataObjects.UserTable;
import response.ResponseHandler;
import response.ResponseStat;
import sound.SoundPlayer;

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
    private int threshold;
    private boolean dryRun;
    private boolean overrideTime;
    private boolean sendEmail;
    private int analysis_cap;
    private int batchSize;
    private Outbox notificationOutbox;
    private Outbox manualActionOutbox;          // Manual messages
    private Outbox coinActionOutbox;          // Manual messages
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

    CampaignEngine(ConnectionHandler.Location dataSource, int threshold, boolean dryRun, boolean overrideTime, boolean sendEmail, int send_cap, int analysis_cap, String testUser, int batchSize){

        this.threshold = threshold;
        this.dryRun = dryRun;
        this.overrideTime = overrideTime;
        this.sendEmail = sendEmail;
        this.analysis_cap = analysis_cap;
        this.batchSize = batchSize;

        try{

            dbConnection    = ConnectionHandler.getConnection(dataSource);
            cacheConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);
            localConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);


            // Create an outbox for all messages

            notificationOutbox  = new Outbox(send_cap, dryRun,                  testUser, localConnection);
            manualActionOutbox  = new Outbox(send_cap, dryRun,                  testUser, localConnection);
            coinActionOutbox    = new Outbox(send_cap, dryRun,                  testUser, localConnection);
            emailOutbox         = new Outbox(send_cap, (dryRun || !sendEmail),  testUser, localConnection);

        }catch(Exception e){

            e.printStackTrace();

        }

    }

    public CampaignEngine(ConnectionHandler.Location dataSource) {

        dbConnection    = ConnectionHandler.getConnection(dataSource);
        cacheConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);
        localConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);

        this.dryRun = true;
        this.overrideTime = true;

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

        Calendar calendar = Calendar.getInstance();
        Timestamp executionTime = new java.sql.Timestamp(calendar.getTime().getTime());
        ExposureTable campaignExposures = new ExposureTable(localConnection);
        int count = 0;
        ExecutionStatistics executionStatistics = new ExecutionStatistics(CampaignRepository.activeCampaigns);

        System.out.println("******************************************************\n* Passing over all players...");
        int userCount = 0;
        int batch = 1;


        // Execute batch by batch

        do{

            userCount = handleBatch(batch++, startDate, dbCache, executionTime, campaignExposures, executionStatistics, userCount);

        } while(userCount != -1);

        System.out.println(" ******************************************\n * Evaluated " + count + " users resulting in the following actions:");

        System.out.println(executionStatistics.toString());

        System.out.println("Total notifications: " + notificationOutbox.size());
        System.out.println("Total emails:        " + emailOutbox.size() + ( sendEmail? "": "( but these are suppressed)"));
        System.out.println("Total manual:        " + manualActionOutbox.size());
        System.out.println("Total coins:         " + coinActionOutbox.size());


        System.out.println("  NOTE! Dry run is " + (dryRun ? "ON" : "OFF"));

        SoundPlayer player = new SoundPlayer();
        player.playSound(SoundPlayer.ReadyBeep);

        System.out.println("\nPress Enter to Start\n>");
        waitReturn();


        System.out.println(" ******************************************\n * Purging Notifications ");

        //notificationOutbox.listRecepients();
        notificationOutbox.purge(executionTime);

        System.out.println(" ******************************************\n * Purging Email ");

        emailOutbox.purge(executionTime);

        System.out.println(" ******************************************\n * Coin Actions ");

        coinActionOutbox.purge(executionTime);

        System.out.println(" ******************************************\n * Manual Actions ");

        manualActionOutbox.purge(executionTime);
    }



    /************************************************************************************************************'
     *
     *
     *          Execute one batch
     *
     *
     * @param batchNo                       - the batch ordinal
     * @param startDate                     - original start date (not for the batch!)
     * @param dbCache                       - locally cached data
     * @param executionTime                 - time for execution
     * @param campaignExposures             - cached data
     * @param executionStatistics           - cached data
     * @param userCount                     - count to stop at cap
     *
     * @return                        - the new user count (or -1 if there are no more users to get)
     */

    private int handleBatch(int batchNo, String startDate, DataCache dbCache, Timestamp executionTime, ExposureTable campaignExposures, ExecutionStatistics executionStatistics, int userCount) {

        System.out.println(" -- Handle Batch #" + batchNo);

        UserTable allPlayers = new UserTable();
        allPlayers.load(dbConnection, " and users.created >= '"+ startDate+"'", "ASC", batchSize, userCount);      // Restriction for testing
        User user = allPlayers.getNext();

        if(user == null)
            return -1;

        while(user != null && (userCount++ < analysis_cap || analysis_cap == -1)){

            System.out.println(" ----------------------------------------------------------\n  " + userCount + "- Evaluating User "+ user.toString());
            PlayerInfo playerInfo = new PlayerInfo(user, dbCache);

            ActionInterface action = evaluateUser(playerInfo, executionTime, campaignExposures, executionStatistics);
            handleAction(action, playerInfo, campaignExposures, executionStatistics);

            user = allPlayers.getNext();
        }

        if(userCount >= analysis_cap)
            return -1;

        return userCount;
    }


    public void playerTest(String[] testPlayers) {

        DataCache dbCache = new DataCache(cacheConnection, "2015-01-01", analysis_cap);

        System.out.println(" -- testing with " + testPlayers.length + " players...");

        Calendar calendar = Calendar.getInstance();
        Timestamp executionTime = new java.sql.Timestamp(calendar.getTime().getTime());

        ExecutionStatistics executionStatistics = new ExecutionStatistics(CampaignRepository.activeCampaigns);
        ExposureTable campaignExposures = new ExposureTable(localConnection);

        System.out.println("******************************************************\n* Passing over all players...");
        int userCount = 0;

        for (String userId : testPlayers) {

            UserTable userTable = new UserTable("and facebookId = " + userId, 1);
            userTable.load(dbConnection);
            User user = userTable.getNext();

            PlayerInfo playerInfo = new PlayerInfo(user, dbCache);


            if(user == null){

                System.out.println(" ----------------------------------------------------------\n  " + userCount + "- !!!!  User "+ userId + " not found!");
                continue;
            }

            System.out.println(" ----------------------------------------------------------\n  " + userCount + "- Evaluating User "+ user.toString());

            ActionInterface action = evaluateUser(playerInfo, executionTime, campaignExposures, executionStatistics);


            if(action == null)
                System.out.println(" -- No action for player");

        }

    }


    /************************************************************************''
    *
    *          Evaluate if and what to send to the player
    *
    *
    *
    *
    * @param playerInfo                  - the user in question
    * @param executionTime         - time of execution (for time dependent rules
    * @param campaignExposures     - Exposure for campaigns
    * @param executionStatistics   - collection of all statistics
    */


    private ActionInterface evaluateUser(PlayerInfo playerInfo, Timestamp executionTime, ExposureTable campaignExposures, ExecutionStatistics executionStatistics) {

        // Package and pre calculate the playerInfo
        // This is used for all the analysis and defines the API to the information in the database

        TimeAnalyser timeAnalyser = new TimeAnalyser(playerInfo, localConnection);
        User user = playerInfo.getUser();
        ResponseHandler responseHandler = new ResponseHandler(user.facebookId, localConnection);
        int outcome = ExecutionStatistics.MISSED; // Keep track of the outcome for the user

        // Check for "dead" players that we should not even try to send a message to

        ResponseStat responseStat = responseHandler.getOverallResponse();
        boolean giveUp = false;

        if(responseStat.isStrikeout()){

            System.out.println("  !! Three strike out for player -" + responseStat.getExposures() + " attempts");
            executionStatistics.registerStrikeOut( responseStat.getExposures() );
            giveUp = true;
        }


        ActionInterface selectedAction = null;
        System.out.println("    (found " + playerInfo.getUser().sessions + " sessions and "+ playerInfo.getPaymentsForUser().size()+" payments for the user)");

        // Go through all campaigns and see if any of them fire.
        // We store the most significant for this user this time


        for (CampaignInterface campaign : repository.getActiveCampaigns()) {

            // Get a response factor for the campaign. This is used to boost significance and to decide eligibility (stored in the action)
            double responseFactor = timeAnalyser.getResponseAdjustment(user, campaign);

            Exposure lastExposure = campaignExposures.getLastExposure(campaign.getName(), user);

            if(lastExposure != null){

                if(campaign.failCoolDown(lastExposure, executionTime)){

                    System.out.println("    -- Last exposure for campaign "+ campaign.getName()+" is " + lastExposure.exposureTime.toString() + ", less than "+ campaign.getCoolDown()+" days ago. Avoiding over exposure");
                    outcome = ExecutionStatistics.COOLDOWN;
                    continue;
                }

            }

            // Check calender restriction
            String failCalendarReason = campaign.testFailCalendarRestriction(executionTime, overrideTime);
            if(failCalendarReason != null){

                System.out.println("    -- Campaign " + campaign.getName() + " not applicable. " + failCalendarReason);

            }
            else{

                ActionInterface action;
                try{

                    action = campaign.evaluate(playerInfo, executionTime, responseFactor);

                }catch(FailActionException e){

                    action = null;
                    outcome = adjustOutcome(outcome, e.getOutcome());

                }


                if(action != null && responseHandler.permanentlyFail(action.getType())){

                    System.out.println("    -- Campaign " + campaign.getName() + " not applicable. Permanently failed on sending messages of type " + action.getType() + " ignoring.");
                    continue;

                }

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

            executionStatistics.registerOutcome(outcome);

            if(responseStat.getExposures() == 0 && playerInfo.getUser().sessions > 10)
                executionStatistics.registerOverlooked();


        }
        else if(selectedAction.getType() == ActionType.NOTIFICATION && giveUp){

            System.out.println(" Removing action " + selectedAction.getCampaign() + " as we have given up on the player");
            executionStatistics.registerOutcome(ExecutionStatistics.GIVEUP);
            return null;
        }



        return selectedAction;
    }

    private int adjustOutcome(int existingOutcome, int newOutcome) {

        if(newOutcome < existingOutcome)
            return newOutcome;

        return existingOutcome;
    }

    /***********************************************************************
     *
     *              Actually handle the action that is selected for the user. This includes:
     *
     *               - See if we actually should send actions to the user
     *               - Queue it
     *               - Register
     *
     * @param selectedAction
     * @param playerInfo
     * @param campaignExposures
     * @param executionStatistics
     */


    private void handleAction(ActionInterface selectedAction, PlayerInfo playerInfo, ExposureTable campaignExposures, ExecutionStatistics executionStatistics){

        TimeAnalyser timeAnalyser = new TimeAnalyser(playerInfo, localConnection);
        User user = playerInfo.getUser();
        ResponseHandler handler = new ResponseHandler( user.facebookId, localConnection );
        int eligibility = timeAnalyser.eligibilityForCommunication(campaignExposures, handler);

        if(selectedAction == null){

            System.out.println("    -- No action found for the user.");
            return;
        }

        eligibility = timeAnalyser.adjustForResponse(eligibility, selectedAction );

        if(selectedAction.getSignificance( eligibility ) < threshold){

            System.out.println("    -- Selected action significance "+ selectedAction.getSignificance(eligibility) + "not above threshold " + threshold);
            executionStatistics.registerOutcome(ExecutionStatistics.EXPOSED);
            return;
        }

        executionStatistics.registerOutcome(ExecutionStatistics.REACHED);
        queueAction(selectedAction);
        executionStatistics.registerSelected(selectedAction);

    }

    /**************************************************************************
     *
     *          Put the actions in the correct outbox (depending on type)
     *
     *
     * @param action             - the action
     */


    private void queueAction(ActionInterface action) {

        if(action.getType() == ActionType.NOTIFICATION )
            notificationOutbox.queue(action);

        if(action.getType() == ActionType.EMAIL )
            emailOutbox.queue(action);

        if(action.getType() == ActionType.MANUAL_ACTION )
            manualActionOutbox.queue(action);

        if(action.getType() == ActionType.COIN_ACTION )
            coinActionOutbox.queue(action);

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

    public static void waitReturn() {
        try {

            System.in.read();

        } catch (IOException e) {

            System.out.println("Error getting input. Ignoring");
        }

    }

}
