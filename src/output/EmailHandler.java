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
    private String title = null;
    private String url = null;
    private String game = "";

    private String messageTemplate;
    private User recipient;
    public static final String MESSAGE_TEMPLATE = "campaignMailTemplate";
    public static final String GAME_TEMPLATE = "messageMailTemplate";

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

        RequestHandler requestHandler = new RequestHandler(mailService);

        String imageLinkURL = "https://apps.facebook.com/slotamerica/?ref=email&promoCode=email&game=" + game;

        imageLinkURL = "http://aftonbladet.se";

        String response = requestHandler.executePost(
                        "playerId=" + actualUser +
                        "&templateName=" + messageTemplate +
                        "&subject=" + subject +
                        (title != null ? "&title=" + URLEncoder.encode(title) : "") +
                        (url != null ? "&imageURL=" + URLEncoder.encode( url ) + "&imageLinkURL="+URLEncoder.encode(imageLinkURL)  : "") +
                        "&textVersion=" + URLEncoder.encode(plain) +
                        "&htmlVersion="+ URLEncoder.encode(message));

        if(response != null){
            System.out.println("   -> Got Response: " + response);
            return true;
        }
        else{

            System.out.println("   -> No Response");
            return false;
        }

    }

    public EmailHandler withTemplate(String messageTemplate) {

        this.messageTemplate = messageTemplate;
        return this;
    }

    public EmailHandler withImageURL(String url, String game) {

        this.url = url;
        this.game = game;
        return this;
    }

    public EmailHandler withTitle(String title) {

        this.title = title;
        return this;
    }

}
