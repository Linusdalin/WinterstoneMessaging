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

        add(new GettingStartedCampaign());
        add(new RememberDiamondCampaign());
        add(new ChurnPokeCampaign());
        add(new BadBeatCampaign());
        add(new CoinsLeftCampaign());
        add(new GameNotification("sweet_money", "Play SlotAmerica’s brand new Sweet Money, a delightful digital dessert guaranteed to not hurt your teeth! Click now"));

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
