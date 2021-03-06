package email;

import net.sf.json.JSONObject;
import remoteData.dataObjects.User;

/*************************************************************
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-09-11
 * Time: 08:50
 * To change this template use File | Settings | File Templates.
 */

public interface EmailInterface {

    String getPlainText();
    String getSubject();
    String getBody();

    String getTemplate();

    JSONObject toJSON();

    void addContentBoxes(User user, int nax);
}
