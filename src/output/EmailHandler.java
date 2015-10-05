package output;

import email.EmailInterface;
import email.ReleaseEmail;

import java.net.URLEncoder;

/**************************************************************
 *
 *          Sending an email
 *
 *
 *
 */


public class EmailHandler {

    private static final String mailService = "http://mailer.slot-america.com/sendCampaignMail";


    private String overrideUser = null;
    private EmailInterface email = null;

    private String recipient;

    public EmailHandler( ){

        this(null);
    }


    public EmailHandler(String override) {

        this.overrideUser = override;
    }

    public EmailHandler withEmail(EmailInterface email) {

        this.email = email;
        return this;
    }

    public EmailHandler toRecipient(String userId) {

        this.recipient = userId;
        return this;
    }

    /***********************************************************
     *
     *      Sending message to all players in the send list
     *
     *
     * @return  - number of successfully sent messages
     */

    public boolean send() throws DeliveryException{

        if(email == null){
            System.out.println("No email");
            return false;

        }


        System.out.println("Preparing to send email \""+ email.getSubject()+"\"");

        if(recipient == null){
            System.out.println("No recipient given");
            return false;
        }

        String actualUser = recipient;

        if(overrideUser != null){

            actualUser = overrideUser;
        }

        String title = null;
        String imageURL = null;
        String link = null;

        if(email instanceof ReleaseEmail){

            // Special configuration for release emails, This is not really in use, so we may remove it
            ReleaseEmail instance = (ReleaseEmail)email;

            title    = instance.getTitle();
            imageURL = instance.getImageURL();
            link     = instance.getLink();

        }

        RequestHandler requestHandler = new RequestHandler(mailService).withBasicAuth("5b09eaa11e4bcd80800200c", "X");

        String response = requestHandler.executePost(
                        "playerId=" + actualUser +
                        "&templateName=" + email.getTemplate() +
                        "&subject=" + email.getSubject() +
                        (title != null ? "&title=" + URLEncoder.encode(title) : "") +
                        (imageURL != null ? "&imageURL=" + URLEncoder.encode( imageURL ) + "&imageLinkURL="+URLEncoder.encode(link)  : "") +
                        "&textVersion=" + URLEncoder.encode(email.getPlainText()) +
                        "&htmlVersion="+ URLEncoder.encode(email.getBody()));

        if(response != null){

            System.out.println("   -> Got Response: " + response);
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

    private boolean evaluateOKResponse(String response) {

        if(response.equals("NO_MAIL_ADDRESS") || response.equals("UNSUBSCRIBED"))
            return false;

        return true;
    }



}
