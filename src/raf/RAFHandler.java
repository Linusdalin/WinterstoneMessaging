package raf;

import remoteData.dataObjects.User;

import java.sql.Connection;
import java.sql.Timestamp;

/***********************************************************************
 *
 *              Functionality for Refer a Friend
 */
public class RAFHandler {

    private User user;
    private Connection connection;
    private static final int COINS_FOR_REG = 5000;

    public RAFHandler(User user, Connection connection) {

        this.user = user;
        this.connection = connection;
    }

    public User getRAFParent(User user) {

        if(user.promoCode == null)
            return null;

        if(user.promoCode.startsWith("raf_")){


            // Creating a fake user. The only thing we need is the userId
            String otherUserId = user.promoCode.substring(user.promoCode.indexOf("_", 4));
            return new User(otherUserId, "", "", "", "", Timestamp.valueOf("2016-01-01 00:00:00"), 0, 0, 0, 0, 0, 0, 0, 0, "", "", Timestamp.valueOf("2016-01-01 00:00:00"));
        }

        return null;
    }

    public boolean isFirstPayment() {

        FriendTable table = new FriendTable(connection);


        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    public boolean isNewRegistration() {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    public int getCoinsForPayment() {
        return 0;  //To change body of created methods use File | Settings | File Templates.
    }

    public int getCoinsForRegistration() {
        return COINS_FOR_REG;
    }
}
