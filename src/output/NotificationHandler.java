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

    private int cap = 1;               // max notifications to send out. Default is 1
    private String message = null;
    private String[] recipientList;
    private String ref = "";
    private String promoCode = "";
    private String reward = null;
    private String game = null;
    private final String dummyOverride;

    public NotificationHandler(String dummyOverride){

        this.dummyOverride = dummyOverride;
    }

    public NotificationHandler withCap(int max){

        this.cap = max;
        return this;
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

    public int send() {

        if(message == null){
            System.out.println("No message given");
            return 0;

        }

        System.out.println("Preparing to send message \""+ message+"\"");

        if(recipientList == null){
            System.out.println("No recipients given");
            return 0;
        }

        if(recipientList.length > 1)
            System.out.println("  -> to " + recipientList.length + " recipients.");

        int count = 0;
        int successCount = 0;

        for (String recipient : recipientList) {

            if(dummyOverride != null){

                // Use dummy override to send out ONE controlled message for the first
                recipient = dummyOverride;
                if(cap != 0)
                     cap = 1;

            }
            //recipient = "627716024"; //TODO: Remove this when tested to actually send
            //recipient = "105390519812878";

            if(count < cap){

                //System.out.println("Sending to: " + recipient);
                RequestHandler requestHandler = new RequestHandler("https://graph.facebook.com/v2.3/" + recipient + "/notifications");

                String response = requestHandler.executePost("access_token=" + accessToken + "&href=?promoCode="+ this.promoCode+
                        (reward != null ? "%26reward="+ reward : "")+
                        (game != null ? "%26game="+ game : "")+
                        "&template="+ message+"&ref="+this.ref);
                //String response = requestHandler.executePost("");

                if(response != null){
                    System.out.println("   -> Got Response: " + response);
                    successCount++;
                }
                else{

                    System.out.println("   -> No Response");

                }

                count++;

            }
            else{

                System.out.println("Capped at " + cap + " messages. Ignoring " + (recipientList.length - cap) + " recipients");
                break;

            }

        }



        return successCount;
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

        return withRecipients(new String[] { facebookId });
    }

    private NotificationHandler withRecipients(String[] recipientList) {

        this.recipientList = recipientList;
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
