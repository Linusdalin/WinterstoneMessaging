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


    public String       facebookId;
    public Timestamp    lastSession;
    public int          failMail;
    public int          failNotification;
    public int          failPush;
    public int          desktopSessions;
    public int          iosSessions;
    public Timestamp    firstMobileSession;
    public Timestamp    lastMobileSession;
    public int          level;

    public CachedUser(String facebookId, Timestamp lastSession, int failMail, int failNotification, int failPush, int desktopSessions, int iosSessions, Timestamp firstMobileSession, Timestamp lastMobileSession, int level){


        this.facebookId = facebookId;
        this.lastSession = lastSession;
        this.failMail = failMail;
        this.failNotification = failNotification;
        this.failPush = failPush;
        this.desktopSessions = desktopSessions;
        this.iosSessions = iosSessions;
        this.firstMobileSession = firstMobileSession;
        this.lastMobileSession = lastMobileSession;
        this.level = level;
    }


    public String toString(){

        return "(" + facebookId + ", " +lastSession.toString() + ", M:" +failNotification+ ", N:" +failNotification + ", P:" +failPush + " - " + iosSessions + "/" +desktopSessions + ")";

    }

    /************************************************************
     *
     *          fallback from mobile will trigger an alternative communication method
     *          if mobile push fails and the player plays significantly
     *
     *
     * @return
     */


    public boolean fallbackFromMobile(){

        return(failPush > 1 && iosSessions > desktopSessions * 2);

    }

}
