package core;

import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.Payment;
import remoteData.dataObjects.User;

import java.util.List;

/**********************************************************************
 *
 *          Primitives based on the data for the user
 *
 *          This uses lazy loading of sessions and payments and is based
 *          on the session and payment data in the player object.
 *
 */


public class PlayerInfo {

    private final User user;
    private final DataCache dbCache;

    private List<Payment> userPayments;

    private List<GameSession> userSessions = null;
    private GameSession lastSession = null;

    public PlayerInfo(User user, DataCache dbCache){

        this.user = user;
        this.dbCache = dbCache;

    }


    public List<GameSession> getSessionsForUser() {

        if(userSessions == null)
            userSessions = dbCache.getSessionsForUser(user);

        if(userSessions.size() > 0)
            lastSession = userSessions.get(userSessions.size()-1);

        return userSessions;
    }

    public List<Payment> getPaymentsForUser() {

        if(userPayments == null){

           userPayments = dbCache.getPaymentsForUser(user);
        }

        return userPayments;
    }


    public GameSession getLastSession() {

        if(lastSession != null)
            return lastSession;

        if(user.sessions == 0)
            return null;

        if(userSessions != null && userSessions.size() > 0){
            lastSession = userSessions.get(userSessions.size()-1);
            return lastSession;
        }

        lastSession = dbCache.getLastSessionForUser(user);
        return lastSession;

    }

    public User getUser() {

        return user;
    }


}
