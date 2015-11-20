package email;

import net.sf.json.JSONObject;

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

    public NotificationEmail(JSONObject json) {

        super(json.getString("subject"), json.getString("body"), json.getString("plainText"), Template);

    }


}
