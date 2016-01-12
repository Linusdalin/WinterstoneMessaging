package test;


import core.DataCache;
import core.PlayerInfo;
import dbManager.ConnectionHandler;
import dbManager.DatabaseException;
import org.junit.Test;
import remoteData.dataObjects.User;
import remoteData.dataObjects.UserTable;

import java.sql.Connection;
import java.sql.Timestamp;

import static junit.framework.Assert.assertTrue;

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
        try {
            table.load(connection);
        } catch (DatabaseException e) {
            assertTrue(false);
        }

        User user = table.getNext();

        System.out.println("user:" + user.toString());

        DataCache dbCache = new DataCache(cacheConnection, "2015-01-01", -1);
        PlayerInfo playerInfo = new PlayerInfo(user, dbCache);

        Timestamp lastSession = playerInfo.getLastSession();

        System.out.println("last: " + lastSession.toString());


    }
}
