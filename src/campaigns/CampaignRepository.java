package campaigns;

import rewards.RewardRepository;

import java.util.ArrayList;
import java.util.List;

/****************************************************************'
 *
 *              Static repository of scheduled campaigns
 *
 *              //TODO
 *               - Make separate mystery monday for mobile
 *               - more fallback to email for all mobile push
 *               - add pictures to the try new game emails
 *               - add more try new for mobile
 */


public class CampaignRepository {

    // Static list of active campaigns


    public static final List<CampaignInterface> activeCampaignsT = new ArrayList<CampaignInterface>(){{

            add(new ConversionOfferCampaign      ( 100, CampaignState.ACTIVE, false ));

    }};


    public static final List<CampaignInterface> activeCampaigns = new ArrayList<CampaignInterface>(){{

        // Morning messages

        add(new RememberDiamondMorningCampaign ( 70, CampaignState.ACTIVE));
        add(new ChurnPokeMorningCampaign       ( 70, CampaignState.ACTIVE));
        add(new GettingStartedMorningCampaign  ( 70, CampaignState.ACTIVE));


        add(new GettingStartedCampaign      ( 80, CampaignState.ACTIVE ));
        //add(new ConversionOfferCampaign      ( 101, CampaignState.TEST_MODE ));


        add(new BadBeatCampaign             ( 95, CampaignState.ACTIVE));
        add(new RememberDiamondCampaign     ( 70, CampaignState.ACTIVE));
        add(new ChurnPokeCampaign           ( 70, CampaignState.REDUCED));
        add(new MobilePokeNotification      ( 70, CampaignState.ACTIVE));
        add(new AnniversaryCampaign         ( 99, CampaignState.ACTIVE));
        //add(new CoinsLeftCampaign           ( 60, CampaignState.ACTIVE));

        //add(new SeventeenEmailCampaign      ( 59, CampaignState.ACTIVE, RewardRepository.loyaltyMystery2));
        //add(new SeventeenCampaign           ( 59, CampaignState.ACTIVE, RewardRepository.loyaltyMystery1));


        //add(new LevelUpCampaign             ( 75, CampaignState.ACTIVE ));
        //add(new ActivationPokeCampaign      ( 55, CampaignState.ACTIVE));
        //add(new ActivationFreeCoinCampaign  ( 62, CampaignState.ACTIVE));


        // Weekend and game release. (This is day-restricted (Thu, Fri, Sat, Sun)
        //

        add(new GameNotificationWeekendAB  (95, CampaignState.ACTIVE, "black_castle", "Win honor, glory and 10 free spins on our new slot game ‘Black Castle’!", RewardRepository.blackCastle));
        //add(new TryNewGameClubSevenCampaign( 72, CampaignState.ACTIVE, "lördag, måndag"));


        // Monday is the day for sending reward reminders and to get players to try new games. Adding more campaigns for newer games
        // Mystery Monday should be connected with a app post
        // All of these are day restricted

        //add(new MysteryMondayCampaign           ( 75, CampaignState.ACTIVE, RewardRepository.mysteryMonday6, "måndag"));
        //add(new MysteryMondayMobileCampaign     ( 75, CampaignState.ACTIVE, RewardRepository.mysteryMonday6, "måndag"));

        //add(new RewardReminderCampaign          ( 93, CampaignState.ACTIVE, RewardRepository.cocktail, "cocktail_cherries", "måndag", "Your Cocktail Cherries freespins still awaits you. Click here to claim"));
        //add(new RewardReminderCampaign          ( 95, CampaignState.ACTIVE, RewardRepository.eruption, "eruption", "måndag", "Your Eruption freespins still awaits you. Click here to claim"));


        /*

        add(new TryNewGameOS2345Campaign        ( 70, CampaignState.ACTIVE, "onsdag"));
        add(new TryNewGameCrystalCampaign       ( 68, CampaignState.ACTIVE, "onsdag"));
        add(new TryNewGameOS6XCampaign          ( 70, CampaignState.ACTIVE, "onsdag"));
        add(new TryNewGameClockworkCampaign     ( 71, CampaignState.ACTIVE, "onsdag"));
        add(new TryNewGameOS8XCampaign          ( 72, CampaignState.ACTIVE, "onsdag"));
        //add(new TryNewGameSonicCampaign         ( 68, CampaignState.ACTIVE, "tisdag"));
        //add(new TryNewGameAbsoluteSevenCampaign ( 71, CampaignState.ACTIVE, "tisdag"));         // Stop by Mar 31
        add(new TryNewGameVideoPokerCampaign    ( 67, CampaignState.ACTIVE, "onsdag"));



        add(new TryNewGameMobileOS6XCampaign    ( 70, CampaignState.ACTIVE, "onsdag"));
        add(new TryNewGameMobileOS5XQCampaign   ( 70, CampaignState.ACTIVE, "onsdag"));
        add(new TryNewGameMobileClockworkCampaign( 69, CampaignState.ACTIVE, "onsdag"));

*/


        // Tuesday and Wednesday level up

        //add(new LevelUpTuesday              ( 70, CampaignState.ACTIVE, "tisdag"));
        //add(new LevelUpTuesdayReward        ( 101, CampaignState.ACTIVE ));


        //Happy hour and sale

        //add(new SaleEventCampaign( 95, CampaignState.ACTIVE, "Our St. Patrick’s Day Sale is on. Grab your own pot of gold in SlotAmerica. Sale ends Friday night, so don’t wait!"));


        // Loyalty campaigns

        add(new FirstPaymentCampaign    ( 100, CampaignState.ACTIVE));

        // Mobile

        add(new MobileGameNotification( 93, CampaignState.ACTIVE, "os2x", "New Double Pay Game on mobile", null));
        add(new MobileCrossPromotionCampaign( 80, CampaignState.ACTIVE));
        add(new MobileConversionWelcomeCampaign( 99, CampaignState.ACTIVE));


        // Reactivations and hail marys
        // TODO: Make these on the right day to increase click-through. Mon - Wed and rest on Thursday

        //add(new ReactivationCampaign        ( 60, CampaignState.ACTIVE, "måndag, tisdag, onsdag"));
        add(new ReactivationMobileCampaign  ( 70, CampaignState.ACTIVE));    // More like activation of registered lost players
        add(new ReactivationEmailCampaign   ( 60, CampaignState.ACTIVE));


        //Misc

        //add(new CheatTest(100, CampaignState.ACTIVE));

        //add(new EngagementCampaign      ( 65, CampaignState.TEST_MODE ));
        //add(new FakeCoinsLeftCampaign   ( 90, CampaignState.TEST_MODE));

        //add(new NewYearGiftCampaign( 90, CampaignState.ACTIVE));
        //add(new DumyTest( 70, CampaignState.ACTIVE));
        //add(new GameActivationCampaign      ( 65, CampaignState.ACTIVE));
        //add(new SuperTuesdayCampaign ( 70, CampaignState.ACTIVE));
        //add(new ValentinesCampaign            ( 94, CampaignState.ACTIVE));
        //add(new ValentinesThankyou            ( 101, CampaignState.ACTIV E));
        //add(new BlackFridayCampaign       ( 95, CampaignState.ACTIVE, 200));

    }};



    public List<CampaignInterface> getActiveCampaigns(){

        return activeCampaigns;
    }

}
