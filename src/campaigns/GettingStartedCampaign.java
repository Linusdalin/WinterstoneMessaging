package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import remoteData.dataObjects.User;

/************************************************************************'
 *
 *              The getting started campaign is sending messages to
 *              players that has only installed and played one session
 */

public class GettingStartedCampaign implements CampaignInterface {

    private static final String Name = "Getting started";

    GettingStartedCampaign(){

    }

    /********************************************************************
     *
     *              Decide on the campaign
     *
     *              The output could be one of 4 different messages depending on the day
     *
     *
     * @param user             - the user to evaluate
     * @return
     */


    public ActionInterface evaluate(User user) {

        if( user.amount == 0 &&
            user.lastgamePlayed.equals("")){

            // Check days before here

            return new NotificationAction("come back", user, "", 90);

        }
        else{

            System.out.println("Campaign " + Name + " not applicable for player" + user.name );
            return null;
        }


    }
}
