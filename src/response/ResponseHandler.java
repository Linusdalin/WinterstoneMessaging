package response;

import action.ActionType;
import campaigns.AbstractCampaign;
import campaigns.CampaignInterface;
import campaigns.CampaignRepository;
import localData.*;
import remoteData.dataObjects.GameSession;

import java.sql.*;

/*************************************************************************'
 *
 *              Handle and calculate response for a user
 *
 *
 */
public class ResponseHandler {


    private String userId;
    private Connection connection;
    private CachedUser cachedUser = null;

    public ResponseHandler(String userId, Connection connection){

        this.userId = userId;
        this.connection = connection;
    }


    /***********************************************************************************************
     *
     *
     *
     *
     *
     *
     * @param session
     * @param connection
     */


    public void storeResponse(GameSession session, Connection connection) {

        String user = session.facebookId;
        String campaign = getCampaign(session.promocode);

        if(campaign == null){

            if(!session.promocode.equals(""))
                System.out.println(" !'" + session.promocode + "' is not a notification feedback. No response store");
            return;

        }

        int messageId = getMessageId(session.promocode);

        ResponseTable responseTable = new ResponseTable("and user = '"+ user+"' and campaign = '"+ campaign+"' and messageId = " + messageId, 1, connection);
        responseTable.load(connection);
        Response response = responseTable.getNext();

        if(response == null){

            System.out.println(" !Storing a new response");
            Response newResp = new Response(session.facebookId, campaign, messageId, 1, session.timeStamp);
            newResp.store(connection);
            return;
        }
        else{

            System.out.println(" !Found response "+ response+" for user" + session.facebookId + " update!");

            if(AbstractCampaign.isDaysBefore(response.lastUpdate, session.timeStamp, 0)){

                // Same day. Ignore this. The user is responding to the same message
                System.out.println(" !Duplicate response. Ignoring!");
                return;

            }
            else{

                // Increase the count with 1 and update the last update timestamp
                response.updateCount(connection);

            }

        }

    }

    private String getCampaign(String promoCode) {

        CampaignRepository repository = new CampaignRepository();
        for (CampaignInterface campaign : repository.getActiveCampaigns()) {

            if(promoCode.startsWith(campaign.getTag()))
                return campaign.getTag();
        }
        return null;
    }

    private int getMessageId(String promoCode) {

        try{

            return new Integer(promoCode.substring(promoCode.indexOf("-")+1));

        }catch(Exception e){

            e.printStackTrace();
            System.out.println("Fail to get message id from promo code: " + promoCode );
            throw new RuntimeException();
        }

    }

    public boolean permanentlyFail(ActionType type) {


        if(cachedUser == null){

            CachedUserTable table = new CachedUserTable("and facebookId = '" + userId + "'", 1);
            table.load(connection);
            cachedUser = table.getNext();

        }

        if(cachedUser == null)
            return false;

        switch (type) {

            case NOTIFICATION:
                return cachedUser.failNotification > 0;
            case EMAIL:
                return cachedUser.failMail > 0;
            case IN_GAME:
            case MANUAL_ACTION:
            case COIN_ACTION:
        }

        return false;

    }

    /********************************************************************
     *
     *          This is a simplification.
     *          //TODO: Improvement: Look for exposures AFTER the last response
     *
     *
     * @return
     */


    public ResponseStat getOverallResponse() {

        int responses = getResponses(connection);
        int exposures = getExposures(connection);
        return new ResponseStat(exposures, responses);

    }

    /*********************************************************************************
     *
     *          Looking at exposures 150 days back. After that they "expire" and
     *          we will start trying to send messages one by one to them.
     *
     *
     * @param connection             -
     * @return
     */


    private int getExposures(Connection connection) {

        ExposureTable table = new ExposureTable(connection);
        int responses = table.getUserExposure(userId, 150);  // Look 150 days back for exposures
        return responses;

    }

    private int getResponses(Connection connection) {

        ResponseTable table = new ResponseTable(connection);
        int responses = table.getResponses(userId);
        return responses;

    }
}
