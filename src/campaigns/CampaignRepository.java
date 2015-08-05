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

        add(new GettingStartedCampaign());

    }};


    public static List<CampaignInterface> getActiveCampaigns(){

        return activeCampaigns;
    }

}
