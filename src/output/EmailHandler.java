package output;

import email.EmailInterface;
import email.ReleaseEmail;
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
    private EmailInterface email = null;

    private User recipient;

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

    public EmailHandler toRecipient(User user) {

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

        if(email == null){
            System.out.println("No email");
            return false;

        }


        System.out.println("Preparing to send email \""+ email.getSubject()+"\"");

        if(recipient == null){
            System.out.println("No recipient given");
            return false;
        }

        String actualUser = recipient.facebookId;

        if(overrideUser != null){

            actualUser = overrideUser;
        }

        String title = null;
        String imageURL = null;
        String link = null;

        if(email instanceof ReleaseEmail){

            // Special configuration for release emails
            ReleaseEmail instance = (ReleaseEmail)email;

            title    = instance.getTitle();
            imageURL = instance.getImageURL();
            link     = instance.getLink();

        }


        //recipient = "627716024"; //TODO: Remove this when tested to actually send

        RequestHandler requestHandler = new RequestHandler(mailService);

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
            return true;
        }
        else{

            System.out.println("   -> No Response");
            return false;
        }

    }


}
