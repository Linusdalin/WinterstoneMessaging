package output;


/******************************************************************************
 *
 * Notification test tool for sending notifications to specific players.
 *
 * This is mostly for testing out response rates for notifications
 */


public class NotificationHandler {


    private static final String appId = "765552976795552";
    private static final String appSecret = "c3a005258d928d6357606d046a2361c5";
    private static final String accessToken = appId + "|" + appSecret;

    private String message = null;
    private String recipient = null;
    private String ref = "";
    private String promoCode = "";
    private String reward = null;
    private String game = null;
    private final String dummyOverride;

    public NotificationHandler(String dummyOverride){

        this.dummyOverride = dummyOverride;
    }


    public NotificationHandler withMessage(String message){

        if(message == null){
            System.out.println("No message given");
            return this;

        }

        if(message.length() > 180){
            System.out.println("message too long. Only 180 chars allowed");
            return this;

        }


        this.message = message;
        return this;
    }




    /***********************************************************
     *
     *      Sending message to all players in the send list
     *
     *
     * @return  - number of successfully sent messages
     */

    public boolean send() throws DeliveryException {

        if(message == null){
            System.out.println("No message given");
            return false;

        }

        System.out.println("Preparing to send message \""+ message+"\"");

        if(recipient == null){
            System.out.println("No recipients given");
            return false;
        }



        if(dummyOverride != null){

            // Use dummy override to send out ONE controlled message for the first
            recipient = dummyOverride;

        }

        //recipient = "627716024"; //TODO: Remove this when tested to actually send
        //recipient = "105390519812878";


        RequestHandler requestHandler = new RequestHandler("https://graph.facebook.com/v2.3/" + recipient + "/notifications");


        String response = requestHandler.executePost("access_token=" + accessToken + "&href=?promoCode="+ this.promoCode+
                (reward != null ? "%26reward="+ reward : "")+
                (game != null ? "%26game="+ game : "")+
                "&template="+ message+"&ref="+this.ref);

        if(response != null){
            System.out.println("   -> Got Response: " + response);
            return true;
        }
        else{

            System.out.println("   -> No Response");
            return false;
        }

    }


    public NotificationHandler withRef(String ref) {

        this.ref = ref;
        return this;
    }

    public NotificationHandler withPromoCode(String code) {

        this.promoCode = code;
        return this;
    }

    public NotificationHandler withRecipient(String facebookId) {

        this.recipient = facebookId;
        return this;
    }


    public NotificationHandler withReward(String reward) {
        this.reward = reward;
        return this;
    }

    public NotificationHandler withGame(String game) {
        this.game = game;
        return this;
    }

}
