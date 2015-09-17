package core;

import campaigns.CampaignInterface;
import localData.ExposureTable;
import localData.ResponseTable;
import remoteData.dataObjects.User;
import response.ResponseStat;
import response.ResponseHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**************************************************************************
 *
 *
 *              Calculate the timing for sending a message
 *
 */
public class TimeAnalyser {


    private PlayerInfo playerInfo;
    private final int Personal_CoolOff = 7;

    public TimeAnalyser(PlayerInfo playerInfo) {

        this.playerInfo = playerInfo;
    }

    public int eligibilityForCommunication(ExposureTable campaignExposures, ResponseHandler handler, Connection localConnection, Connection remoteConnection){

        ResponseStat response = handler.getOverallResponseRate(localConnection, remoteConnection);
        System.out.println("      Got response " + response.toString() + " for user.");

        int exposures = campaignExposures.getUserExposure(playerInfo.getUser(), Personal_CoolOff);

        int limit = 1;

        if(exposures > 1){

            System.out.println("Already "+ exposures+" messages this week. Not sending any more");
            return 0;
        }

        if(exposures == 1 ){

            System.out.println("Already "+ exposures+" message this week. Only high priority messages");
            return 65;
        }

        return 100;
    }


    private boolean hasResponded(Connection connection, User user, CampaignInterface campaign){

        ResponseTable responseTable = new ResponseTable(connection);
        int responses = responseTable.getResponses(user, campaign);

        return (responses > 0);
    }

}
