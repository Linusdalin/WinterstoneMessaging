package test;


import org.junit.Test;
import output.DeliveryException;
import output.PushHandler;
import remoteData.dataObjects.User;
import rewards.Reward;

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

    private static final User user       = new User("627716024",                "Linus",        "linusdalin@gmail.com",     "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");
    private static final User stageLinus = new User("10152816515441025",        "LinusTest",    "linusdalin@gmail.com",     "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");
    private static final User user3      = new User("ap_4F3463D4-AFAE-4DD1-AA25-D4FF1C2C4B7C", "MarkusTest", "linusdalin@gmail.com", "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");
    private static final User wrongUser  = new User("1111111",                  "Mr avreggad",  "linusdalin@gmail.com",     "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");
    private static final User roos       = new User("10152409426034632",        "Roos",         "linusdalin@gmail.com",     "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");
    private static final User markus     = new User("10155448131120431",        "Markus",       "linusdalin@gmail.com",     "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");

    private static final User nissen     = new User("10153396329897575",        "Tobias",       "linusdalin@gmail.com",     "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");

    private static final User fredrik     = new User("906873472663922",        "Fredrik",       "linusdalin@gmail.com",     "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");


    private static Reward stageReward = new Reward("stage", "036e588a-aaee-42da-88c1-c489a9812ccc ", 3000, true);


    @Test
    public void sendTest(){


        try{

            PushHandler handler = new PushHandler()
                    .withMessage("Play again!")
                    .withReward(stageReward.getCode())
                    .withGame("royal_colors")
                    .toRecipient(user.facebookId);


            boolean success = handler.send();

            assertThat("Should work", success, is(true) );

        }catch(DeliveryException e){

            assertTrue(false);
        }

    }

    /***************************************************************************
     *
     *
     *          This does not result in a message. The stage server cannot send push notifications
     *
     */

    @Test
    public void sendStageTest(){

        String StagePushService = "http://slotamerica:fruitclub@dev.slot-america.com:3302/sendNotification/";

        try{

            PushHandler handler = new PushHandler()
                    .withMessage("Play again!")
                    .withReward(stageReward.getCode())
                    .withGame("royal_colors")
                    .withAlternateService(StagePushService)
                    .toRecipient(stageLinus.facebookId);


            boolean success = handler.send();

            assertThat("Should work", success, is(true) );

        }catch(DeliveryException e){

            assertTrue(false);
        }

    }


    @Test
    public void personalMessageTest(){

        try{

            PushHandler handler = new PushHandler()
                    .withMessage("This is a test message from Linus!")
                    .withGame("royal_colors")
                    .toRecipient(nissen.facebookId);

            boolean success = handler.send();

            assertThat("Should work", success, is(true) );

        }catch(DeliveryException e){

            assertTrue(false);
        }

    }




}
