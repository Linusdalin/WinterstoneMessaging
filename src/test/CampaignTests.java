package test;

import campaigns.GettingStartedCampaign;
import org.junit.Test;

import java.sql.Timestamp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/*******************************************************************''''''
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-08-10
 * Time: 11:00
 * To change this template use File | Settings | File Templates.
 */
public class CampaignTests {


    @Test
    public void dateComparisonTest(){

        Timestamp t1, t2;

        t1 = new Timestamp(2015, 8, 1, 15, 10, 0, 0);
        t2 = new Timestamp(2015, 7, 30, 10, 10, 0, 0);

        assertThat("Should be two days before", GettingStartedCampaign.daysBefore(t2, t1, 2), is(true));

        t1 = new Timestamp(2015, 8, 1, 0, 0, 1, 0);
        t2 = new Timestamp(2015, 7, 30, 23, 59, 59, 0);

        assertThat("Should be two days before", GettingStartedCampaign.daysBefore(t2, t1, 2), is(true));

        t2 = new Timestamp(2015, 8, 14, 16, 13, 59, 0);   //2015-08-14 16:13:59
        t1 = new Timestamp(2015, 8, 16, 20, 13, 59, 0);   //2015-08-14 16:13:59

        assertThat("Should be two days before", GettingStartedCampaign.daysBefore(t2, t1, 2), is(true));

    }
}
