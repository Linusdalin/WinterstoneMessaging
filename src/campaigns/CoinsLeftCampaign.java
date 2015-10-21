package campaigns;

import action.ActionInterface;
import email.AbstractEmail;
import action.EmailAction;
import action.NotificationAction;
import core.PlayerInfo;
import email.EmailInterface;
import email.NotificationEmail;
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
    private static final int CoolDown_Days = 6;     // Only once per player
    private int[] MessageIds = {1, 2, 3};


    // Trigger specific config data
    private static final int INACTIVITY_LIMIT   = 10;   // 10 days inactivity before kicking in this offer
    private static final int INACTIVITY_LIMIT2   = 25;   // 25 days inactivity before stopping


    private static final int COINS_FOR_FREE_PLAYER           = 4500;
    private static final int COINS_FOR_LOW_SPENDER           = 15000;
    private static final int COINS_FOR_HIGH_SPENDER          = 20000;


    CoinsLeftCampaign(int priority, CampaignState active){

        super(Name, priority, active);
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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor) {


        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        if(!isFrequent(user) && !isPaying(user)){

            System.out.println("    -- Campaign " + Name + " not firing. Not active" );
            return null;
        }


        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }
        int inactivity = getDaysBetween(lastSession, executionDay);

        if(inactivity >= INACTIVITY_LIMIT && inactivity < INACTIVITY_LIMIT2){

            if(!isOkDay(playerInfo, executionTime)){

                System.out.println("    -- Campaign " + Name + " not firing. Waiting for the right day");
                return null;

            }

            if(isHighSpender( user )){

                if(user.balance > COINS_FOR_HIGH_SPENDER){

                    System.out.println("    -- Campaign " + Name + " firing for high spender with balance " + user.balance );
                    return new NotificationAction("SlotAmerica is open for business. You have "+ user.balance+" coins left on your account. Click here to enjoy them in the Casino ",
                            user, getPriority(), getTag(), Name, 1, getState(), responseFactor)
                    .attach(new EmailAction(coinLeftEmail(user),
                            user, getPriority(), Name, 1, getState(), responseFactor));

                }
                else
                    System.out.println("    -- Campaign " + Name + " not firing for high spender with balance " + user.balance );



            }else if(isPaying(user)){

                if(user.balance > COINS_FOR_LOW_SPENDER){

                    System.out.println("    -- Campaign " + Name + " firing for low spender with balance " + user.balance );
                    return new NotificationAction("SlotAmerica is open for business. You have "+ user.balance+" coins left on your account. Click here to enjoy them in the Casino ",
                            user, getPriority(), getTag(), Name, 2, getState(), responseFactor)
                            .attach(new EmailAction(coinLeftEmail(user),
                                    user, getPriority(), Name, 1, getState(), responseFactor));

                }
                else
                    System.out.println("    -- Campaign " + Name + " not firing for low spender with balance " + user.balance );

            }else if(isFrequent(user)){

                if(user.balance > COINS_FOR_FREE_PLAYER){

                    System.out.println("    -- Campaign " + Name + " firing for free player with balance " + user.balance );
                    return new NotificationAction("You have "+ user.balance+" coins left on your account. There are some fabulous new games you can try out with it ",
                            user, getPriority(), getTag(), Name, 3, getState(), responseFactor);

                }
                else
                    System.out.println("    -- Campaign " + Name + " not firing for free player with balance " + user.balance );

            }

        }

            System.out.println("    -- Campaign " + Name + " not firing. waiting " + INACTIVITY_LIMIT + " days (last:" + lastSession.toString());
            return null;



    }

    private EmailInterface coinLeftEmail(User user) {
        return new NotificationEmail("there is more fun awaiting you", "<p>Did you know you have <b>"+ user.balance+"</b> coins left on your account? It would be a shame to let them go to waste, right?</p>" +
                "<p> There are some new and fabulous new games you can try out with it! Like <a href=\"https://apps.facebook.com/slotAmerica/?game=wild_cherries&promocode=coinsLeftEmail-1\">Wild Cherries</a>. Welcome back to test it out :-) </p>",
                "You have "+ user.balance+ " coins left on your account.There are some fabulous new games you can try out with it.");
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
