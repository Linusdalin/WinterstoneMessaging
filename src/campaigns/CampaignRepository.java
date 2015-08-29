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
        add(new BadBeatCampaign         ( 95, CampaignState.INACTIVE));
        add(new CoinsLeftCampaign       ( 70, CampaignState.ACTIVE));
        add(new LevelUpCampaign         ( 60, CampaignState.ACTIVE ));
        add(new FakeCoinsLeftCampaign   ( 90, CampaignState.TEST_NODE ));

        //add(new GameNotification(90, "sweet_money", "Play SlotAmerica’s brand new Sweet Money, a delightful digital dessert guaranteed to not hurt your teeth! Click now"));

        //add(new ReactivationCampaign());

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
