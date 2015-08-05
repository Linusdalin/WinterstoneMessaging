package campaigns;

import action.ActionInterface;
import remoteData.dataObjects.User;

/************************************************************
 *
 *          General interface for campaigns
 *
 */
public interface CampaignInterface {

    ActionInterface evaluate(User user);

}
