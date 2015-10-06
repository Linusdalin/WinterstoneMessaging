package campaigns;

import action.ActionInterface;
import action.ManualAction;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;

import java.sql.Timestamp;


/************************************************************************'
 *
 *                  addressing old players who still have
 *                  coins left on the account
 *
 *                  This fires 10 days after the last action
 */

public class FakeCoinsLeftCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "FakeCoinsLeft";
    private static final int CoolDown_Days = 36500;     // Only once per player
    private int[] MessageIds = { 1 };


    // Trigger specific config data
    private static final int INACTIVITY_LIMIT   = 13;   // 13 days inactivity before kicking in this offer


    private static final int COINS_MIN                       = 1100;
    private static final int COINS_MAX                       = 15000;
    private static final int COINS_FOR_LOW_SPENDER           = 15000;
    private static final int COINS_FOR_HIGH_SPENDER          = 20000;


    FakeCoinsLeftCampaign(int priority, CampaignState activation){

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
     *
     *
     * @param playerInfo             - the user to evaluate
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime) {

        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        if(!isPaying(user)){

            System.out.println("    -- Campaign " + Name + " not firing. Only paying users" );
            return null;
        }

        if(user.balance < COINS_MIN){

            System.out.println("    -- Campaign " + Name + " not firing. No money on the account" );
            return null;
        }


        if(user.balance > COINS_MAX){

            System.out.println("    -- Campaign " + Name + " not firing. Too much money on the account" );
            return null;
        }


        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }
        int inactivity = getDaysBetween(lastSession, executionDay);

        if(inactivity == INACTIVITY_LIMIT){

            int newCoinBalance = 0;

            if(isHighSpender( user ))

                newCoinBalance = createBalance(user.balance, COINS_FOR_HIGH_SPENDER);
            else
                newCoinBalance = createBalance(user.balance, COINS_FOR_LOW_SPENDER);


            // Give the player some coins and tell him/her that there are coins left.
            // Attach a manual action to credit the difference


                System.out.println("    -- Campaign " + Name + " firing upping balance from " + user.balance + " to " + newCoinBalance );


                return new NotificationAction("Don't forget you have "+ newCoinBalance+" coins left on your account. There are some fabulous new games you can try out with it ",
                        user, getPriority(), getTag(),  Name, 1, getState())

                        .attach(new ManualAction("Credit user with " + (newCoinBalance-user.balance) + " coins.", user, getPriority(), Name, 1, getState()));


            }

            System.out.println("    -- Campaign " + Name + " not firing. waiting "+ INACTIVITY_LIMIT+" days (last:" + lastSession.toString() );
            return null;


    }

    private int createBalance(int balance, int coinsForHighSpender) {

        int noise = (int)((Math.random() * 6) - 2) * 500;

        return balance + coinsForHighSpender + noise;

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
