package hailMary;

import action.ActionInterface;
import action.NotificationAction;
import campaigns.CampaignState;
import remoteData.dataObjects.User;
import rewards.RewardRepository;

import java.sql.Timestamp;

/******************************************************
 *
 *              This is actions against the list of lost players,
 *              They are not in the database for one reason or another
 *
 *
 */
public class HailMary extends HailMaryUsers {

    private static final String Name = "HaleMary";

    public HailMary(){

    }


    public void execute(Timestamp executionTime) {


        for (String lostUser : lostUsers) {

            User user = new User(lostUser, "", "", "", "", null, 0, 0, 0, 0, 0, 0, 0, 0, "A", "M");

            ActionInterface action = new NotificationAction( "Hello, We have added "+ RewardRepository.freeCoinAcitivation.getCoins()+" free coins for you to play with on your account. Click here to collect and play!",
                    user, executionTime, 60, Name,  Name, 1, CampaignState.ACTIVE, 1.0)
                    .withReward(RewardRepository.freeCoinAcitivation);


        }


    }
}
