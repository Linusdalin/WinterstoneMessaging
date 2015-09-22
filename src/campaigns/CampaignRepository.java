package campaigns;

import action.ActionInterface;
import email.AbstractEmail;
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

        add(new GettingStartedCampaign      ( 80, CampaignState.ACTIVE ));
        add(new RememberDiamondCampaign     ( 80, CampaignState.ACTIVE ));
        add(new ChurnPokeCampaign           ( 70, CampaignState.ACTIVE));
        add(new BadBeatCampaign             ( 95, CampaignState.ACTIVE));
        add(new CoinsLeftCampaign           ( 70, CampaignState.ACTIVE));
        add(new LevelUpCampaign             ( 60, CampaignState.ACTIVE ));
        add(new ActivationPokeCampaign      ( 55, CampaignState.ACTIVE));
        add(new GameActivationCampaign      ( 65, CampaignState.ACTIVE));
        add(new ActivationFreeCoinCampaign  ( 62, CampaignState.ACTIVE));

        add(new HappyHourCampaign       ( 95, CampaignState.ACTIVE));

        add(new EngagementCampaign      ( 65, CampaignState.TEST_MODE ));
        add(new FakeCoinsLeftCampaign   ( 90, CampaignState.TEST_MODE));
        add(new ReactivationCampaign    ( 60, CampaignState.ACTIVE));

        add(new GameNotification        (90, CampaignState.ACTIVE, "fire_fruit", "The heat is on! Our new game Fire Fruit is hot and freshly served. 2,500 free coins to try it out. Click Now!", null, "115a00be-c90f-4c2c-80bc-7624273f4197"));


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
