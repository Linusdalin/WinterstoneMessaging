package campaigns;

import action.ActionInterface;
import email.EmailInterface;
import email.ReleaseEmail;

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
        //add(new ActivationFreeCoinCampaign  ( 62, CampaignState.ACTIVE));

        //add(new ChurnPokeCampaign           ( 70, CampaignState.ACTIVE));
        //add(new CoinsLeftCampaign           ( 70, CampaignState.ACTIVE));
        //add(new ReactivationCampaign        ( 60, CampaignState.ACTIVE));

        //add(new HappyHourCampaign       ( 95, CampaignState.ACTIVE, 25));

        //add(new EngagementCampaign      ( 65, CampaignState.TEST_MODE ));
        //add(new FakeCoinsLeftCampaign   ( 90, CampaignState.TEST_MODE));

        //add(new GameNotificationWeekendAB  (95, CampaignState.ACTIVE, "famous_bells", "4 times the fun! 81 ways to win! Click here to get 5 free spins to test it out! ", null));

        //add(new GameNotification (95, CampaignState.ACTIVE, "clockwork", "Can you stand the test of time? Here is 2000 coins on the house to try our new slot “Clockwork”. Just click to redeem.",
        //        null, RewardRepository.clockwork));

        //add(new GameNotificationGenderAB  (90, CampaignState.ACTIVE));         // Special test.

        add(new FirstPaymentCampaign    ( 95, CampaignState.ACTIVE));
        //add(new RewardReminderCampaign  ( 94, CampaignState.ACTIVE, RewardRepository.clockwork, "clockwork", "Don't forget your "+RewardRepository.clockwork.getCoins()+" free coins to try out the new game release. It is still waitning for you"));

        add(new MobileGameNotification( 96, CampaignState.TEST_MODE, "os6x", "New game out for SlotAmerica. Old School 6x. Try now!", null));


        //TODO. Mobile welcome
        //TODO: Mobile release with new game
        //TODO: Mobile conversion special welcome
        //TODO: Mobile conversion mail (olika kategorier)


        //TODO: spelartyper: mobileonly, mobileconverted, mobileTry, mobileCanvasPay

    }};

    /*************************************************************************
     *
     *
     *
     *
     * @return  - an email of the type ReleaseEmail


    Subject: On a day like today – SlotAmerica beams with pride.
    Title: Remembering what happened, and celebrating our new beginning!

    Dear SlotAmerica player,

    It’s a special day today. A day to reflect, to love, to remember. Whilst this particular date is always a day of mourning and remembrance – it should also be a day to celebrate our come-back, our unbent pride and our belief in our American values. On such a strongly emotional day across the country, all we can wish for is that our games put a little smile on everyone’s face and lighten up the day.  Ribbons [länk till spel] celebrates a re-start, an opening of a new era.

    If a game can capture national pride from the people who made it, we assure you – SlotAmerica is beaming with it today. Ribbons [länk till spel] is our “reel” American tribute, a parade of stars and stripes in the most classic of gaming uniforms.

    And to really end on the happy note… Happy birthday, Bretagne! The last surviving 9/11 rescue dog was celebrated this week: eonli.ne/1XQvd6C

    We wish you all a good ending to the week. Take care of each other!

    Sam and Diane

    Your SlotAmerica Casino Managers

    */

    private static EmailInterface getEmail() {

        String mailSubject = "On a day like today – SlotAmerica beams with pride";
        String mailTitle = "Remembering what happened, and celebrating our new beginning!";
        String body =
                "    <p>It’s a special day today. A day to reflect, to love, to remember. Whilst this particular date is always a day of mourning and remembrance – it should also be a day to celebrate our come-back, our unbent pride and our belief in our American values. On such a strongly emotional day across the country, all we can wish for is that our games put a little smile on everyone’s face and lighten up the day.  " +
                        "<a href=\"https://apps.facebook.com/slotamerica/?ref=email0911&game=ribbons\">Ribbons</a> celebrates a re-start, an opening of a new era.</p>" +
                "    <p>If a game can capture national pride from the people who made it, we assure you – SlotAmerica is beaming with it today. " +
                        "<a href=\"https://apps.facebook.com/slotamerica/?ref=email0911&game=ribbons\">Ribbons</a> is our \"reel\" American tribute, a parade of stars and stripes in the most classic of gaming uniforms.</p>" +
                "    <p>And to really end on the happy note… Happy birthday, Bretagne! The last surviving 9/11 rescue dog was <a href=\"eonli.ne/1XQvd6C\">celebrated this week</a></p>" +
                "    <p>We wish you all a good ending to the week. Take care of each other!</p>" +
                "    <p><b>Sam and Diane</b></p>" +
                "    <p><i>Your SlotAmerica Casino Managers</i></p>";
        String alt = "    It’s a special day today. A day to reflect, to love, to remember. Whilst this particular date is always a day of mourning and remembrance – " +
                "it should also be a day to celebrate our come-back, our unbent pride and our belief in our American values. On such a strongly emotional day across the country, " +
                "all we can wish for is that our games put a little smile on everyone’s face and lighten up the day.  Ribbons celebrates a re-start, an opening of a new era.\n" +
                "    If a game can capture national pride from the people who made it, we assure you – SlotAmerica is beaming with it today. Ribbons is our “reel” American tribute, " +
                "a parade of stars and stripes in the most classic of gaming uniforms.\n" +
                "    And to really end on the happy note… Happy birthday, Bretagne! The last surviving 9/11 rescue dog was celebrated this week: eonli.ne/1XQvd6C\n" +
                "    We wish you all a good ending to the week. Take care of each other!\n\n" +
                "    Sam and Diane\n" +
                "    Your SlotAmerica Casino Managers\n";
        String image = "https://d24xsy76095nfe.cloudfront.net/campaigns/ribbons_sept.jpg";
        String link = "https://apps.facebook.com/slotamerica/?ref=email0911&game=ribbons";

        return new ReleaseEmail(mailSubject, mailTitle, body, alt, image, link);


    }


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
