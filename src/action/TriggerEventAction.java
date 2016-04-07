package action;

import campaigns.CampaignState;
import events.EventInterface;
import net.sf.json.JSONObject;
import output.DeliveryException;
import output.TriggerEventHandler;
import remoteData.dataObjects.User;

import java.sql.Connection;
import java.sql.Timestamp;

/*******************************************************************
 *
 *          Trigger an event
 *
 *          NOTE:   This provides no information for the user.
 *                  It should be combined with a push message either mobile or facebook
 */

public class TriggerEventAction extends Action implements ActionInterface{

    private static final String message = "Trigger event";

    private EventInterface event;
    private int duration;       // hours


    public TriggerEventAction(EventInterface event, int hours, User user, Timestamp timeStamp, int significance, String campaignName, int messageId, CampaignState state, double responseFactor){

        this(0, event, hours, new ActionParameter(user.name, user.facebookId, user.email), timeStamp, significance, campaignName, messageId, state, responseFactor);
    }


    public TriggerEventAction(int id, EventInterface event, int hours, ActionParameter parameter, Timestamp timeStamp, int significance, String campaignName, int messageId, CampaignState state, double responseFactor){

        super(id, ActionType.TRIGGER_EVENT, parameter, timeStamp, message, significance, campaignName, messageId, state, responseFactor );
        this.event = event;
        this.duration = hours;
    }

    /**************************************************************
     *
     *              Execute the action
     *
     *
     * @param dryRun                - do not send (just testing)
     * @param testUser              - override user with dummy
     * @param executionTime         - time to store for the execution
     * @param localConnection       - connection to the crmDatabase to store xposure and outcomes
     * @param count
     *@param size @return                      - the response from executing action
     */

    public ActionResponse execute(boolean dryRun, String testUser, Timestamp executionTime, Connection localConnection, int count, int size)  {

        if(!isLive()){

            System.out.println("--------------------------------------------------------");
            System.out.println("%% Skipping (reason: "+ state.name()+") " + type.name() + " for player " + actionParameter.facebookId + "("+message+ ": " + event.getName() + ")" );
            return new ActionResponse(ActionResponseStatus.IGNORED,   "No Message sent - (reason: "+ state.name()+") " );

        }

        System.out.println("--------------------------------------------------------");
        System.out.println(" - Perform " + type.name() + " for player " + actionParameter.facebookId);

        /*

        GiveAwayHandler handler = new GiveAwayHandler(testUser)
                .toRecipient(actionParameter.facebookId)
                .withAmount(amount);
          */

        Timestamp endTime = new Timestamp(executionTime.getTime() + duration * 3600 * 1000);

        TriggerEventHandler handler = new TriggerEventHandler(testUser)
                .toRecipient(actionParameter.facebookId)
                .targetingEvent(event)
                .endTime(endTime)
                .useStage(useStage);


        try {

            if(!dryRun){

                if(handler.send()){
                    return new ActionResponse(ActionResponseStatus.OK,   "Personal Event Trigger Sent");
                }
                else
                    return new ActionResponse(ActionResponseStatus.FAILED,   "Personal Event Trigger failed");

            }
            else{
                System.out.println("  %%%Dryrun: Ignoring triggering event "+ event.getName() + "for user "+ actionParameter.name );
                return new ActionResponse(ActionResponseStatus.IGNORED,   "No personal event triggered - dry run");
            }

        } catch (DeliveryException e) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new ActionResponse(ActionResponseStatus.FAILED,   "Personal Event Trigger failed");
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
                .put("eventId", event.getName())
                .put("duration", duration);

        super.store(connection, data);

    }



}
