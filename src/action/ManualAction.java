package action;

import campaigns.CampaignState;
import remoteData.dataObjects.User;

import java.sql.Connection;
import java.sql.Timestamp;

/*******************************************************************
 *
 *          Perform a manual action
 *
 */

public class ManualAction extends Action implements ActionInterface{

    public ManualAction(String message, User user, int significance, String campaignName, int messageId, CampaignState state){

        super(ActionType.MANUAL_ACTION, user, message, significance, campaignName, messageId, state );

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

        if(!isLive()){

            System.out.println("--------------------------------------------------------");
            System.out.println("%% Skipping (reason: "+ state.name()+") " + type.name() + " for player " + user.facebookId + "("+message+")" );
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Message sent - (reason: "+ state.name()+") " );

        }

        System.out.println("--------------------------------------------------------");
        System.out.println("! Perform " + type.name() + " for player " + user.facebookId);

        if(!dryRun){

            System.out.println("! PLEASE EXECUTE: " + message + " for user " + getUser().name + " ( " + getUser().facebookId + " ) ");
            return new ActionResponse(ActionResponseStatus.MANUAL,   "Awaiting");

        }else{


            System.out.println("  %%%Dryrun: Ignoring performing "+ message+" to user "+ user.facebookId);
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Message sent - dry run");

        }

    }



}
