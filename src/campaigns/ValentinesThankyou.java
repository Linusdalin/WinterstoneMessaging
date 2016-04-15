package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;
import response.ResponseStat;
import rewards.RewardRepository;

import java.sql.Timestamp;


/************************************************************************'
 *
 *              For ongoing happy hour campaigns send info to paying players
 */

public class ValentinesThankyou extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "ValentinesTY";
    private static final int CoolDown_Days = 20000;            // Only once
    private static final int[] MessageIds = { };

    private static final String[] Players = {
            "1471069663", "1139647949397874", "818795122", "100001109837750", "677412042370154", "1656782794562545",
            "10204184836288892", "10204883578074130", "453566374807138", "100008104639120", "986869787997673",
            "748195991956461", "656949407772479", "1656782794562545", "502182156613975", "949613105108821", "10201350919693160" };



    // Trigger specific config data
    private static final int MAX_INACTIVITY = 9;

    ValentinesThankyou(int priority, CampaignState activation){

        super(Name, priority, activation);
        setCoolDown(CoolDown_Days);
        registerMessageIds( MessageIds );
    }

    /********************************************************************
     *
     *              Decide on the campaign
     *
     *              The output could be one of 4 different messages depending on the day
     *
     * @param playerInfo             - the user to evaluate
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {


        User user = playerInfo.getUser();


        if(inList(user.id, Players)){

            System.out.println("    -- Sending a reward to the player" );
            return new NotificationAction("Thank you for sharing your Valentines love on our page! Click here for 10,000 coin reward!",
                    user, executionTime, getPriority(), getTag(),  Name, 1, getState(), responseFactor)
                    .withReward(RewardRepository.valentineCoins);

        }

        return null;

    }


    /*********************************************************************
     *
     *              Campaign timing restrictions
     *
     * @param executionTime     - time of execution
     * @return                  - messgage or null if ok.
     */

    public String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {


        String tooEarlyCheck = isTooEarly(executionTime, overrideTime);

        return tooEarlyCheck;

    }



    private boolean inList(String playerId, String[] listOfPlayers){

        for (String aPlayer : listOfPlayers) {

            if(playerId.equals(aPlayer))
                return true;
        }

        return false;


    }

}
