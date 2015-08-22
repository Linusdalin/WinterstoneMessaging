package test;


import core.DataCache;
import core.PlayerInfo;
import dbManager.ConnectionHandler;
import org.junit.Test;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.User;
import remoteData.dataObjects.UserTable;

import java.sql.Connection;

/*****************************************************************************''
 *
 *                  Code to look at a specific user in the database
 *
 *
 */

public class SpecificUserTest {


    @Test
    public void getUser(){

        Connection cacheConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);
        Connection connection = ConnectionHandler.getConnection(ConnectionHandler.Location.remote);
        UserTable  table = new UserTable("facebookId = '105390519812878'", 1);
        table.load(connection);

        User user = table.getNext();

        System.out.println("user:" + user.toString());

        DataCache dbCache = new DataCache(cacheConnection, "2015-01-01", -1);
        PlayerInfo playerInfo = new PlayerInfo(user, dbCache);

        GameSession lastSession = playerInfo.getLastSession();

        System.out.println("last: " + lastSession.toString());


    }
}
