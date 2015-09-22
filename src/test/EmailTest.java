package test;


import campaigns.FirstPaymentCampaign;
import email.NotificationEmail;
import email.ReleaseEmail;
import org.junit.Test;
import output.EmailHandler;
import remoteData.dataObjects.Payment;
import remoteData.dataObjects.User;

import java.sql.Timestamp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/*****************************************************************************''
 *
 *                  Sending a test email
 *
 *
 */

public class EmailTest {

    private static final User user = new User("627716024", "Linus", "linusdalin@gmail.com", "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A");
    private static final User wrongUsesr = new User("1111111", "Mr avreggad", "linusdalin@gmail.com", "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A");

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

    private static final Payment payment = new Payment("627716024", 30, "", new Timestamp(2015, 1, 1, 1, 1, 1, 1), "promo", new Timestamp(2015, 1, 1, 1, 1, 1, 1));


        @Test
    public void sendTest(){


        EmailHandler handler = new EmailHandler()
                .withEmail(testMail)
                .toRecipient(user);

        boolean success = handler.send();

        assertThat("Should work", success, is(true) );

    }

    @Test
    public void failWrongUserTest(){


        EmailHandler handler = new EmailHandler()
                .withEmail(testMail)
                .toRecipient(wrongUsesr);

        boolean success = handler.send();

        assertThat("Should not work", success, is(false) );

    }


    @Test
    public void overrideTest(){


        EmailHandler handler = new EmailHandler( user.facebookId )
                .withEmail(testMail)
                .toRecipient(wrongUsesr);

        boolean success = handler.send();

        assertThat("Should work", success, is(true) );

    }


    @Test
    public void emailStyleTest(){


        EmailHandler handler = new EmailHandler()
                .withEmail(testMail2)
                .toRecipient(user);

        boolean success = handler.send();

        assertThat("Should work", success, is(true) );

    }

    @Test
    public void vipMailTest(){


        EmailHandler handler = new EmailHandler()
                .withEmail(FirstPaymentCampaign.firstDepositEmail(user, payment))
                .toRecipient(user);

        boolean success = handler.send();

        assertThat("Should work", success, is(true) );

    }


}
