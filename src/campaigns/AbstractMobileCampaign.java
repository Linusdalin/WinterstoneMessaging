package campaigns;

import remoteData.dataObjects.User;
import rewards.Reward;
import rewards.RewardRepository;

/*************************************************
 *
 *          Common functionality for the mobile cross promotion
 *
 */
public abstract class AbstractMobileCampaign extends AbstractCampaign{

    protected static final String GameLink = "https://app.adjust.com/drl7ga?deep_link=slotamerica://&campaign=";

    public AbstractMobileCampaign(String name, int priority, CampaignState state) {

        super(name, priority, state);
    }

    /************************************************************************'
     *
     *      For the mobile conversion campaigns
     *
     *
     * @param user        - the user
     * @return            - an appropriate reward
     */


    protected Reward getRewardForUser(User user) {

        if(isHighSpender(user))
            return RewardRepository.mobileHighRoller;

        if(isPaying(user))
            return RewardRepository.mobilePaying;

        if(isFrequent(user))
            return RewardRepository.mobileFrequent;

        return RewardRepository.mobile1;
    }



}
