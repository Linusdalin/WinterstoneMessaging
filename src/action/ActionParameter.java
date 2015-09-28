package action;

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

}
