package output;

/**************************************************************
 *
 *          Calling the support service to give awa
 *
 */


public class GiveAwayHandler {

    private static final String coinService = "https://sa-cluster-prod.slot-america.com/addCoins";


    private String overrideUserId = null;
    private int amount = 0;
    private String recipient;

    public GiveAwayHandler(String override) {

        this.overrideUserId = override;
    }

    public GiveAwayHandler withAmount(int amount) {

        this.amount = amount;
        return this;
    }

    public GiveAwayHandler toRecipient(String userId) {

        this.recipient = userId;
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

        if(amount == 0){
            System.out.println("No amount given");
            return false;

        }

        System.out.println("Preparing to give " + amount + " coins away");

        if(recipient == null){
            System.out.println("No recipient given");
            return false;
        }

        if(overrideUserId != null){

            // Use dummy override to send out ONE controlled message for the first
            recipient = overrideUserId;

        }

        //System.out.println("Sending to: " + recipient);
        RequestHandler requestHandler = new RequestHandler(coinService);

        try{

            String response = requestHandler.executeGet(
                    "auth=c7849722a97707d96ceb356d2417a4bf" +
                    "&coins=" + amount+
                    "&playerId=" + recipient);

            if(response != null){
                System.out.println("   -> Got Response: " + response);
                return  (response.contains("ok"));

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
}
