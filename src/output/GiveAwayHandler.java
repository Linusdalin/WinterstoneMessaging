package output;

import remoteData.dataObjects.User;

/**************************************************************
 *
 *          Sending an email
 *
 */


public class GiveAwayHandler {

    private static final String coinService = "https://dev.null";   //TODO: Add correct message here


    private String overrideUserId = null;
    private int amount = 0;
    private User recipient;

    public GiveAwayHandler(String override) {

        this.overrideUserId = override;
    }

    public GiveAwayHandler withAmount(int amount) {

        this.amount = amount;
        return this;
    }

    public GiveAwayHandler withRecipient(User user) {

        this.recipient = user;
        return this;
    }



    /***********************************************************
     *
     *      Sending message to all players in the send list
     *
     *
     * @return  - success or not
     */

    public boolean send() {

        if(amount == 0){
            System.out.println("No amount given");
            return false;

        }

        System.out.println("Preparing to give " + amount + " coins away");

        if(recipient == null){
            System.out.println("No recipient given");
            return false;
        }

        //recipient = "627716024"; //TODO: Remove this when tested to actually send
        //recipient = "105390519812878";


        //System.out.println("Sending to: " + recipient);
        RequestHandler requestHandler = new RequestHandler(coinService);

        String response = requestHandler.executePost(
                        "player=" + recipient.facebookId +
                        "&amount=" + amount);

        if(response != null){
            System.out.println("   -> Got Response: " + response);
            return true;
        }
        else{

            System.out.println("   -> No Response");
            return false;
        }

    }
}
