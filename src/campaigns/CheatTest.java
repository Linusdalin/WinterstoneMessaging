package campaigns;

import action.ActionInterface;
import action.GiveCoinAction;
import core.PlayerInfo;
import core.UsageProfileClassification;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.User;

import java.sql.Timestamp;
import java.util.List;


/************************************************************************'
 *
 *              Bad beat will give players some extra coins if
 *              they have had an significantly unlucky session
 *
 *
 */

public class CheatTest extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "CheatTest";
    private static final int CoolDown_Days = 0;

    private static final int CHEAT_PAYOUT = 1000;

    private static final int MAX_REMAINING_BALANCE = 3000 ;
    private int[] MessageIds = { 1,
                                201 };


    CheatTest(int priority, CampaignState active){

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
     * @param playerInfo             - the user to evaluate
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor) {

        User user = playerInfo.getUser();

        if(user.balance < 100000000){

            System.out.println("    -- Campaign " + Name + " not applicable. Min 100 million");
            return null;
        }

        if(playerInfo.getUsageProfile() == UsageProfileClassification.ANONYMOUS){

            System.out.println("    -- Campaign " + Name + " not applicable. Mobile Player");
            return null;
        }


        if(user.balance > 1000000000){

            System.out.println("    -- Campaign " + Name + " firing for a billion coins");

            long deduction = calculateAppropriateDelta(user);

            return new GiveCoinAction(deduction, user, executionTime, getPriority(), Name, 2, getState(), responseFactor);


            //return new NotificationAction( user.name +", Something went wrong this weekend. We had to remove coins that incorrectly was credited to your account. However we left some for you to play with. Sorry for this",
            //        user, executionTime, getPriority(), getTag(),  Name, 1, getState(), responseFactor)
            //        .attach(new  GiveCoinAction(deduction, user, executionTime, getPriority(), Name, 1, getState(), responseFactor));
        }



        // Check if there were activity yesterday to avoid scanning sessions unneccesarily

        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null || isDaysBefore(lastSession, executionTime, 4)){

            System.out.println("    -- Campaign " + Name + " not firing. No activity during period" );
            return null;

        }

        int payout = getCheatPayout(playerInfo);


        if(payout < 5){

            System.out.println("    -- Campaign " + Name + " payout only " + payout );
            return null;

        }

        System.out.println("    -- Campaign " + Name + " payout = " + payout + " " + (payout > 10 ? "VERY HIGH" : "") );

        long deduction = calculateAppropriateDelta(user);

        //return new NotificationAction( user.name +", Something went wrong this weekend. We had to remove coins that incorrectly was credited to your account. However we left some for you to play with. Sorry for this",
        //        user, executionTime, getPriority(), getTag(),  Name, 2, getState(), responseFactor)
        //        .attach(new GiveCoinAction(deduction, user, executionTime, getPriority(), Name, 2, getState(), responseFactor));

        return new GiveCoinAction(deduction, user, executionTime, getPriority(), Name, 2, getState(), responseFactor);


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


    private int getCheatPayout(PlayerInfo info){

        Timestamp endTime = Timestamp.valueOf("2015-12-19 00:00:00");

        int totalWager = 0;
        int totalWin = 0;
        int totalActions = 0;

        List<GameSession> sessions = info.getSessionsYesterday(endTime, 2);

        for (GameSession session : sessions) {

            if(AbstractCampaign.isDaysBefore(session.timeStamp, endTime, 2)){

                totalWager += session.totalWager;
                totalWin += session.totalWin;
                totalActions += session.spins;
            }


        }

        if(totalWager == 0)
            return 0;

        if(totalActions < 400)
            return 0;

        return totalWin/totalWager;

    }


    private long calculateAppropriateDelta(User user){

        if(user.payments == 0)
            return 1000000 - user.balance;


        if(user.amount < 100)
            return 10000000 - user.balance;

        return 30000000 - user.balance;

    }

}
