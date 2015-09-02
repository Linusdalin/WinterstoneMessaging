package action;

import campaigns.CampaignState;
import localData.Exposure;
import output.EmailHandler;import remoteData.dataObjects.User;

import java.sql.Connection;
import java.sql.Timestamp;

/*******************************************************************
 *
 *          Email
 *
 *          An instance of the abstract Action resulting in a notification sent to the user
 */

public class EmailAction extends Action implements ActionInterface{

    public static final boolean inUse = false;

    /******************************************************
     *
     *          Create a new email action
     *
     *
     * @param message                 - the message (html)
     * @param user                    - recipient
     * @param significance
     * @param campaignName            - campaign for followup in exposure tracking
     * @param messageId               - the id of the message (within the campaign)
     * @param state                   - state of campaign (to send or ignore)
     */


    public EmailAction(String message, User user, int significance, String campaignName, int messageId, CampaignState state){

        super(ActionType.EMAIL, user, message, significance, campaignName, messageId, state );
        setPromoCode(createPromoCode(campaignName, messageId));

    }

    /**************************************************************
     *
     *              Execute the action
     *
     *
     *
     *
     * @param dryRun                - do not send (just testing)
     * @param testUser              - override user with dummy
     * @param executionTime         - time to store for the execution
     * @param localConnection       - connection to the crmDatabase to store xposure and outcomes
     * @return                      - the response from executing action
     */

    public ActionResponse execute(boolean dryRun, String testUser, Timestamp executionTime, Connection localConnection) {

        if(!inUse){

            return new ActionResponse(ActionResponseStatus.IGNORED,   "No email sent - (not in use)");

        }

        if(!isLive()){

            System.out.println("--------------------------------------------------------");
            System.out.println("%% Skipping (reason: "+ state.name()+") " + type.name() + " for player " + user);
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No email sent - (reason: "+ state.name()+") " );

        }

        System.out.println("--------------------------------------------------------");
        System.out.println("! Executing " + type.name() + " for player " + user);

        EmailHandler handler = new EmailHandler(testUser)
                .withMessage( message )
                .withRecipient( user );

        // Now check if we are to send off the message or just log it (dry run)

        int successCount = 0;

        if(!dryRun){
            successCount =  handler.send();
            if(successCount > 0){
                noteSuccessFulExposure( (testUser == null ? user.facebookId : testUser ), executionTime, localConnection );
                return new ActionResponse(ActionResponseStatus.OK,   "Message sent");
            }
            else
                return new ActionResponse(ActionResponseStatus.FAILED,   "Message delivery failed");

        }
        else{
            System.out.println("  %%%Dryrun: Ignoring sending email to user "+ getUser().name + "(" + getUser().email + ") " + "-\""+ message+"\" Promocode:" + promoCode);
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Message sent - dry run");
        }


    }


    private void noteSuccessFulExposure(String actualUser, Timestamp executionTime, Connection localConnection) {

        Exposure exposure = new Exposure(actualUser, getCampaign(), getMessageId(), executionTime , promoCode);
        exposure.store(localConnection);
    }



}
