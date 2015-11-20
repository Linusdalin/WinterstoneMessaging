package test;

import action.Action;
import action.NotificationAction;
import campaigns.CampaignState;
import dbManager.ConnectionHandler;
import org.junit.Test;
import remoteData.dataObjects.User;

import java.sql.Connection;
import java.sql.Timestamp;

/*******************************************************************''''''
 *
 *          Test storing an action in the database
 *
 *
 */
public class ActionTests {

    private static final User user       = new User("627716024", "Linus",     "linusdalin@gmail.com", "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");
    Timestamp actionTime= new Timestamp(1015, 8, 1, 15, 10, 0, 0);
    Connection connection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);


    @Test
    public void StoreAction(){

        Action action = new NotificationAction("message", user, actionTime, 100, "ref", "campaign", 1, CampaignState.ACTIVE, 1.0);
        action.store( connection );


    }

    @Test
    public void StoreActionWithSpecialChar(){

        Action action = new NotificationAction("Don't miss", user, actionTime, 100, "ref", "campaign", 1, CampaignState.ACTIVE, 1.0);
        action.store( connection );


    }


}
