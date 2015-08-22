package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.User;

import java.sql.Timestamp;


/************************************************************************'
 *
 *                  Old players
 */

public class ReactivationCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Reactivation";
    private static final int CoolDown_Days = 36500;     // Only once per player

    // Trigger specific config data
    private static final int INACTIVITY_LIMIT   = 22;   // 22 days inactivity before kicking in this offer
    private static final int HIGH_SPENDER       = 10;   // Average spend of $15

    ReactivationCampaign(){

        super(Name);
        setCoolDown(CoolDown_Days);
    }


    /********************************************************************
     *
     *              Decide on the campaign
     *
     *              The output could be one of 4 different messages depending on the day
     *
     *
     *
     * @param playerInfo             - the user to evaluate
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime) {


        //System.out.println("Registration Date: " + getDay(user.created).toString());
        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        GameSession lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }
        int inactivity = getDaysBetween(lastSession.timeStamp, executionDay);

        if(inactivity > INACTIVITY_LIMIT){

            if(user.payments > 4 && user.amount / user.payments > HIGH_SPENDER ){

                System.out.println("    -- Campaign " + Name + " firing message1. Creating bonus for player" );
                return new NotificationAction("We haven't seen you in a while. There are some fabulous new games to try out. ", user, 90, createTag(Name), createPromoCode(Name, user, inactivity), Name)
                        .withReward("cac6b086-189f-4ee6-bb30-7bcfb2a0ecfa");


            }else if(user.payments > 0){

                System.out.println("    -- Campaign " + Name + " firing message2. Creating bonus for player" );
                return new NotificationAction("We haven't seen you in a while. There are some fabulous new games to try out. ", user, 90, createTag(Name), createPromoCode(Name, user, inactivity), Name)
                    .withReward("93f00dac-26cf-46e4-8bde-1eb59dd13032");       //TODO: Change to correct

            }else if(user.sessions > 40){

                    System.out.println("    -- Campaign " + Name + " firing message3. Creating bonus for player" );
                    return new NotificationAction("You have got 20,000 free coins! We haven't seen you in a while and there are some fabulous new games to try out. Click here to claim ", user, 90, createTag(Name), createPromoCode(Name, user, inactivity), Name)
                            .withReward("363526a3-1fb1-499d-bb33-66dd9dcb9259");      //TODO: Change to correct

            }

        }

            System.out.println("    -- Campaign " + Name + " not firing. waiting "+ INACTIVITY_LIMIT+" days (last:" + lastSession.timeStamp.toString() );
            return null;



    }


    /*********************************************************************
     *
     *              Campaign timing restrictions
     *
     * @param executionTime     - time of execution
     * @return                  - messgage or null if ok.
     */

    public String testFailCalendarRestriction(Timestamp executionTime, boolean overrideTime) {

        return isTooEarly(executionTime, overrideTime);

    }



}
