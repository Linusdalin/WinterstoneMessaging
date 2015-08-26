package campaigns;

import action.ActionInterface;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.User;

import java.sql.Timestamp;


/************************************************************************'
 *
 *              Sending a message to players that are close to levelling up
 *
 *              This is a simple first take, sending a message 3 to all players that have played at lease 10 sessions and then stopped
 *
 */

public class LevelUpCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Level-Up";
    private static final int CoolDown_Days = 50;      // This should really be once per level. Especially when players are not playing too much

    // Trigger specific config data
    private static final int Level_up_halfWay   = 25;
    private static final int Level_up_50        = 48;
    private static final int Level_up_100       = 97;

    LevelUpCampaign(int priority){

        super(Name, priority);
        setCoolDown(CoolDown_Days);
    }


    /********************************************************************
     *
     *              Decide on the message
     *
     *              This actually will fire every time, but it is relying on the
     *              cool down not to repeat the message
     *
     *
     *
     * @param playerInfo             - the user to evaluate
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime) {


        //System.out.println("Registration Date: " + getDay(user.created).toString());
        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();
        String message;

        if(user.level == Level_up_halfWay)
            message = "You reached level 25! Congratulations. You are halfway to the level 50 bonus!";
        else if(user.level == Level_up_50)
            message = "You are getting close to the level 50 bonus!";
        else if(user.level == Level_up_100)
            message = "You are getting close to the level 100 bonus!";
        else{

            System.out.println("    -- Campaign " + Name + " not firing. Not applicable level " + user.level );
            return null;

        }



        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }

        int inactivation = getDaysBetween(lastSession, executionTime);
        if(inactivation <= 1){


            System.out.println("    -- Player is active. Not sending a message" );
            return null;

        }
        else if(inactivation > 10){


            System.out.println("    -- Player is NOT active. Not sending a message" );
            return null;

        }
        else{


                System.out.println("    -- Campaign " + Name + " firing message for level " + user.level );
                return new NotificationAction(message, user, getPriority(), createTag(Name), createPromoCode(Name, user, inactivation), Name);


        }
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
