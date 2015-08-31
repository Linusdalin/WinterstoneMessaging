package output;

import remoteData.dataObjects.User;

/**************************************************************
 *
 *          Sending an email
 *
 */


public class EmailHandler {

    private static final String mailService = "https://dev.null";   //TODO: Add correct message here


    private String overrideAddress = null;
    private String message = null;
    private User recipient;
    private int cap = 1;

    public EmailHandler(String override) {

        this.overrideAddress = override;
    }

    public EmailHandler withMessage(String message) {

        this.message = message;
        return this;
    }

    public EmailHandler withRecipient(User user) {

        this.recipient = user;
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

        System.out.println("Preparing to send email \""+ message+"\"");

        if(recipient == null){
            System.out.println("No recipient given");
            return 0;
        }

        //recipient = "627716024"; //TODO: Remove this when tested to actually send
        //recipient = "105390519812878";


        //System.out.println("Sending to: " + recipient);
        RequestHandler requestHandler = new RequestHandler(mailService);

        String response = requestHandler.executePost(
                        "message=" + message +
                        "&name=" + recipient.name +
                        "&email="+ recipient.email);
        //String response = requestHandler.executePost("");

        if(response != null){
            System.out.println("   -> Got Response: " + response);
            return 1;
        }
        else{

            System.out.println("   -> No Response");
            return 0;
        }

    }
}
