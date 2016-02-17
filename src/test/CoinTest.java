package test;


import org.junit.Test;
import output.DeliveryException;
import output.GiveAwayHandler;
import remoteData.dataObjects.Payment;
import remoteData.dataObjects.User;

import java.sql.Timestamp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/*****************************************************************************''
 *
 *                  test Giving coins to a player
 *
 *
 */

public class CoinTest {

    private static final User user       = new User("627716024", "Linus",     "linusdalin@gmail.com", "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male", Timestamp.valueOf("2016-01-01 00:00:00"));
    private static final User wrongUsesr = new User("1111111", "Mr avreggad", "linusdalin@gmail.com", "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male", Timestamp.valueOf("2016-01-01 00:00:00"));


    private static final Payment payment = new Payment("627716024", 30, "", new Timestamp(2015, 1, 1, 1, 1, 1, 1));


    @Test
    public void giveTest(){

        try{

            GiveAwayHandler handler = new GiveAwayHandler(null)
                    .withAmount(1000)
                    .toRecipient(user.facebookId);


            boolean success = handler.send();

            assertThat("Should work", success, is(true) );

        }catch(DeliveryException e){

            assertTrue(false);
        }

    }

}
