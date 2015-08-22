package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.User;

import java.sql.Timestamp;


/************************************************************************'
 *
 *                  addressing old players who still have
 *                  coins left on the account
 *
 *                  This fires 10 days after the last action
 */

public class CoinsLeftCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Coins Left";
    private static final int CoolDown_Days = 36500;     // Only once per player

    // Trigger specific config data
    private static final int INACTIVITY_LIMIT   = 10;   // 10 days inactivity before kicking in this offer


    private static final int COINS_FOR_FREE_PLAYER           = 5000;
    private static final int COINS_FOR_LOW_SPENDER           = 15000;
    private static final int COINS_FOR_HIGH_SPENDER          = 20000;


    CoinsLeftCampaign(){

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

        if(inactivity == INACTIVITY_LIMIT){

            // Get the players on the day

            if(isHighSpender( user )){

                if(user.balance > COINS_FOR_HIGH_SPENDER){

                    System.out.println("    -- Campaign " + Name + " firing for high spender with balance " + user.balance );
                    return new NotificationAction("You have "+ user.balance+" coins left on your account. There are some fabulous new games you can try out with it ", user, 70, createTag(Name), createPromoCode(Name, user, inactivity), Name);

                }
                else
                    System.out.println("    -- Campaign " + Name + " not firing for high spender with balance " + user.balance );



            }else if(isLowSpender(user)){

                if(user.balance > COINS_FOR_LOW_SPENDER){

                    System.out.println("    -- Campaign " + Name + " firing for low spender with balance " + user.balance );
                    return new NotificationAction("You have "+ user.balance+" coins left on your account. There are some fabulous new games you can try out with it ", user, 70, createTag(Name), createPromoCode(Name, user, inactivity), Name);

                }
                else
                    System.out.println("    -- Campaign " + Name + " not firing for low spender with balance " + user.balance );

            }else if(user.sessions > 40){

                if(user.balance > COINS_FOR_FREE_PLAYER){

                    System.out.println("    -- Campaign " + Name + " firing for free player with balance " + user.balance );
                    return new NotificationAction("You have "+ user.balance+" coins left on your account. There are some fabulous new games you can try out with it ", user, 70, createTag(Name), createPromoCode(Name, user, inactivity), Name);

                }
                else
                    System.out.println("    -- Campaign " + Name + " not firing for free player with balance " + user.balance );

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
