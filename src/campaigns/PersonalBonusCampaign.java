package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;

import java.sql.Timestamp;


/************************************************************************'
 *
 *                  Old players
 *
 *                  Remind old paying players that they have a personal discount bonus
 *
 */

public class PersonalBonusCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Reactivation";
    private static final int CoolDown_Days = 12;
    private static final int[] MessageIds = { 1, 2 };


    // Trigger specific config data
    private static final Timestamp LEVEL_SYSTEM_LANCH = Timestamp.valueOf("2015-08-26 00:00:00");     // The day when the level system was introduced
    private static final int LEVEL   = 100;                      // The level where players get a purchase bonus
    private static final int MIN_ACTIVITY   = 40;           // This set very high to test out potential

    private static final int DAILY_CAP   = 100;         // Max per day
    private int count = 0;


    PersonalBonusCampaign(int priority, CampaignState activation){

        super(Name, priority, activation);
        setCoolDown(CoolDown_Days);
        registerMessageIds( MessageIds );
    }


    /********************************************************************
     *
     *              Decide on the campaign
     *
     *
     *
     *
     * @param playerInfo             - the user to evaluate
     */


    public ActionInterface  evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor) {

        count++;

        if(count > DAILY_CAP){

            System.out.println("    -- Campaign " + Name + " not firing. Daily cap reached for campaign." );
            return null;

        }

        User user = playerInfo.getUser();

        if(!isPaying(user)){

            System.out.println("    -- Campaign " + Name + " not firing. Only Paying users." );
            return null;

        }

        Timestamp executionDay = getDay(executionTime);

        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }

        if(lastSession.after(LEVEL_SYSTEM_LANCH)){


            System.out.println("    -- Campaign " + Name + " not firing. Player has been active" );
            return null;

        }

        if(user.level >= 100){

            System.out.println("    -- Campaign " + Name + " firing message 1" );
            return new NotificationAction("A lot has happened here at SlotAmerica. You still have your VIP status with a personal discount on all purchases. Check out some of the new fabulous games here! ",
                    user, getPriority(), getTag(), Name, 1, getState(), responseFactor)
                    .withGame("os2x3x4x5x");

        }

        if(user.level >= 50){

            System.out.println("    -- Campaign " + Name + " firing message 1" );
            return new NotificationAction("A lot has happened here at SlotAmerica. You still have your VIP status with extra diamonds in the bonus click. Check out some of the new fabulous games here!",
                    user, getPriority(), getTag(), Name, 2, getState(), responseFactor)
                    .withGame("os2x3x4x5x");

        }

        return  null;

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
