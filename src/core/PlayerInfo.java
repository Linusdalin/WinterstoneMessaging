package core;

import localData.Exposure;
import localData.ExposureTable;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.Payment;
import remoteData.dataObjects.User;

import java.util.List;

/**********************************************************************
 *
 *          Primitives based on the data for the user
 */


public class PlayerInfo {

    private final User user;
    private final DataCache dbCache;

    private final List<Payment> userPayments;

    private List<GameSession> userSessions = null;
    private GameSession lastSession = null;

    public PlayerInfo(User user, DataCache dbCache){

        this.user = user;
        this.dbCache = dbCache;
        userPayments = dbCache.getPaymentsForUser(user);


    }


    public List<GameSession> getSessionsForUser() {

        if(userSessions == null)
            userSessions = dbCache.getSessionsForUser(user);

        if(userSessions.size() > 0)
            lastSession = userSessions.get(userSessions.size()-1);

        return userSessions;
    }

    public List<Payment> getPaymentsForUser() {

        return userPayments;
    }


    public GameSession getLastSession() {

        if(userSessions == null){
            userSessions = dbCache.getSessionsForUser(user);

            if(userSessions.size() > 0)
                lastSession = userSessions.get(userSessions.size()-1);

        }
        return lastSession;
    }

    public User getUser() {

        return user;
    }


}
