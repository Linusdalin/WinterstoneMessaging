package email;

import net.sf.json.JSONObject;

/***********************************************************************
 *
 *              The representation of an email
 */

public abstract class AbstractEmail implements EmailInterface{

    private final String subject;
    private final String body;
    private final String plainText;
    private String template;

    public AbstractEmail(String subject, String html, String plainText, String template){

        this.subject = subject;
        this.body = html;
        this.plainText = plainText;
        this.template = template;
    }

    public String getPlainText(){

        return this.plainText;
    }


    public String getTemplate() {
        return template;
    }

    //TODO: Implement serialization of email

    @Override
    public JSONObject toJSON() {
        return new JSONObject();
    }

    public String getBody() {
        return body;
    }

    public String getSubject() {
        return subject;
    }
}
