package action;

import campaigns.CampaignState;
import email.EmailInterface;
import net.sf.json.JSONObject;
import output.DeliveryException;
import output.EmailHandler;
import remoteData.dataObjects.User;

import java.sql.Connection;
import java.sql.Timestamp;

/*******************************************************************
 *
 *          Email
 *
 *          An instance of the abstract Action resulting in a notification sent to the user
 */

public class EmailAction extends Action implements ActionInterface{

    public static final boolean inUse = true;
    private final EmailInterface email;

    /******************************************************
     *
     *          Create a new email action
     *
     *
     * @param email                 - the message (html)
     * @param user                      - recepient
     * @param significance              - action significance
     * @param campaignName              - name(tag) of campaign for tracking
     * @param messageId                 - id for tracking
     * @param state                     - state of the campaign (to decide the final action)
     * @param responseFactor            - the modification to eligibility given the players history with this campaign

     */

    public EmailAction(EmailInterface email, User user, Timestamp timeStamp, int significance, String campaignName, int messageId, CampaignState state, double responseFactor){

        this(0, email, new ActionParameter(user.name, user.id, user.email), timeStamp, significance, campaignName, messageId, state, responseFactor);

    }

    public EmailAction(int id, EmailInterface email, ActionParameter parameter, Timestamp timeStamp, int significance, String campaignName, int messageId, CampaignState state, double responseFactor){

        super(id, ActionType.EMAIL, parameter, timeStamp, email.getPlainText(), significance, campaignName, messageId, state, responseFactor );
        this.email = email;
        setPromoCode(createPromoCode(campaignName, messageId));

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
     * @param count                 - the number
     *@param size @return                      - the response from executing action
     */

    public ActionResponse execute(boolean dryRun, String testUser, Timestamp executionTime, Connection localConnection, int count, int size) {

        if(!inUse){

            return new ActionResponse(ActionResponseStatus.IGNORED,   "No email sent - (not in use)");

        }

        if(!isLive()){

            System.out.println("--------------------------------------------------------");
            System.out.println("%% Skipping (reason: "+ state.name()+") " + type.name() + " for player " + actionParameter.name);
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No email sent - (reason: "+ state.name()+") " );

        }

        System.out.println("--------------------------------------------------------");
        System.out.println("! Executing " + type.name() + "("+ count+"/"+size+") for player " + actionParameter.name);

        EmailHandler handler = new EmailHandler(testUser)
                .withEmail( email )
                .toRecipient( actionParameter.facebookId );

        // Now check if we are to send off the message or just log it (dry run)



        if(!dryRun){

            try{

                if(handler.send()){

                    noteSuccessFulExposure( (testUser == null ? actionParameter.facebookId : testUser ), ActionType.EMAIL, executionTime, localConnection );
                    pause(1);
                    return new ActionResponse(ActionResponseStatus.OK,   "Message sent");
                }
                else{
                    pause(1);
                    return new ActionResponse(ActionResponseStatus.FAILED,   "Message delivery failed");

                }


            }catch(DeliveryException e){

                noteFailedExposure( (testUser == null ? actionParameter.facebookId : testUser ), ActionType.EMAIL, executionTime, localConnection );
                return new ActionResponse(e.getStatus(),   "Message delivery failed");

            }

        }
        else{
            System.out.println("  %%%Dryrun: Ignoring sending email to user "+ actionParameter.name + "(" + actionParameter.email + ") " + "-\""+ message+"\" Promocode:" + promoCode);
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Message sent - dry run");
        }


    }



    /*************************************************************'
     *
     *      Appending specific data and storing
     *
     * @param connection    - database
     */

    public void store(Connection connection){

        JSONObject data = actionAsJSON()
                .put("email", email.toJSON());

        super.store(connection, data);

    }



}
