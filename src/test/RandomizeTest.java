package test;


import campaigns.AbstractCampaign;
import org.junit.Test;
import remoteData.dataObjects.User;

import java.sql.Timestamp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/*****************************************************************************''
 *
 *                  Testing that the randomization functions work
 *
 *
 */

public class RandomizeTest {

    private static final User user       = new User("627716024", "Linus",     "linusdalin@gmail.com", "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");
    private static final User user2       = new User("627716021", "Linus",     "linusdalin@gmail.com", "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");

    @Test
    public void randomize3Test(){

        assertThat(AbstractCampaign.randomize3(user, 0), is(false));
        assertThat(AbstractCampaign.randomize3(user, 1), is(true));
        assertThat(AbstractCampaign.randomize3(user, 2), is(false));

        assertThat(AbstractCampaign.randomize3(user2, 0), is(true));
        assertThat(AbstractCampaign.randomize3(user2, 1), is(false));
        assertThat(AbstractCampaign.randomize3(user2, 2), is(false));


    }

    @Test
    public void randomize4Test(){

        assertThat(AbstractCampaign.randomize4(user, 0), is(false));
        assertThat(AbstractCampaign.randomize4(user, 1), is(true));
        assertThat(AbstractCampaign.randomize4(user, 2), is(false));
        assertThat(AbstractCampaign.randomize4(user, 2), is(false));

        assertThat(AbstractCampaign.randomize4(user2, 0), is(true));
        assertThat(AbstractCampaign.randomize4(user2, 1), is(false));
        assertThat(AbstractCampaign.randomize4(user2, 2), is(false));
        assertThat(AbstractCampaign.randomize4(user2, 2), is(false));


    }



}
