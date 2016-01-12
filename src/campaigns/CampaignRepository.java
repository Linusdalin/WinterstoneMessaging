package campaigns;

import rewards.RewardRepository;

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


        add(new GettingStartedCampaign      ( 80, CampaignState.ACTIVE ));
        add(new BadBeatCampaign             ( 95, CampaignState.ACTIVE));
        add(new ChurnPokeCampaign           ( 70, CampaignState.ACTIVE));
        add(new MobilePokeNotification      ( 70, CampaignState.ACTIVE));
        add(new CoinsLeftCampaign           ( 60, CampaignState.ACTIVE));

        add(new LevelUpCampaign             ( 75, CampaignState.ACTIVE ));
        //add(new GameActivationCampaign      ( 65, CampaignState.ACTIVE));
        add(new ActivationPokeCampaign      ( 55, CampaignState.ACTIVE));
        add(new ActivationFreeCoinCampaign  ( 62, CampaignState.ACTIVE));

        //Weekend and game release

        //add(new GameNotificationWeekendAB  (95, CampaignState.ACTIVE, "os2x", "New Year, New Games! First out is the mother of multiplier slots - “Double Pay”. Click here to receive 12 free spins on our 3-reel casino classic!", RewardRepository.doublePay12));
        //add(new GameNotification (93, CampaignState.ACTIVE, "burning_sevens", "Burning Sevens light up the winter darkness, when SlotAmerica-Santa brings you 12 free spins – just click here to access!\n",
        //        null, RewardRepository.burningSevens12));


        //Monday

        // TODO: Check and fix day restriction (måndag) for these

        add(new TryNewGameOS2345Campaign        ( 70, CampaignState.ACTIVE));
        add(new TryNewGameClockworkCampaign     ( 70, CampaignState.ACTIVE));
        add(new TryNewGameSonicCampaign         ( 68, CampaignState.ACTIVE));
        add(new TryNewGameVideoPokerCampaign    ( 70, CampaignState.ACTIVE));
        add(new RewardReminderCampaign          ( 94, CampaignState.ACTIVE, RewardRepository.doublePay12, "os2x", "Your freespins still awaits you. Don't forget to try out the brand new Double Pay. Click here to claim"));


        // Tuesday and Wednesday level up

        add(new LevelUpTuesday              ( 70, CampaignState.ACTIVE ));
        add(new LevelUpTuesdayReward        ( 101, CampaignState.ACTIVE ));

        add(new FirstPaymentCampaign    ( 94, CampaignState.ACTIVE));


        // Mobile

        //add(new MobileGameNotification( 93, CampaignState.ACTIVE, "os_slotamerica_2x", "Old School SlotAmerica Game! Update the app now to try it out!", null));
        add(new MobileCrossPromotionCampaign( 94, CampaignState.ACTIVE));
        add(new MobileConversionWelcomeCampaign( 99, CampaignState.ACTIVE));

        //add(new CheatTest(100, CampaignState.ACTIVE));
        //add(new ReactivationCampaign        ( 60, CampaignState.ACTIVE));

        //add(new HappyHourCampaign       ( 95, CampaignState.ACTIVE, 25));
        //add(new BlackFridayCampaign       ( 95, CampaignState.ACTIVE, 200));

        //add(new EngagementCampaign      ( 65, CampaignState.TEST_MODE ));
        //add(new FakeCoinsLeftCampaign   ( 90, CampaignState.TEST_MODE));



        //add(new NewYearGiftCampaign( 90, CampaignState.ACTIVE));


    }};



    public List<CampaignInterface> getActiveCampaigns(){



        return activeCampaigns;
    }

}
