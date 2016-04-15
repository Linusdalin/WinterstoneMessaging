package firstGameExperience;

import core.CampaignEngine;
import dbManager.ConnectionHandler;
import dbManager.DatabaseException;
import remoteData.dataObjects.User;
import remoteData.dataObjects.UserTable;
import sound.SoundPlayer;

import java.sql.Connection;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2016-02-22
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public class FirstGameAnalyser {

    private static final String startDate = "2015-0-01";

    public static void main(String[] args){

        System.out.println("****************************************************\n*  Executing FirstGame Analysis");


        System.out.print("Start Run?\n>");
        CampaignEngine.waitReturn();

        FirstGameAnalyser analyser = new FirstGameAnalyser();
        Connection remoteConnection    = ConnectionHandler.getConnection(ConnectionHandler.Location.remote);

        analyser.handleBatch(remoteConnection, startDate);
    }


    private int handleBatch(Connection connection, String startDate){

        UserTable allPlayers = new UserTable();
        loadWithRetry(connection, allPlayers, startDate, 0);
        User user = allPlayers.getNext();

        int userCount = 0;

        if(user == null)
            return -1;

        while(user != null ){


            System.out.println(" ----------------------------------------------------------\n  " + userCount + "- Evaluating User "+ user.toString());

            user = allPlayers.getNext();
        }

        return userCount;
    }

    private void loadWithRetry(Connection connection, UserTable allPlayers, String startDate, int userCount) {

        boolean retry = false;

        do{

            try {
                allPlayers.load(connection, " and players.created >= '"+ startDate+"' and players.uninstall=0", "ASC", 400000, userCount);      // Restriction for testing

            } catch (DatabaseException e) {

                SoundPlayer.playSound(SoundPlayer.FailBeep);
                System.out.println("Error with the database. Retry?\n>");

                retry = true;

            }

        }while(retry);
    }



}
