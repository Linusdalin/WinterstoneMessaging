package localData;


import java.sql.Timestamp;

/**
 *
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-04-16
 * Time: 13:10
 * To change this template use File | Settings | File Templates.
 */
public class CachedUser {


    public String facebookId;
    public Timestamp lastSession;

    public CachedUser(String facebookId, Timestamp lastSession){


        this.facebookId = facebookId;
        this.lastSession = lastSession;

    }


    public String toString(){

        return "(" + facebookId + ", " +lastSession.toString() +  ")";

    }

}
