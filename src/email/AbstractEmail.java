package email;

import net.sf.json.JSONObject;
import remoteData.dataObjects.User;

import java.util.List;


/***********************************************************************
 *
 *              The representation of an email
 */

public abstract class AbstractEmail implements EmailInterface{

    private final String subject;
    private StringBuffer body = new StringBuffer();
    private final String plainText;
    private String template;

    public AbstractEmail(String subject, String html, String plainText, String template){

        this.subject = subject;
        this.body.append(html);
        this.plainText = plainText;
        this.template = template;
    }

    public String getPlainText(){

        return this.plainText;
    }


    public String getTemplate() {
        return template;
    }


    /*********************************************************
     *
     *          Clean up and replace any remaining box marker
     *
     *
     * @return
     */

    @Override
    public JSONObject toJSON() {

        return new JSONObject()
                .put("template", template)
                .put("subject", subject.replaceAll("'", ""))
                .put("body", body.toString().replaceAll("'", ""))
                .put("plainText", plainText.replaceAll("'", ""));
    }

    /**************************************************************
     *
     *          Adding content boxes to the email depending on the user
     *
     * @param user      - user for which to add user specific content
     * @param max       - max number of boxes to add
     */


    @Override
    public void addContentBoxes(User user, int max) {

        ContentBoxManager mgr = new ContentBoxManager(user);
        List<String> contentBoxes = mgr.getBoxes( max );
        StringBuffer boxedContent = new StringBuffer();

        for (String box : contentBoxes) {

            body.append( box );
        }


    }

    public String getBody() {
        return body.toString();
    }

    public String getSubject() {
        return subject;
    }
}
