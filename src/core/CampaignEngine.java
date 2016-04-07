package core;

import action.ActionInterface;
import action.ActionType;
import action.NotificationAction;
import campaigns.AbstractCampaign;
import campaigns.CampaignInterface;
import campaigns.CampaignRepository;
import campaigns.CampaignState;
import dbManager.ConnectionHandler;
import dbManager.ConnectionPool;
import dbManager.DatabaseException;
import executionStatistics.ExecutionStatistics;
import hailMary.HailMaryUsers;
import localData.Exposure;
import localData.ExposureTable;
import output.Outbox;
import receptivity.ReceptivityProfile;
import remoteData.dataObjects.User;
import remoteData.dataObjects.UserTable;
import response.ResponseHandler;
import response.ResponseStat;
import rewards.RewardRepository;
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
    private boolean sendNotification;
    private boolean sendEventTrigger;
    private int analysis_cap;
    private int batchSize;
    private boolean purge;
    private Outbox notificationOutbox;
    private Outbox pushOutbox;
    private Outbox manualActionOutbox;          // Manual messages
    private Outbox coinActionOutbox;          // Manual messages
    private Outbox emailOutbox;
    private Outbox eventTriggerOutbox;

    private int giveUp = 0;

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

    CampaignEngine(ConnectionHandler.Location dataSource, int threshold, boolean dryRun, boolean overrideTime, boolean sendEmail, boolean sendNotification, boolean sendEventTrigger, int send_cap, int analysis_cap, String testUser, int batchSize, boolean purge){

        this.threshold = threshold;
        this.dryRun = dryRun;
        this.overrideTime = overrideTime;
        this.sendEmail = sendEmail;
        this.sendNotification = sendNotification;
        this.sendEventTrigger = sendEventTrigger;
        this.analysis_cap = analysis_cap;
        this.batchSize = batchSize;
        this.purge = purge;

        try{

            dbConnection    = ConnectionHandler.getConnection(ConnectionHandler.Location.remote);
            cacheConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);
            localConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);


            // Create an outbox for all messages

            notificationOutbox  = new Outbox(send_cap, dryRun,                  testUser);
            eventTriggerOutbox  = new Outbox(send_cap, dryRun,                  testUser);
            pushOutbox          = new Outbox(send_cap, dryRun,                  testUser);
            manualActionOutbox  = new Outbox(send_cap, dryRun,                  testUser);
            coinActionOutbox    = new Outbox(send_cap, dryRun,                  testUser);
            emailOutbox         = new Outbox(send_cap, (dryRun || !sendEmail),  testUser);

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
        int count = 0;
        ExecutionStatistics executionStatistics = new ExecutionStatistics(CampaignRepository.activeCampaigns);

        System.out.println("******************************************************\n* Passing over all players...");
        int userCount = 0;
        int batch = 1;


        // Execute batch by batch

        do{

            try{

                //Connection tempConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);
                Connection tempConnection = ConnectionPool.getStatic();

                userCount = handleBatch(tempConnection, batch++, startDate, dbCache, executionTime, executionStatistics, userCount);
                tempConnection.close();
                System.gc();


            }catch(Exception e){

                e.printStackTrace();
                SoundPlayer.playSound(SoundPlayer.FailBeep);
                return;
            }

        } while(userCount != -1);


        while(!selectAndProceed(executionStatistics));



        // Check if we want an immediate purge or separatey executing the actions in the database
        if(purge){

            Connection connection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);


            System.out.println(" ******************************************\n * Coin Actions ");

            coinActionOutbox.purge(executionTime, connection);

            System.out.println(" ******************************************\n * Purging Event Triggers ");

            if(sendEventTrigger)
                eventTriggerOutbox.purge(executionTime, connection);
            else
                System.out.println(" -- supressed!");


            System.out.println(" ******************************************\n * Purging Notifications ");

            if(sendNotification)
                notificationOutbox.purge(executionTime, connection);
            else
                System.out.println(" -- supressed!");

            System.out.println(" ******************************************\n * Purging Mobile Push Notifications");

            //notificationOutbox.listRecepients();
            pushOutbox.purge(executionTime, connection);

            System.out.println(" ******************************************\n * Purging Email ");

            if(sendEmail)
                emailOutbox.purge(executionTime, connection);
            else
                System.out.println(" -- supressed!");

            System.out.println(" ******************************************\n * Manual Actions ");

            manualActionOutbox.purge(executionTime, connection);

            SoundPlayer.playSound(SoundPlayer.ReadyBeep);

        }

    }

    private boolean selectAndProceed(ExecutionStatistics executionStatistics) {


        System.out.println(" ******************************************\n * Evaluated all users resulting in the following statistics:\n\n");

        System.out.println(executionStatistics.toString());

        System.out.println(" ******************************************\n * Outboxes to purge:\n\n");

        System.out.println("Total notifications:  " + notificationOutbox.size() + ( sendNotification? "": "( but these are suppressed)"));
        System.out.println("Total push:           " + pushOutbox.size());
        System.out.println("Total emails:         " + emailOutbox.size() + ( sendEmail? "": "( but these are suppressed)"));
        System.out.println("Total manual:         " + manualActionOutbox.size());
        System.out.println("Total coins:          " + coinActionOutbox.size());
        System.out.println("Total event triggers: " + eventTriggerOutbox.size());


        System.out.println("  NOTE! Dry run is " + (dryRun ? "ON" : "OFF"));

        SoundPlayer.playSound(SoundPlayer.ReadyBeep);

        System.out.println("\nPress Enter to Start\n>");
        String command = waitReturn();

        if(command == null)
            return true;

        update(command);
        return false;

    }

    /******************************************************************************
     *
     *          Update the messages
     *
     *
     * @param command
     */

    private void update(String command) {
        //TODO: parse and remove messages for a specific campaign/messageID

        System.out.println(" -- Adjusting messages to send with \""+ command+"\"");

        String[] token = command.split(" ");

        if(token.length == 0){

            System.out.println("   !! Expecting command");
            return;

        }

        if(token[0].equalsIgnoreCase("remove")){

            try{

                if(token.length != 3){

                    System.out.println("   !! Expecting remove <campaign> <messageId>");
                    return;
                }

                String campaign = token[1];
                int messageId = Integer.valueOf(token[2]);

                System.out.println("   -- removing all actions with campaign name '"+campaign+"' and messageId=" + messageId);
                int removed = removeAll(campaign, messageId, notificationOutbox, pushOutbox, emailOutbox);
                System.out.println("   -- Removed all in all " + removed + " actions");
                return;

            }catch(Exception e){

                e.printStackTrace();
                System.out.println(" !! Could not parse'" + command + "'");
                return;
            }

        }

        System.out.println(" !! Could not understand '" + command + "'");

        return;

    }

    //TODO: This could be optimized by analyzing the ranges of the messageIds

    private int removeAll(String campaign, int messageId, Outbox notificationOutbox, Outbox pushOutbox, Outbox emailOutbox) {

        int count = 0;

        count += notificationOutbox.removeAll(campaign, messageId);
        count += pushOutbox.removeAll(campaign, messageId);
        count += emailOutbox.removeAll(campaign, messageId);

        return count;
    }


    /************************************************************************************************************'
     *
     *
     *          Execute one batch
     *
     *
     *
     * @param tempConnection                - connection to database
     * @param batchNo                       - the batch ordinal
     * @param startDate                     - original start date (not for the batch!)
     * @param dbCache                       - locally cached data
     * @param executionTime                 - time for execution
     * @param executionStatistics           - cached data
     * @param userCount                     - count to stop at cap
     *
     * @return                        - the new user count (or -1 if there are no more users to get)
     */

    private int handleBatch(Connection tempConnection, int batchNo, String startDate, DataCache dbCache, Timestamp executionTime, ExecutionStatistics executionStatistics, int userCount) {



        System.out.println(" -- Handle Batch #" + batchNo);

        UserTable allPlayers = new UserTable();
        loadWithRetry(allPlayers, startDate, userCount);
        User user = allPlayers.getNext();
        ExposureTable campaignExposures = new ExposureTable(tempConnection);

        if(user == null)
            return -1;

        while(user != null && (userCount++ < analysis_cap || analysis_cap == -1)){

            // Loop over all users.
            //   - Get the information,
            //   - get an appropriate action (depending on user info and previous activity and finally
            //   - handle the action (queue and store statistics)


            System.out.println(" ----------------------------------------------------------\n  " + userCount + "- Evaluating User "+ user.toString());
            PlayerInfo playerInfo = new PlayerInfo(user, dbCache, tempConnection);

            int paymentBehavior = playerInfo.getPaymentBehavior();
            System.out.println(" -- Paymentbehavior = " + paymentBehavior);

            executionStatistics.registerReceptivityDay(playerInfo.getReceptivityForPlayer().getFavouriteDay(ReceptivityProfile.SignificanceLevel.GENERAL));
            executionStatistics.registerReceptivityTime(playerInfo.getReceptivityForPlayer().getFavouriteTimeOfDay(ReceptivityProfile.SignificanceLevel.GENERAL));

            ActionInterface action = evaluateUser(tempConnection, playerInfo, executionTime, campaignExposures, executionStatistics);
            handleAction(action, playerInfo, campaignExposures, executionStatistics);

            campaignExposures.close();

            user = allPlayers.getNext();
        }

        if(userCount >= analysis_cap)
            return -1;

        //allPlayers.close();
        return userCount;
    }

    private void loadWithRetry(UserTable allPlayers, String startDate, int userCount) {

        boolean retry = false;

        do{

            try {
                allPlayers.load(dbConnection, " and users.created >= '"+ startDate+"' and users.uninstall=0", "ASC", batchSize, userCount);      // Restriction for testing

            } catch (DatabaseException e) {

                SoundPlayer.playSound(SoundPlayer.FailBeep);
                System.out.println("Error with the database. Retry?\n>");

                waitReturn();
                retry = true;
                dbConnection    = ConnectionHandler.getConnection(ConnectionHandler.Location.remote);


            }

        }while(retry);
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

            UserTable userTable = new UserTable("and facebookId = '" + userId + "'", 1);
            try {
                userTable.load(dbConnection);
            } catch (DatabaseException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            User user = userTable.getNext();

            PlayerInfo playerInfo = new PlayerInfo(user, dbCache, localConnection);


            if(user == null){

                System.out.println(" ----------------------------------------------------------\n  " + userCount + "- !!!!  User "+ userId + " not found!");
                continue;
            }

            System.out.println(" ----------------------------------------------------------\n  " + userCount + "- Evaluating User "+ user.toString());

            ActionInterface action = evaluateUser(localConnection, playerInfo, executionTime, campaignExposures, executionStatistics);


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


    private ActionInterface evaluateUser(Connection tempConnection, PlayerInfo playerInfo, Timestamp executionTime, ExposureTable campaignExposures, ExecutionStatistics executionStatistics) {

        if(giveUpPlayer(playerInfo, executionTime)){

            executionStatistics.registerLostPlayer();
            return null;
        }

        User user = playerInfo.getUser();
        Exposure lastExposure = campaignExposures.getLastExposure(user);
        if(lastExposure != null && AbstractCampaign.getDaysBetween(lastExposure.exposureTime, executionTime) == 0){

            System.out.println("Already exposed today. Ignoring");
            return null;
        }


        // Package and pre calculate the playerInfo
        // This is used for all the analysis and defines the API to the information in the database

        TimeAnalyser timeAnalyser = new TimeAnalyser(playerInfo, tempConnection);
        ResponseHandler responseHandler = new ResponseHandler(user.facebookId, tempConnection);


        int outcome = ExecutionStatistics.MISSED; // Keep track of the outcome for the user

        // Check for "dead" players that we should not even try to send a message to

        ResponseStat responseStat = responseHandler.getOverallResponse();
        boolean giveUp = false;


        if(responseStat.isStrikeout() && user.payments == 0){

            System.out.println("  !! Three strike out for player -" + responseStat.getExposures() + " attempts");
            executionStatistics.registerStrikeOut( responseStat.getExposures() );
            giveUp = true;
        }


        ActionInterface selectedAction = null;
        System.out.println("    (found " + playerInfo.getUser().sessions + " sessions and "+ playerInfo.getPaymentsForUser().size()+" payments for the user)");

        // Go through all campaigns and see if any of them fire.
        // We store the most significant for this user this time


        for (CampaignInterface campaign : repository.getActiveCampaigns()) {

            // Check calender restriction
            String failCalendarReason = campaign.testFailCalendarRestriction(playerInfo, executionTime, overrideTime);
            if(failCalendarReason != null){

                System.out.println("    -- Campaign " + campaign.getName() + " not applicable. " + failCalendarReason);
                continue;
            }



            // Get a response factor for the campaign. This is used to boost significance and to decide eligibility (stored in the action)
            double responseFactor = timeAnalyser.getResponseAdjustment(user, campaign);

            lastExposure = campaignExposures.getLastExposure(campaign.getName(), user);

            if(lastExposure != null){

                if(campaign.failCoolDown(lastExposure, executionTime)){

                    System.out.println("    -- Last exposure for campaign "+ campaign.getName()+" is " + lastExposure.exposureTime.toString() + ", less than "+ campaign.getCoolDown()+" days ago. Avoiding over exposure");
                    outcome = ExecutionStatistics.COOLDOWN;
                    executionStatistics.registerCoolDown(campaign);
                    continue;
                }

            }

            ActionInterface action;
            try{

                ResponseStat campaignResponse = responseHandler.getCampaignResponse( campaign.getName() );
                action = campaign.evaluate(playerInfo, executionTime, responseFactor, campaignResponse);

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

        if(selectedAction == null){

            executionStatistics.registerOutcome(outcome);

            if(responseStat.getExposures() == 0 && playerInfo.getUser().sessions > 10 && !playerInfo.getUser().email.equals(""))
                executionStatistics.registerOverlooked(playerInfo.getUser().facebookId);


        }
        else if(selectedAction.getType() == ActionType.NOTIFICATION && giveUp){

            System.out.println(" Removing action " + selectedAction.getCampaign() + " as we have given up on the player");
            executionStatistics.registerOutcome(ExecutionStatistics.GIVEUP);

            ActionInterface associatedAction = selectedAction.getNext();

            if(associatedAction != null){

                // There is an associated action. If this is a fore coin action, we should execute it anyway

                if(associatedAction.getType() == ActionType.COIN_ACTION && associatedAction.isForced()){

                    return associatedAction;
                }


            }
            return null;
        }


        return selectedAction;
    }

    private boolean giveUpPlayer(PlayerInfo playerInfo, Timestamp executionTime) {

        if(playerInfo.getUser().sessions <= 1 && AbstractCampaign.getDaysBetween(playerInfo.getUser().created, executionTime) > 200)
            return true;

        if(playerInfo.getUser().sessions <= 3 &&  playerInfo.getUser().payments == 0 && playerInfo.getLastSession() != null  && AbstractCampaign.getDaysBetween(playerInfo.getLastSession(), executionTime) > 240)
            return true;

        return false;
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
     * @param selectedAction             - the action to execute (or null when there are no action
     * @param playerInfo                 - information about the player
     * @param campaignExposures          - information about the exposures
     * @param executionStatistics        - execution
     */


    private void handleAction(ActionInterface selectedAction, PlayerInfo playerInfo, ExposureTable campaignExposures, ExecutionStatistics executionStatistics){

        TimeAnalyser timeAnalyser = new TimeAnalyser(playerInfo, localConnection);
        User user = playerInfo.getUser();

        if(selectedAction == null){

            System.out.println("    -- No action found for the user.");
            return;
        }

        ResponseHandler handler = new ResponseHandler( user.facebookId, localConnection );
        int eligibility = timeAnalyser.eligibilityForCommunication(campaignExposures, handler, selectedAction, executionStatistics);

        eligibility = timeAnalyser.adjustForResponse(eligibility, selectedAction );

        if(selectedAction.getSignificance( eligibility ) < threshold){

            System.out.println("    -- Selected action significance "+ selectedAction.getSignificance(eligibility) + "not above threshold " + threshold);
            executionStatistics.registerOutcome(ExecutionStatistics.EXPOSED);
            return;
        }

        executionStatistics.registerOutcome(ExecutionStatistics.REACHED);
        queueAction(selectedAction, localConnection);


        if(selectedAction.getMessageId() >= 400){

            System.out.println("Id too high " + selectedAction.getMessageId() + " for action " + selectedAction.getCampaign());
        }

        executionStatistics.registerSelected(selectedAction);

    }

    /**************************************************************************
     *
     *          Put the actions in the correct outbox (depending on type)
     *
     * @param action             - the action
     *
     */


    private void queueAction(ActionInterface action, Connection connection) {

        if(purge){

            switch (action.getType()) {

                case NOTIFICATION:
                    notificationOutbox.queue(action);
                    break;
                case PUSH:
                    pushOutbox.queue(action);
                    break;
                case MANUAL_ACTION:
                    manualActionOutbox.queue(action);
                    break;
                case EMAIL:
                    emailOutbox.queue(action);
                    break;
                case IN_GAME:
                    // Nothing implemented
                    break;
                case COIN_ACTION:
                    coinActionOutbox.queue(action);
                    break;
                case TRIGGER_EVENT:
                    eventTriggerOutbox.queue(action);
                    break;

                default:
                    throw new RuntimeException("Unknown action type " + action.getType().name());

            }
        }


        // Store in the database for
        action.store(connection);

        ActionInterface next = action.getAssociated();

        if(next != null){
           queueAction( next, connection );

        }
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

    /****************************************************************
     *
     *              Wait for a enter and return teh test entered
     *
     *
     * @return          - the text
     */

    public static String waitReturn() {
        try {

            byte[] buffer = new byte[1024];
            System.in.read(buffer);

            if(buffer[0] == 10)
                return null;

            return new String(buffer);

        } catch (IOException e) {

            System.out.println("Error getting input. Ignoring");
        }

        return null;

    }

    /****************************************************************************************************
     *
     *              Hail Mary is a test performed on a set of 12,000 users that are not in the
     *              SQL database. Probably due to misses in the registration or that they are older.
     *
     *
     * @param executionTime             - time for execution (for logging)
     * @param dbCache                   - data about the user. (Not used)
     * @param executionStatistics
     * @param localConnection
     */


    public void hailMary(Timestamp executionTime, DataCache dbCache, ExecutionStatistics executionStatistics, Connection localConnection ) {

        String name = "hailMary";
        ExposureTable campaignExposures = new ExposureTable(localConnection);


        for (String lostUser : HailMaryUsers.lostUsers) {

            User user = new User(lostUser, "", "", "", "", null, 0, 0, 0, 0, 0, 0, 0, 0, "A", "M", Timestamp.valueOf("2016-01-01 00:00:00"));
            PlayerInfo playerInfo = new PlayerInfo(user, dbCache, localConnection);

            ActionInterface action = new NotificationAction( "Hello, We ar missing you at SlotAmrica. To welcome you back we have added "+ RewardRepository.freeCoinAcitivation.getCoins()+" free coins for you to play with on your account. Click here to collect and play!",
                    user, executionTime, 60, name,  name, 1, CampaignState.ACTIVE, 1.0)
                    .withReward(RewardRepository.freeCoinAcitivation);

            handleAction(action, playerInfo, campaignExposures, executionStatistics);

        }


    }

}
