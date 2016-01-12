package core;

import action.ActionInterface;
import action.GiveCoinAction;
import campaigns.CampaignInterface;
import localData.ExposureTable;
import localData.ResponseTable;
import remoteData.dataObjects.User;
import response.ResponseHandler;
import response.ResponseStat;

import java.sql.Connection;

/**************************************************************************
 *
 *
 *              Calculate the timing for sending a message
 *
 */
public class TimeAnalyser {


    private PlayerInfo playerInfo;
    private Connection connection;
    private static final int Personal_CoolOff = 7;
    private static final double RESPONSE_FACTOR = 2.0;      // This will make a 60 x 65 above 100

    public TimeAnalyser(PlayerInfo playerInfo, Connection connection) {

        this.playerInfo = playerInfo;
        this.connection = connection;
    }

    /**************************************************************************
     *
     *          Calculate eligibility for receiving a message
     *
     *          //TODO: Add a back-off when overall response goes down
     *
     *
     * @param campaignExposures     - campaign exposures
     * @param handler               - handler for all responses to lookup response frequency
     * @return                      - percentage value as threshold
     */


    public int eligibilityForCommunication(ExposureTable campaignExposures, ResponseHandler handler, ActionInterface action){

        ResponseStat response = handler.getOverallResponse();
        double responseFactor = action.getResponseFactor();
        System.out.println("      Got response " + response.toString() + " for user.");

        int exposures = campaignExposures.getUserExposure(playerInfo.getUser().facebookId, Personal_CoolOff);

        int limit = 1;

        if(responseFactor > 1.0){
            System.out.println("  The user has responded to the campaign previously ("+ responseFactor+") so we increase the limit ");
            limit++;
        }

        // If the action is give coin, we add one to the limit to make sure we execute it
        if(action.getNext() != null && action.getNext() instanceof GiveCoinAction)
            limit += 2;

        // Add one for an action with reward
        if(action.getReward() != null )
            limit += 1;

        if(playerInfo.getUser().payments > 0)
            limit++;

        if(playerInfo.getUser().payments > 3)
            limit++;

        System.out.println("   (Exposure limit is " + limit + ")");

        if(exposures > limit){

            System.out.println("Already "+ exposures+" messages this week. (limit= "+ limit+") Not sending any more");
            return 0;
        }

        if(exposures == limit ){

            System.out.println("Already "+ exposures+" message this week. Only high priority messages");
            return 65;
        }

        return 100;
    }


    public boolean hasResponded(Connection connection, User user, String campaign){

        ResponseTable responseTable = new ResponseTable(connection);
        int responses = responseTable.getResponses(user.facebookId, campaign);

        return (responses > 0);
    }


    /*************************************************************************************'''''
     *
     *          Calculate the response factor for a specific campaign based on previous response
     *
     *
     *          //TODO: Improvement: This is pretty simple. Could be improved upon
     *
     * @param user                          - the user
     * @param campaign                      - the specific campaign
     * @return                              - a rate
     */

    public double getResponseAdjustment(User user, CampaignInterface campaign) {

        if(hasResponded(connection, user, campaign.getTag())){

            System.out.println(" !! Setting response factor "+ RESPONSE_FACTOR+" for user " + user.name);

            return RESPONSE_FACTOR;
        }

        return 1.0;
    }



    public int adjustForResponse(int eligibility, ActionInterface action) {

        return (int)(eligibility * action.getResponseFactor());

    }
}
