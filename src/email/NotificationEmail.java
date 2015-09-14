package email;

/*********************************************************************************
 *
 *
 *
 */


public class NotificationEmail extends AbstractEmail implements EmailInterface {

    private static final String Template = "campaignMailTemplate";

    public NotificationEmail(String subject, String html, String plainText){

        super(subject, html, plainText, Template);

    }

}
