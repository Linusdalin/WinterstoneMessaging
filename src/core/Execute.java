package core;

import dbManager.ConnectionHandler;

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
 *               - Big weekend test
 *               - Unclaimed reward reminder
 *               - Store all actions in database - not in memory to allow for one pass
 *               - Test night players click through
 *               - Complete scheduling of multiple runs, multiple sending with replace action and three batches over 24 hours
 *               - Handle sessions separately to count acceptance of rewards
 *
 *              coin error 500 for player 675417622563573
 */

public class Execute {

        private static final int     Threshold              = 50;
        private static final int     Send_Cap               = 10000;
        private static final int     User_Cap               = 125000;
        private static final boolean DRY_RUN                = false;
        private static final boolean OVERRIDE_TIME_CONSTR   = true;
        private static final String  UserScrapeStart        = "2015-08-01 07:11:07";      // "2015-01-18";
        private static final String  TEST_USER              = null;                       // "627716024";          // Tina:     "105390519812878";

        private static final boolean SEND_EMAIL             = true;
        private static final int     BatchSize              = 25000;


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
        CampaignEngine engine = new CampaignEngine(dataSource, Threshold, DRY_RUN, OVERRIDE_TIME_CONSTR, SEND_EMAIL,  Send_Cap, User_Cap, TEST_USER, BatchSize);

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


        System.out.print("Start Run?\n>");
        CampaignEngine.waitReturn();
        engine.executeRun(UserScrapeStart);

    }


}
