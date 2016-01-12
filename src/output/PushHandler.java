package output;

import java.net.URLEncoder;

/**************************************************************
 *
 *          Sending a push notification
 *
 *
 *          https://slotamerica:fruitclub@data-warehouse.slot-america.com/sendNotification?playerId=627716024&message=HelloWorld&category=game:royal_colors,reward':fe8f3144-cd54-45ab-8f69-68f33dd13a42
 *
 *
 *
 */


public class PushHandler {

    private static final String DefaultPushService = "https://slotamerica:fruitclub@data-warehouse.slot-america.com/sendNotification/";

    private static final int MAX_MESSAGE_SIZE = 180;           //TODO: Check this. What is the max size for a push message

    private String overrideUser = null;
    private String message = null;
    private String game = null;
    private String rewardId = null;
    private String recipient;

    private String pushService;

    public PushHandler( ){

        this(null);
        pushService = DefaultPushService;
    }



    public PushHandler(String override) {

        this.overrideUser = override;
        pushService = DefaultPushService;
    }

    public PushHandler withMessage(String message){

        if(message == null){
            System.out.println("No message given");
            return this;

        }


        if(message.length() > MAX_MESSAGE_SIZE){
            System.out.println("message too long. Only "+MAX_MESSAGE_SIZE+" chars allowed");
            return this;

        }


        this.message = message;
        return this;
    }

    public PushHandler toRecipient(String userId) {

        this.recipient = userId;
        return this;
    }

    public PushHandler withReward(String code) {

        this.rewardId = code;
        return this;
    }

    public PushHandler withGame(String game) {

        this.game = game;
        return this;
    }

    /***********************************************************
     *
     *          Pushing the message to the users mobile device
     *
     *
     * @return  - number of successfully sent messages
     */

    public boolean send() throws DeliveryException{

        if(message == null){
            System.out.println("No email");
            return false;

        }


        System.out.println("Preparing to send message \""+ message+"\"");

        if(recipient == null){
            System.out.println("No recipient given");
            return false;
        }

        String actualUser = recipient;

        if(overrideUser != null){

            actualUser = overrideUser;
        }

        String request = pushService + "?playerId="+ actualUser+"&message=" + URLEncoder.encode(message);
        String category = "";


        if(game != null)
            category += "game:" + game;


        if(rewardId != null){

            if(category.length() > 0)
                category +=",";

            category += "reward:" + rewardId;
        }

        if(category.length() > 0)
            request += "&category=" + category;


        RequestHandler requestHandler = new RequestHandler( request ).withBasicAuth("5b09eaa11e4bcd80800200c", "X");

        String response = requestHandler.executePost( "" );

        if(response != null){

            System.out.println("   -> Got Response: '" + response + "'");
            return evaluateOKResponse(response);

        }
        else{

            System.out.println("   -> No Response");
            return false;
        }

    }

    /**************************************************************''
     *
     *          Evaluate a response to see if is OK
     *
     * @param response         - test from the service
     * @return                 - did the response mean ok or fail
     *
     *
     *          NOTE: These messages may not be complete
     */

    private boolean evaluateOKResponse(String response) throws DeliveryException {

        if(response.indexOf("\"success\":false") >= 0)
            throw new DeliveryException(400);

        System.out.println(" --- This is ok!");
        return true;
    }

    /****************************************************************
     *
     *
     *          Override the URL to the push service. E.g. for use on stage
     *
     * @param url
     * @return
     */

    public PushHandler withAlternateService(String url){

        pushService = url;
        return this;
    }


}
