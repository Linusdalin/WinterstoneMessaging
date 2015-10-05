package core;

import dbManager.ConnectionHandler;

import java.io.IOException;

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
 *               - Separate notification and email in the summary report before executing actions
 *               - Handle fail delivery of notifications and email
 *               - Analyse time of day for sessions with notification promoCode
 *               - Add exposure for message as a block
 *               - Get campaign acceptance as feedback
 *               - Check recent click to add allowed messages
 *               - Check the overrun functionality for game message over churn poke and teh 45 limit
 *               - connect Happy Hour event with Happy Hour Campaign (check that it is on)
 */

public class Execute {

    private static final int     Threshold              = 50;
    private static final int     Send_Cap               = 10000;
    private static final int     User_Cap               = 320000;
    private static final boolean DRY_RUN                = false;
    private static final boolean OVERRIDE_TIME_CONSTR   = true;
    private static final String  UserScrapeStart        = "0000-00-00";               // "2015-01-18";
    private static final String  TEST_USER              = null;                       // "627716024";          // Tina:     "105390519812878";


    private static final int     BatchSize              = 10000;


    /********************************************************************
     *
     *              Main execution
     *
     *
     * @param args
     */

    public static void main(String[] args){

        System.out.println("****************************************************\n*  Executing the WinterStone Campaign Tool");

        ConnectionHandler.Location dataSource = ConnectionHandler.Location.remote;
        CampaignEngine engine = new CampaignEngine(dataSource, Threshold, DRY_RUN, OVERRIDE_TIME_CONSTR, Send_Cap, User_Cap, TEST_USER, BatchSize);

        System.out.println(" -- DRY_RUN is      " + DRY_RUN);
        System.out.println(" -- SEND_CAP is     " + Send_Cap);

        if(Send_Cap == 1 && TEST_USER != null){

            System.out.println(" -- TEST USER ONLY!");

        }
        else if(!DRY_RUN && Send_Cap > 0){

            System.out.println(" -- THIS WILL BE A LIVE EXECUTION!");

        }



        System.out.print("Start Run?\n>");

        try {

            System.in.read();

        } catch (IOException e) {

            System.out.println("Aborting");
            return;
        }

        engine.executeRun(UserScrapeStart);

    }


}
