package output;

import remoteData.dataObjects.User;

import java.net.URLEncoder;

/**************************************************************
 *
 *          Sending an email
 *
 */


public class EmailHandler {

    private static final String mailService = "http://mailer.slot-america.com/sendCampaignMail";


    private String overrideUser = null;
    private String message = null;
    private String subject = null;
    private String plain = null;
    private User recipient;

    public EmailHandler( ){

        this(null);
    }


    public EmailHandler(String override) {

        this.overrideUser = override;
    }

    public EmailHandler withMessage(String message) {

        this.message = message;
        return this;
    }

    public EmailHandler withAlt(String message) {

        this.plain = message;
        return this;
    }

    public EmailHandler withSubject(String subject) {

        this.subject = subject;
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

    public boolean send() {

        if(message == null){
            System.out.println("No message given");
            return false;

        }

        if(plain == null){
            System.out.println("No plain alt given");
            return false;

        }

        if(subject == null){
            System.out.println("No subject given");
            return false;

        }

        System.out.println("Preparing to send email \""+ message+"\"");

        if(recipient == null){
            System.out.println("No recipient given");
            return false;
        }

        String actualUser = recipient.facebookId;

        if(overrideUser != null){

            actualUser = overrideUser;
        }

        //recipient = "627716024"; //TODO: Remove this when tested to actually send
        //recipient = "105390519812878";


        // playerId=836390979715760&subject=test
        //      &textVersion=Hello+World
        //      &htmlVersion=%3Cp+style%3D%22Margin-top%3A+0%3Bcolor%3A+%23333333%3Bfont-family%3A+sans-serif%3Bfont-size%3A+16px%3Bline-height%3A+24px%3BMargin-bottom%3A+24px%22%3EHello%20World%3C%2Fp%3E

        //System.out.println("Sending to: " + recipient);
        RequestHandler requestHandler = new RequestHandler(mailService);

        String response = requestHandler.executePost(
                        "playerId=" + actualUser +
                        "&subject=" + subject +
                        "&textVersion=" + URLEncoder.encode(plain) +
                        "&htmlVersion="+ URLEncoder.encode(message));
        //String response = requestHandler.executePost("");

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
