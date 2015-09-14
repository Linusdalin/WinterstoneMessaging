package response;

import campaigns.CampaignInterface;
import campaigns.CampaignRepository;
import localData.Response;
import localData.ResponseTable;
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

    public ResponseHandler(String userId){

        this.userId = userId;
    }

    public ResponseStat getOverallResponseRate(Connection localConnection, Connection remoteConnection) {


        return new ResponseStat(0, 0);

    }

    //TODO: Get campaign and sessionid from session
    //TODO: Check that there is a real response
    //TODO: Filter on first in the day. All game sessions are tagged with the promoCode

    public void storeResponse(GameSession session, Connection connection) {

        String user = session.facebookId;
        String campaign = getCampaign(session.promocode);

        if(campaign == null){

            if(!session.promocode.equals(""))
                System.out.println(" !'" + session.promocode + "' is not a notification feedback. No response store");
            return;

        }

        int messageId = getMessageId(session.promocode);

        ResponseTable responseTable = new ResponseTable("and user = '"+ user+"' and campaign = '"+ campaign+"' and messageId = " + messageId + " and date(lastUpdate) = date('"+ session.timeStamp+"')", 1);
        responseTable.load(connection);
        Response response = responseTable.getNext();

        if(response == null){

            System.out.println(" !Storing a new response");
            Response newResp = new Response(session.facebookId, campaign, messageId, 1, session.timeStamp);
            newResp.store(connection);
        }
        else{

            System.out.println(" !Found response for user" + session.facebookId + " update NOT IMPLEMENTED!");

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
        return new Integer(promoCode.substring(promoCode.indexOf("-")+1));
    }

}
