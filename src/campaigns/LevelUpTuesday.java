package campaigns;

import action.ActionInterface;
import action.MobilePushAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;
import response.ResponseStat;

import java.sql.Timestamp;


/************************************************************************'
 *
 *
 *
 */

public class LevelUpTuesday extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "LevelUpTuesday";
    private static final int CoolDown_Days = 5;            // Just once per game

    private String reminderDay = null;


    // Trigger specific config data
    private static final int Min_Level = 3;
    private static final int Min_Sessions = 20;
    private static final int Min_Inactivity = 2;
    private static final int Max_Inactivity_Free = 10;
    private static final int Max_Inactivity_Paying = 50;

    LevelUpTuesday(int priority, CampaignState active, String day){

        super(Name, priority, active);
        setCoolDown(CoolDown_Days);
        reminderDay = day;
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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {

        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        if(user.sessions < Min_Sessions){

            System.out.println("    -- Campaign " + Name + " not applicable. User is not frequent enough (" + user.sessions + " < " + Min_Sessions + ")" );
            return null;

        }

        if(user.level < Min_Level){

            System.out.println("    -- Campaign " + Name + " not applicable. User level too low (" + user.level + " < " + Min_Level + ")" );
            return null;

        }


        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }
        int inactivity = getDaysBetween(lastSession, executionDay);

        if(inactivity <  Min_Inactivity){

            System.out.println("    -- Campaign " + Name + " not firing. User is active (" + inactivity + " >" + Min_Inactivity + ")" );
            return null;
        }

        if(inactivity >  Max_Inactivity_Free && !isPaying(user)){

            System.out.println("    -- Campaign " + Name + " not firing. User inactive too long. (" + inactivity + " >" + Max_Inactivity_Free + ")" );
            return null;
        }

        if(inactivity >  Max_Inactivity_Paying && isPaying(user)){

            System.out.println("    -- Campaign " + Name + " not firing. User inactive too long. (" + inactivity + " >" + Max_Inactivity_Paying + ")" );
            return null;
        }

        String tuesdayRestriction    = isSpecificDay(executionTime, false, reminderDay);

        if(tuesdayRestriction != null){

            System.out.println("    -- Campaign " + Name + " not firing. Wrong day.");
            return null;

        }

        System.out.println("    -- Sending level up reminder " );

        if(playerInfo.getUsageProfile().hasTriedMobile()){

            return new MobilePushAction("All Level XP is boosted today to allow you to level up faster. Click here to start!",
                    user, executionTime, getPriority(), getTag(), Name,  31, getState(), responseFactor);

        }


        return null;  // Not supported on canvas yet.

        //return new NotificationAction("Today is Level-up day! All Level XP is boosted to allow you to level up faster",
        //        user, executionTime, getPriority(), getTag(),  Name, 1, getState(), responseFactor)
        //;

    }


    /*********************************************************************
     *
     *              Campaign timing restrictions
     *
     * @param executionTime     - time of execution
     * @return                  - messgage or null if ok.
     */

    public String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {

        String specificWeekDay = isSpecificDay(executionTime, false, reminderDay);

        if(specificWeekDay != null)
            return specificWeekDay;



        return isTooEarly(executionTime, overrideTime);

    }


}
