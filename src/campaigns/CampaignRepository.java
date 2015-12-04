package campaigns;

import java.util.ArrayList;
import java.util.List;

/****************************************************************'
 *
 *              Static repository of scheduled campaigns
 *
 *
 */


public class CampaignRepository {

    // Static list of active campaigns

    public static final List<CampaignInterface> activeCampaigns = new ArrayList<CampaignInterface>(){{

        add(new RememberDiamondCampaign     ( 80, CampaignState.ACTIVE ));
        add(new GettingStartedCampaign      ( 80, CampaignState.ACTIVE ));
        add(new BadBeatCampaign             ( 95, CampaignState.ACTIVE));

        add(new LevelUpCampaign             ( 60, CampaignState.ACTIVE ));
        //add(new GameActivationCampaign      ( 65, CampaignState.ACTIVE));
        //add(new ActivationPokeCampaign      ( 55, CampaignState.ACTIVE));
        //add(new ActivationFreeCoinCampaign  ( 62, CampaignState.ACTIVE));


        add(new ChurnPokeCampaign           ( 70, CampaignState.ACTIVE));
        add(new CoinsLeftCampaign           ( 70, CampaignState.ACTIVE));
        add(new ReactivationCampaign        ( 60, CampaignState.ACTIVE));

        //add(new HappyHourCampaign       ( 95, CampaignState.ACTIVE, 25));
        //add(new BlackFridayCampaign       ( 95, CampaignState.ACTIVE, 200));

        //add(new EngagementCampaign      ( 65, CampaignState.TEST_MODE ));
        //add(new FakeCoinsLeftCampaign   ( 90, CampaignState.TEST_MODE));

        add(new GameNotificationWeekendAB  (95, CampaignState.ACTIVE, "os_slotamerica_2x", "SlotAmerica presents ‘SlotAmerica’. Our eponymous game of the week really summarizes what we are all about! Try it today!", null));

        //add(new GameNotification (93, CampaignState.ACTIVE, "os7x", "7 days per week, 7 colors in the rainbow, 7 seas and 7 continents. 7x your win in SlotAmerica’s latest slot! Play Now!",
        //        null, null));

        //add(new GameNotificationGenderAB  (90, CampaignState.ACTIVE));         // Special test.

        add(new FirstPaymentCampaign    ( 94, CampaignState.ACTIVE));
        //add(new RewardReminderCampaign  ( 94, CampaignState.ACTIVE, RewardRepository.bellsFreespin, "famous_bells", "Don't forget your freespins to try out the new game release. It is still waitning for you"));

        //add(new MobileGameNotification( 96, CampaignState.TEST_MODE, "os6x", "New game out for SlotAmerica. Old School 6x. Try now!", null));

        add(new MobileCrossPromotionCampaign( 97, CampaignState.ACTIVE));
        add(new MobileConversionWelcomeCampaign( 99, CampaignState.ACTIVE));



    }};



    public List<CampaignInterface> getActiveCampaigns(){



        return activeCampaigns;
    }

}
