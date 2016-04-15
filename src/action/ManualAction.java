package action;

import campaigns.CampaignState;
import remoteData.dataObjects.User;

import java.sql.Connection;
import java.sql.Timestamp;

/*******************************************************************
 *
 *          Perform a manual action
 *
 *
 *          //TODO: There is no feedback for the manual action implemented
 */

public class ManualAction extends Action implements ActionInterface{


    /*******************************************************************************
     *
     *
     *              Create a manual action
     *
     * @param message                   - message for the operator
     * @param user                      - recepient
     * @param significance              - action significance
     * @param campaignName              - name(tag) of campaign for tracking
     * @param messageId                 - id for tracking
     * @param state                     - state of the campaign (to decide the final action)
     * @param responseFactor            - the modification to eligibility given the players history with this campaign
     */


    public ManualAction(String message, User user, Timestamp timeStamp,  int significance, String campaignName, int messageId, CampaignState state, double responseFactor){

        this(0, message, new ActionParameter(user.name, user.id, user.email), timeStamp, significance, campaignName, messageId, state, responseFactor);
    }


    public ManualAction(int id, String message, ActionParameter parameter, Timestamp timeStamp,  int significance, String campaignName, int messageId, CampaignState state, double responseFactor){

        super(id, ActionType.MANUAL_ACTION, parameter, timeStamp,  message, significance, campaignName, messageId, state, responseFactor );

    }

    /**************************************************************
     *
     *              Execute the action
     *
     *
     *
     *
     *
     * @param dryRun                - do not send (just testing)
     * @param testUser              - override user with dummy
     * @param executionTime         - time to store for the execution
     * @param localConnection       - connection to the crmDatabase to store xposure and outcomes
     * @param count
     *@param size @return                      - the response from executing action
     */

    public ActionResponse execute(boolean dryRun, String testUser, Timestamp executionTime, Connection localConnection, int count, int size) {

        if(!isLive()){

            System.out.println("--------------------------------------------------------");
            System.out.println("%% Skipping (reason: "+ state.name()+") " + type.name() + " for player " + actionParameter.facebookId + "("+message+")" );
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Message sent - (reason: "+ state.name()+") " );

        }

        System.out.println("--------------------------------------------------------");
        System.out.println(" - Perform " + type.name() + " for player " + actionParameter.facebookId);

        if(!dryRun){

            System.out.println("! PLEASE EXECUTE: " + message + " for user " + actionParameter.name + " ( " + actionParameter.facebookId + " ) ");
            return new ActionResponse(ActionResponseStatus.MANUAL,   "Awaiting");

        }else{


            System.out.println("  %%%Dryrun: Ignoring performing "+ message+" to user "+ actionParameter.facebookId);
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Message sent - dry run");

        }

    }



}
