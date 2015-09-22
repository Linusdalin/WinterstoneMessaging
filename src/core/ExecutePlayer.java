package core;

import dbManager.ConnectionHandler;

import java.io.IOException;

/************************************************************************
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-09-21
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */


public class ExecutePlayer {

    private static final String[] testPlayers = {

            "627716024",               // Linus
            "105390519812878",         // Tina
            "10153396329897575",       // Nissen

    };

    public static void main(String[] args){

        System.out.println("****************************************************\n*  Executing the WinterStone Campaign Tool");

        ConnectionHandler.Location dataSource = ConnectionHandler.Location.remote;
        CampaignEngine engine = new CampaignEngine(dataSource);

        System.out.println(" -- Test PLayer Dry Run");
        engine.playerTest(testPlayers);

    }



}
