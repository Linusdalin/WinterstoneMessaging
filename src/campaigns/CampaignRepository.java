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

        add(new GettingStartedCampaign  ( 80, CampaignState.ACTIVE ));
        add(new RememberDiamondCampaign ( 80, CampaignState.ACTIVE ));
        add(new ChurnPokeCampaign       ( 70, CampaignState.ACTIVE));
        add(new BadBeatCampaign         ( 95, CampaignState.ACTIVE));
        add(new CoinsLeftCampaign       ( 70, CampaignState.ACTIVE));
        add(new LevelUpCampaign         ( 60, CampaignState.ACTIVE ));
        add(new EngagementCampaign      ( 65, CampaignState.ACTIVE ));

        add(new FakeCoinsLeftCampaign   ( 90, CampaignState.TEST_MODE));
        add(new ActivationPokeCampaign  ( 55, CampaignState.TEST_MODE));
        add(new ReactivationCampaign    ( 60, CampaignState.TEST_MODE));

        //add(new GameNotification        (90, CampaignState.INACTIVE, "triple_pay", "Spin your way into orbit with our game of the week, Triple Pay 3000! Click here for take-off!"));


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
