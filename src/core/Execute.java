package core;

import dbManager.ConnectionHandler;
import receptivity.ReceptivityUpdater;
import transfer.Transfer;

/************************************************************************************
 *
 *              Execute is the main class for running campaigns
 *
 *              This will both produce and consume....
 *
 *              TODO:
 *
 *              *  - Get statistics per campaign+message per day
 *              *  - Test a specific player
 *              *  - Separate notification and email in the summary report before executing actions
 *              *  - Handle fail delivery of notifications and email
 *              *  - Analyse time of day for sessions with notification promoCode
 *              *  - Get campaign acceptance as feedback
 *              *  - Test Thursday players click through
 *              *  - Check resent click to add allowed messages
 *              *  - Big weekend test
 *              *  - Unclaimed reward reminder
 *              *  - Store all actions in database - not in memory - to allow for one pass
 *               - Test night players click through
 *               - Complete scheduling of multiple runs, multiple sending with replace action and three batches over 24 hours
 *
 *               - Add coins to first payment mail and schedule a reminder notification
 */

public class Execute {

        private static final int     Threshold              = 50;
        private static final int     Send_Cap               = 10000;
        private static final int     User_Cap               = 10000;
        private static final boolean DRY_RUN                = false;
        private static final boolean OVERRIDE_TIME_CONSTR   = true;
        private static final String  UserScrapeStart        = "0000-00-00";
        private static final String  TEST_USER              = null;                            // "627716024";          // Tina:     "105390519812878";

        private static final boolean SEND_EMAIL             = true;
        private static final int     BatchSize              = 50000;


        private static final boolean Transfer               = true;
        private static final boolean Receptivity_Update     = true;
        private static final boolean Instant_Purge          = true;


    /********************************************************************
     *
     *              Main execution  for scheduling messages
     *
     *
     * @param args        -
     */

    public static void main(String[] args){

        System.out.println("****************************************************\n*  Executing the WinterStone Campaign Tool");

        ConnectionHandler.Location dataSource = ConnectionHandler.Location.remote;
        CampaignEngine engine = new CampaignEngine(dataSource, Threshold, DRY_RUN, OVERRIDE_TIME_CONSTR, SEND_EMAIL,  Send_Cap, User_Cap, TEST_USER, BatchSize, Instant_Purge);

        System.out.println(" -- DRY_RUN is      " + DRY_RUN);
        System.out.println(" -- SEND_CAP is     " + Send_Cap);

        if(Send_Cap == 1 && TEST_USER != null){

            System.out.println(" -- TEST USER ONLY!");

        }
        else if(!DRY_RUN && Send_Cap > 0){

            if(SEND_EMAIL)
                System.out.println(" -- THIS WILL BE A LIVE EXECUTION!");
            else
                System.out.println(" -- THIS IS A LIVE EXECUTION, BUT NO EMAILS!");

        }

        if(Instant_Purge)
            if(DRY_RUN)
                System.out.println("The execution will dry run purge actions");
            else
                System.out.println("The execution will purge actions");
        else
            System.out.println("The execution will ONLY store actions for later");

        System.out.print("Start Run?\n>");
        CampaignEngine.waitReturn();

        if(Transfer){

            Transfer transfer = new Transfer();
            transfer.executeTransfer();
        }

        if(Receptivity_Update){

            ReceptivityUpdater updater = new ReceptivityUpdater();
            updater.executeUpdate();


        }

        engine.executeRun(UserScrapeStart);

    }


}
