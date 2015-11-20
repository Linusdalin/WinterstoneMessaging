package action;

import net.sf.json.JSONObject;

/********************************************************************'
 *
 *              User parameters for an action
 */


public class ActionParameter {

    public final String name;
    public final String facebookId;
    public final String email;

    public ActionParameter(String name, String facebookId, String email){

        this.name = name;
        this.facebookId = facebookId;
        this.email = email;
    }


    public ActionParameter(JSONObject json){

        this.name = json.getString("name");
        this.facebookId = json.getString("facebookId");
        this.email = json.getString("email");
    }



    public JSONObject toJSON() {
        return new JSONObject()
                .put("name", name)
                .put("facebookId", facebookId)
                .put("email", email);
    }
}
