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

    public static final List<CampaignInterface> activeCampaigns = new ArrayList<CampaignInterface>(){{


        add(new RememberDiamondMorningCampaign ( 70, CampaignState.ACTIVE));
        add(new ChurnPokeMorningCampaign       ( 70, CampaignState.REDUCED));


        add(new GettingStartedCampaign      ( 80, CampaignState.REDUCED ));
        //add(new BadBeatCampaign             ( 95, CampaignState.ACTIVE));
        add(new RememberDiamondCampaign     ( 70, CampaignState.ACTIVE));
        add(new ChurnPokeCampaign           ( 70, CampaignState.REDUCED));
        add(new MobilePokeNotification      ( 70, CampaignState.ACTIVE));
        //add(new CoinsLeftCampaign           ( 60, CampaignState.ACTIVE));

        //add(new SeventeenEmailCampaign      ( 59, CampaignState.ACTIVE, RewardRepository.loyaltyMystery2));
        //add(new SeventeenCampaign           ( 59, CampaignState.ACTIVE, RewardRepository.loyaltyMystery1));


        //add(new LevelUpCampaign             ( 75, CampaignState.ACTIVE ));
        add(new ActivationPokeCampaign      ( 55, CampaignState.ACTIVE));
        //add(new ActivationFreeCoinCampaign  ( 62, CampaignState.ACTIVE));

        // Weekend and game release. (This is day-restricted (Thu, Fri, Sat, Sun)
        //

        add(new GameNotificationWeekendAB  (95, CampaignState.REDUCED, "president", "Show who the real boss is! Play the game and rule the reels. 10 free spins to try our new game President.", RewardRepository.president));
        //add(new GameNotification (93, CampaignState.ACTIVE, "president", "Hail to the Chief! It is presidents day. Click here for the free spins",null, RewardRepository.president));


        // Monday is the day for sending reward reminders and to get players to try new games. Adding more campaigns for newer games
        // Mystery Monday should be connected with a app post
        // All of these are day restricted

        //add(new TryNewGameOS2345Campaign        ( 70, CampaignState.ACTIVE, "måndag"));
        //add(new TryNewGameOS6XCampaign          ( 70, CampaignState.ACTIVE, "tisdag"));
        //add(new TryNewGameClockworkCampaign     ( 70, CampaignState.ACTIVE, "måndag"));
        //add(new TryNewGameSonicCampaign         ( 68, CampaignState.ACTIVE, "måndag"));
        //add(new TryNewGameAbsoluteSevenCampaign ( 71, CampaignState.ACTIVE, "måndag"));
        //add(new TryNewGameVideoPokerCampaign    ( 70, CampaignState.ACTIVE, "måndag"));

        //add(new TryNewGameMobileOS6XCampaign    ( 70, CampaignState.ACTIVE, "måndag"));

        //add(new MysteryMondayCampaign           ( 75, CampaignState.ACTIVE, RewardRepository.mysteryMonday2));
        //add(new RewardReminderCampaign          ( 94, CampaignState.ACTIVE, RewardRepository.eightX8, "eight_times_pay", "måndag", "Your Eight times pay freespins still awaits you. Click here to claim"));


        // Tuesday and Wednesday level up

        //add(new LevelUpTuesday              ( 70, CampaignState.ACTIVE ));
        //add(new LevelUpTuesdayReward        ( 101, CampaignState.ACTIVE ));

        //Wednesday Happy hour

        //add(new HappyHourCampaign           ( 95, CampaignState.ACTIVE, 100));
        //add(new ValentinesCampaign            ( 94, CampaignState.ACTIVE));
        add(new ValentinesThankyou            ( 101, CampaignState.ACTIVE));

        add(new FirstPaymentCampaign    ( 100, CampaignState.ACTIVE));

        // Mobile

        add(new MobileGameNotification( 93, CampaignState.ACTIVE, "golden_dollar", "Try Golden Dollar!", null));
        add(new MobileCrossPromotionCampaign( 80, CampaignState.ACTIVE));
        add(new MobileConversionWelcomeCampaign( 99, CampaignState.ACTIVE));


        // Reactivations and hail marys
        // TODO: Make these on the right day to increase click-through. Mon - Wed and rest on Thursday

        //add(new ReactivationCampaign        ( 60, CampaignState.ACTIVE));
        add(new ReactivationMobileCampaign  ( 70, CampaignState.ACTIVE));    // More like activation of registered lost players
        add(new ReactivationEmailCampaign   ( 60, CampaignState.ACTIVE));


        //Misc

        //add(new CheatTest(100, CampaignState.ACTIVE));
        //add(new BlackFridayCampaign       ( 95, CampaignState.ACTIVE, 200));

        //add(new EngagementCampaign      ( 65, CampaignState.TEST_MODE ));
        //add(new FakeCoinsLeftCampaign   ( 90, CampaignState.TEST_MODE));

        //add(new NewYearGiftCampaign( 90, CampaignState.ACTIVE));
        //add(new DumyTest( 70, CampaignState.ACTIVE));
        //add(new GameActivationCampaign      ( 65, CampaignState.ACTIVE));

    }};



    public List<CampaignInterface> getActiveCampaigns(){

        return activeCampaigns;
    }

}
