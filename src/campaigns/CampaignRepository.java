package campaigns;

import action.ActionInterface;

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
        add(new ActivationPokeCampaign      ( 55, CampaignState.ACTIVE));
        add(new ActivationFreeCoinCampaign  ( 62, CampaignState.ACTIVE));


        add(new ChurnPokeCampaign           ( 70, CampaignState.ACTIVE));
        add(new CoinsLeftCampaign           ( 70, CampaignState.ACTIVE));
        add(new ReactivationCampaign        ( 60, CampaignState.ACTIVE));

        //add(new HappyHourCampaign       ( 95, CampaignState.ACTIVE, 25));

        //add(new EngagementCampaign      ( 65, CampaignState.TEST_MODE ));
        //add(new FakeCoinsLeftCampaign   ( 90, CampaignState.TEST_MODE));

        //add(new GameNotificationWeekendAB  (95, CampaignState.ACTIVE, "famous_bells", "Come on in and Ring our Bells! 5 free-spins on the house to try our new slot “Famous Bells”. Just click to redeem.", RewardRepository.bellsFreespin));

        //add(new GameNotification (95, CampaignState.ACTIVE, "clockwork", "Can you stand the test of time? Here is 2000 coins on the house to try our new slot “Clockwork”. Just click to redeem.",
        //        null, RewardRepository.clockwork));

        //add(new GameNotificationGenderAB  (90, CampaignState.ACTIVE));         // Special test.

        add(new FirstPaymentCampaign    ( 95, CampaignState.ACTIVE));
        //add(new RewardReminderCampaign  ( 94, CampaignState.ACTIVE, RewardRepository.bellsFreespin, "famous_bells", "Don't forget your freespins to try out the new game release. It is still waitning for you"));

        //add(new MobileGameNotification( 96, CampaignState.TEST_MODE, "os6x", "New game out for SlotAmerica. Old School 6x. Try now!", null));

        add(new MobileCrossPromotionCampaign( 97, CampaignState.ACTIVE));
        add(new MobileConversionWelcomeCampaign( 99, CampaignState.ACTIVE));


    }};



    public List<CampaignInterface> getActiveCampaigns(){



        return activeCampaigns;
    }

    public int getCampaignIdByName(ActionInterface action) {

        int id = 0;

        for (CampaignInterface campaign : activeCampaigns) {

            if(action.isFiredBy(campaign))
                return id;

            id++;
        }

        throw new RuntimeException("Could not find campaign " + action.getCampaign() + " in action.");

    }
}
