package test;


import org.junit.Test;
import output.DeliveryException;
import output.PushHandler;
import remoteData.dataObjects.User;
import rewards.RewardRepository;

import java.sql.Timestamp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/*****************************************************************************''
 *
 *                  Sending a test email
 *
 *                                              10152409426034632
 */

public class PushTest {

    private static final User user       = new User("627716024",            "Linus",     "linusdalin@gmail.com", "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");
    private static final User user2      = new User("10152816515441025",    "LinusDev",     "linusdalin@gmail.com", "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");
    private static final User user3      = new User("ap_4F3463D4-AFAE-4DD1-AA25-D4FF1C2C4B7C",    "MarkusTest",     "linusdalin@gmail.com", "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");
    private static final User wrongUser = new User("1111111",               "Mr avreggad", "linusdalin@gmail.com", "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");

    @Test
    public void sendTest(){

        try{

            PushHandler handler = new PushHandler()
                    .withMessage("Dont forget your daily bonus")
                    .withReward(RewardRepository.mobileTest.getCode())
                    .withGame("pink_sapphires")
                    .toRecipient(user.facebookId);


            boolean success = handler.send();

            assertThat("Should work", success, is(true) );

        }catch(DeliveryException e){

            assertTrue(false);
        }

    }


}
