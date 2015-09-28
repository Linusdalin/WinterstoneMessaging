package output;

import email.EmailInterface;
import email.ReleaseEmail;
import remoteData.dataObjects.User;

import java.net.URLEncoder;

/**************************************************************
 *
 *          Sending an email
 *
 *
 *          TODO: Handle
 *
 *          Trying to POST: http://mailer.slot-america.com/sendCampaignMail?playerId=705698076219835&templateName=campaignMailTemplate&subject=there is more fun awaiting you&textVersion=You+have+199680+coins+left+on+your+account.There+are+some+fabulous+new+games+you+can+try+out+with+it.&htmlVersion=%3Cp%3EHello+there%21+Did+you+have+%3Cb%3E199680%3C%2Fb%3E+coins+left+on+your+account%3F+It+would+be+a+shame+to+let+them+go+to+waste%2C+right%3F%3C%2Fp%3E%3Cp%3E+There+are+some+new+and+fabulous+new+games+you+can+try+out+with+it%21+Like+%3Ca+href%3D%22https%3A%2F%2Fapps.facebook.com%2FslotAmerica%2F%3Fgame%3Dwild_cherries%26promocode%3DcoinsLeftEmail-1%22%3EWild+Cherries%3C%2Fa%3E.+Welcome+back+to+test+it+out+%3A-%29+%3C%2Fp%3E
    -> Got Response: NO_MAIL_ADDRESS
 Insert with: insert into exposure values ('705698076219835', 'Coins Left', 1, '2015-09-21 20:33:50.757', 'CoinsLeft-1', 'EMAIL')
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

        String actualUser = recipient;

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
