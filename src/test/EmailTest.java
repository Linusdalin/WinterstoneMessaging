package test;


import org.junit.Test;
import output.EmailHandler;
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


    @Test
    public void sendTest(){


        String message = "<p> This is the actual <b>message</b>. It should manage strange encoding like & and ? and ,.;>></p>" +
                "<p>Click me: <a href=\"https://apps.facebook.com/slotAmerica/?game=wild_cherries&promoCode=test-e\"> Try Wild Cherries</a>!</p>";
        String altMessage = "Plain text version";
        String subject = " this is a message for you!";

        EmailHandler handler = new EmailHandler()
                .withTemplate( EmailHandler.MESSAGE_TEMPLATE)
                .withRecipient(user)
                .withSubject( subject )
                .withMessage( message )
                .withAlt( altMessage );

        boolean success = handler.send();

        assertThat("Should work", success, is(true) );

    }

    @Test
    public void failWrongUserTest(){


        String message = "<p> This is the actual <b>message</b></p>";
        String altMessage = "Plain text version";
        String subject = " this is a message for you!";

        EmailHandler handler = new EmailHandler()
                .withTemplate( EmailHandler.MESSAGE_TEMPLATE)
                .withRecipient(wrongUsesr)
                .withSubject( subject )
                .withMessage( message )
                .withAlt( altMessage );

        boolean success = handler.send();

        assertThat("Should not work", success, is(false) );

    }


    @Test
    public void overrideTest(){


        String message = "<p> This is the test message</p>";
        String altMessage = "Plain text version";
        String subject = " this is just test!";

        EmailHandler handler = new EmailHandler( user.facebookId )
                .withTemplate( EmailHandler.MESSAGE_TEMPLATE )
                .withRecipient(wrongUsesr)
                .withSubject( subject )
                .withMessage( message )
                .withAlt( altMessage );

        boolean success = handler.send();

        assertThat("Should work", success, is(true) );

    }


    @Test
    public void emailStyleTest(){


        String message = "this is a new game release message";
        String altMessage = "Plain text version";
        String subject = "it is back";
        String title = "it is Back";
        String url = "https://d24xsy76095nfe.cloudfront.net/campaigns/sweet_money.jpg";
        String game = "sweet_money";

        EmailHandler handler = new EmailHandler()
                .withTemplate( EmailHandler.GAME_TEMPLATE)
                .withRecipient( user )
                .withSubject( subject )
                .withTitle( title )
                .withImageURL( url, game )
                .withMessage( message )
                .withAlt( altMessage );

        boolean success = handler.send();

        assertThat("Should work", success, is(true) );

    }



}
