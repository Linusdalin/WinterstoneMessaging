package core;

import dbManager.ConnectionHandler;

/***************************************************************************************'''
 *
 *              Execute is the main class for running campaigns
 */

public class Execute {

    private static final int Threshold = 50;

    public static void main(String[] args){

        System.out.println("****************************************************\n*  Executing the Winterstone Campaign Tool");

        ConnectionHandler.Location dataSource = ConnectionHandler.Location.local;
        System.out.println(" --  Setting connection to " + dataSource.name());


        CampaignEngine engine = new CampaignEngine(dataSource, Threshold);
        engine.executeRun();

    }

}
