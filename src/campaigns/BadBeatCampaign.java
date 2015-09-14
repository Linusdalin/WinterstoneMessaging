package campaigns;

import action.ActionInterface;
import action.ManualAction;
import action.NotificationAction;
import core.PlayerInfo;
import core.Yesterday;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.User;

import java.sql.Timestamp;


/************************************************************************'
 *
 *              Bad beat will give players some extra coins if
 *              they have had an significantly unlucky session
 *
 *              //TODO: Automatically credit coins
 *
 */

public class BadBeatCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Bad Beat";
    private static final int CoolDown_Days = 10;

    // Trigger specific config data
    private static final int MIN_AVERAGE_BET = 490;
    private static final int MIN_ACTIONS = 130;
    private static final int LONG_SESSION = 350;
    private static final int VERY_LONG_SESSION = 700;

    private static final int VERY_UNLUCKY_PAYOUT = 60;
    private static final int UNLUCKY_PAYOUT = 75;
    private static final int BAD_PAYOUT = 80;

    private static final int MAX_REMAINING_BALANCE = 2000 ;

    BadBeatCampaign(int priority, CampaignState active){

        super(Name, priority, active);
        setCoolDown(CoolDown_Days);

    }

    /********************************************************************
     *
     *              Decide on the campaign
     *
     *              The output could be one of 4 different messages depending on the day
     *
     * @param playerInfo             - the user to evaluate
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime) {

        User user = playerInfo.getUser();

        if(user.payments == 0){

            System.out.println("    -- Campaign " + Name + " not applicable. Only for paying players");
            return null;
        }

        if(user.sessions < MIN_ACTIONS){

            System.out.println("    -- Campaign " + Name + " not applicable. Not enough actions");
            return null;
        }

        // Check if there were activity yesterday to avoid scanning sessions unneccesarily

        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null || !isDaysBefore(lastSession, executionTime, 1)){

            System.out.println("    -- Campaign " + Name + " not firing. No activity yesterday" );
            return null;

        }


        Yesterday yesterdayStats = new Yesterday(playerInfo, executionTime);

        System.out.println("    -- Campaign " + Name + " payout = " + yesterdayStats.getPayout() );

        if( yesterdayStats.getActions() >= MIN_ACTIONS){

            // There are two different thresholds for shorter and longer sessions.
            if(yesterdayStats.getPayout() < VERY_UNLUCKY_PAYOUT ||
                    (yesterdayStats.getPayout() < UNLUCKY_PAYOUT && yesterdayStats.getActions() > LONG_SESSION) ||
                    (yesterdayStats.getPayout() < BAD_PAYOUT && yesterdayStats.getActions() > VERY_LONG_SESSION)){

                if(yesterdayStats.getAverageBet() > MIN_AVERAGE_BET ){
                    if(user.balance < MAX_REMAINING_BALANCE ){

                        // Calculate a reasonable compensation and check that it is not insultingly small
                        int compensation = calculateCompensation(yesterdayStats.getAverageBet(), yesterdayStats.getActions(), yesterdayStats.getPayout());

                        if(compensation < 1000){

                            System.out.println("    -- Campaign " + Name + " Compensation "+ compensation +" is too small for " + yesterdayStats.toString() );
                            return null;

                        }

                        System.out.println("    -- Campaign " + Name + " Firing. payout = " + yesterdayStats.toString());


                        return new NotificationAction("Really Bad luck yesterday... Slots should be fun so we have added " + compensation + " coins to your account. Click here to try again!",
                                user, getPriority(), getTag(), Name, 1, getState())
                                .attach(new ManualAction("Credit user with " + compensation + " coins.", user, getPriority(), Name, 1, getState()));

                    }
                    else
                        System.out.println("    -- Campaign " + Name + " Player is unlucky, but has coins enough. Balance: " + user.balance +" > " + MAX_REMAINING_BALANCE  );
                }
                else
                    System.out.println("    -- Campaign " + Name + " Player is unlucky, but not betting enough to get bad bead compensation. Average bet " + yesterdayStats.getAverageBet() +" < " + MIN_AVERAGE_BET  );
            }
            else
                System.out.println("    -- Campaign " + Name + " Player is no unlucky enough. Payout " + yesterdayStats.getPayout() +" > " + UNLUCKY_PAYOUT  );
        }
        else
            System.out.println("    -- Campaign " + Name + " Player has not played enough to qualify for bad beat. Actions " + yesterdayStats.getActions() +" < " + MIN_ACTIONS  );

        return null;
    }


    // Repay every 10th action

    private int calculateCompensation(int averageBet, int actions, int payout) {

        if(payout > 80 || actions < 100)
            return 0;

        int betVolume = averageBet * actions;                //1539 * 154   = 237006

        int level = 90 - payout;                             // 36

        int compensation =  betVolume *level / 1000;         // 237006 * 36/100 /10 = 8532

        compensation /= 1000;
        compensation *= 1000;                                 // 8000

        return compensation;
    }


    /*********************************************************************
     *
     *              Campaign timing restrictions
     *
     * @param executionTime     - time of execution
     * @return                  - messgage or null if ok.
     */

    public String testFailCalendarRestriction(Timestamp executionTime, boolean overrideTime) {

        return null;

    }


}
