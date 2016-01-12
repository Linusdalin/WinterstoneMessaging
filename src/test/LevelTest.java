package test;


import campaigns.CampaignState;
import campaigns.LevelUpTuesdayReward;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/*****************************************************************************''
 *
 *                  Sending a beep
 *
 *
 */

public class LevelTest {


    @Test
    public void levelUpBonusTest(){

        LevelUpTuesdayReward campaign = new LevelUpTuesdayReward(100, CampaignState.ACTIVE);

        assertThat(campaign.getCoinsForLevel(2), is(200));

        assertThat(campaign.getCoinsForLevel(10), is(750));

        assertThat(campaign.getCoinsForLevel(20), is(2250));
        assertThat(campaign.getCoinsForLevel(40), is(5250));
        assertThat(campaign.getCoinsForLevel(60), is(8250));
        assertThat(campaign.getCoinsForLevel(82), is(11550));
        assertThat(campaign.getCoinsForLevel(100), is(14600));
        assertThat(campaign.getCoinsForLevel(110), is(16700));

    }


}
