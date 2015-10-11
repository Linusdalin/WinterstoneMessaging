package test;


import campaigns.FirstPaymentCampaign;
import campaigns.GettingStartedCampaign;
import email.NotificationEmail;
import email.ReleaseEmail;
import org.junit.Test;
import output.DeliveryException;
import output.EmailHandler;
import remoteData.dataObjects.Payment;
import remoteData.dataObjects.User;

import java.sql.Timestamp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/*****************************************************************************''
 *
 *                  Sending a test email
 *
 *
 */

public class EmailTest {

    private static final User user       = new User("627716024", "Linus",     "linusdalin@gmail.com", "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");
    private static final User wrongUsesr = new User("1111111", "Mr avreggad", "linusdalin@gmail.com", "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");

    private static final NotificationEmail testMail = new NotificationEmail(
            " this is a message for you!",
            "<p> This is the actual <b>message</b>. It should manage strange encoding like & and ? and ,.;>></p>" +
                    "<p>Click me: <a href=\"https://apps.facebook.com/slotAmerica/?game=wild_cherries&promoCode=test-e\"> Try Wild Cherries</a>!</p>",
            "Plain text version"
    );


    private static final ReleaseEmail testMail2 = new ReleaseEmail(
            " this is a message for you!",
            " this is the title",
            "<p> This is the actual <b>message</b>. It should manage strange encoding like & and ? and ,.;>></p>" +
                    "<p>Click me: <a href=\"https://apps.facebook.com/slotAmerica/?game=wild_cherries&promoCode=test-e\"> Try Wild Cherries</a>!</p>",
            "Plain text version",
            "https://d24xsy76095nfe.cloudfront.net/campaigns/ribbons_sept.jpg",
            "https://apps.facebook.com/slotamerica"

    );

    private static final Payment payment = new Payment("627716024", 30, "", new Timestamp(2015, 1, 1, 1, 1, 1, 1));


    @Test
    public void sendTest(){

        try{

            EmailHandler handler = new EmailHandler()
                    .withEmail(testMail)
                    .toRecipient(user.facebookId);

            boolean success = handler.send();

            assertThat("Should work", success, is(true) );

        }catch(DeliveryException e){

            assertTrue(false);
        }


    }

    @Test
    public void multiSendTest(){



        try{

            for(int i = 1; i <= 5; i++){

                NotificationEmail testMail = new NotificationEmail(
                        " this is a message for you!",
                        "<p> This is mail number "+ i +"</p>",
                        "Plain text version"
                );


                EmailHandler handler = new EmailHandler()
                        .withEmail(testMail)
                        .toRecipient(user.facebookId);

                boolean success = handler.send();

                assertThat("Should work", success, is(true) );

            }


        }catch(DeliveryException e){

            assertTrue(false);
        }


    }



    @Test
    public void failWrongUserTest(){

        try{

            EmailHandler handler = new EmailHandler()
                    .withEmail(testMail)
                    .toRecipient(wrongUsesr.facebookId);

            boolean success = handler.send();

            assertThat("Should not work", success, is(false) );

        }catch(DeliveryException e){

            assertTrue(false);
        }



    }


    @Test
    public void overrideTest(){

        try{

            EmailHandler handler = new EmailHandler( user.facebookId )
                    .withEmail(testMail)
                    .toRecipient(wrongUsesr.facebookId);

            boolean success = handler.send();

            assertThat("Should work", success, is(true) );

        }catch(DeliveryException e){

            assertTrue(false);
        }

    }


    @Test
    public void emailStyleTest(){

        try{

            EmailHandler handler = new EmailHandler()
                    .withEmail(testMail2)
                    .toRecipient(user.facebookId);

            boolean success = handler.send();

            assertThat("Should work", success, is(true) );

        }catch(DeliveryException e){

            assertTrue(false);
        }


    }

    @Test
    public void vipMailTest(){

        try{

            boolean success;
            EmailHandler handler;

            handler = new EmailHandler()
                    .withEmail(FirstPaymentCampaign.firstDepositEmail(user, payment))
                    .toRecipient(user.facebookId);

            success = handler.send();

            assertThat("Should work", success, is(true) );


            handler = new EmailHandler()
                    .withEmail(GettingStartedCampaign.gettingStartedEmail(user))
                    .toRecipient(user.facebookId);

            success = handler.send();

            assertThat("Should work", success, is(true) );

        }catch(DeliveryException e){

            assertTrue(false);
        }



    }


}
