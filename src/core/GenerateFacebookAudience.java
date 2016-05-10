package core;

import campaigns.CampaignRepository;
import dbManager.ConnectionHandler;
import facebook.FacebookAudience;
import net.sf.json.JSONArray;

import java.sql.Connection;

/****************************************************************
 *
 *          Generate a new facebook audience from failed communication.
 *
 *          argument: number of hours back to look. for failed communication
 *
 */



public class GenerateFacebookAudience {


    private final static boolean SQL = false;

    public static void main(String[] args){

        JSONArray campaigns = null;

        int hours = Integer.valueOf(args[0]);

        if(args.length > 1){

            campaigns = new JSONArray(args[1]);
        }

        Connection connection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);

        System.out.println("Get audience for the last "+ hours+" hours");

        FacebookAudience newFacebookAudience = new FacebookAudience();
        newFacebookAudience.load(connection, hours);
        System.out.println(newFacebookAudience.toString(CampaignRepository.audienceCampaigns, SQL));


    }

}
