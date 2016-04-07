package output;

import events.EventInterface;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Timestamp;

/**************************************************************
 *
 *          Calling the support service to give awa
 *
 */


public class TriggerEventHandler {

    //private static final String coinService = "https://sa-cluster-prod.slot-america.com/addCoins";

    private static final String eventServiceStage = "http://slotamerica:fruitclub@dev.slot-america.com:3302/api/player-events/";
    //private static final String eventService      = "https://sa-cluster-prod.slot-america.com/api/player-events/";
    private static final String eventService      = "https://slotamerica:fruitclub@data-warehouse.slot-america.com/api/player-events/";


    private String overrideUserId = null;
    private EventInterface event = null;
    private String recipient;
    private boolean useStage = false;
    private Timestamp endTime = null;

    public TriggerEventHandler(String override) {

        this.overrideUserId = override;
    }


    public TriggerEventHandler toRecipient(String userId) {

        this.recipient = userId;
        return this;
    }

    public TriggerEventHandler targetingEvent(EventInterface event) {

        this.event = event;
        return this;
    }

    public TriggerEventHandler endTime(Timestamp endTime) {

        this.endTime = endTime;
        return this;
    }


    /***********************************************************
     *
     *      Sending message to all players in the send list
     *
     *
     * @return  - success or not
     */

    public boolean send() throws DeliveryException{

        if( event == null){
            System.out.println(" !! No event given");
            return false;

        }

        if( endTime == null){
            System.out.println(" !! No end time given");
            return false;

        }

        System.out.println(" - Preparing to enable event " + event.getName());

        if(recipient == null){
            System.out.println(" !! No recipient given");
            return false;
        }

        if(overrideUserId != null){

            // Use dummy override to send out ONE controlled message for the first
            recipient = overrideUserId;

        }

        String service = eventService;

        if(useStage)
            service = eventServiceStage;

        // Merging recipient to the URL

        RequestHandler requestHandler = new RequestHandler(service + recipient)
                .withBasicAuth("5b09eaa11e4bcd80800200c", "X");

        try{

            String response = requestHandler.executeGet( "" );
            System.out.println("Got the following active personal events:" + response);
            JSONArray active;

            if(!response.startsWith("[")){

                active = new JSONArray();
            }else{

                active = new JSONArray(response);

                if(active.toString().indexOf(event.getId()) >= 0){

                    System.out.println("The player already has the event active...");
                    return false;
                }
            }

            JSONObject newEvent = new JSONObject()
                    .put("eventId", event.getId())
                    .put("endTime", endTime.getTime());

            active.put(newEvent);

            response = requestHandler.executePut( active.toString(), "application/json" );


            if(response != null){
                System.out.println("   -> Got Response: " + response);
                return  (response.indexOf("OK") >= 0);

            }
            else{

                System.out.println("   -> No Response");
                return false;
            }

        }catch(DeliveryException e){

            System.out.println("   -> Got response " + e.getHttpCode());
            return false;
        }



    }

    public TriggerEventHandler useStage(boolean useStage) {

        this.useStage = useStage;
        return this;
    }
}
