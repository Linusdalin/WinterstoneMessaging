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
 *              // TODO: Add a "not exposed to this message" as a criteria to reduce the cooldown
 *
 *
 */

public class LevelUpCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Level-Up";
    private static final int CoolDown_Days = 14;      // This should really be once per level. Especially when players are not playing too much

    // Trigger specific config data

    private static final int Level_up_started   = 10;
    private static final int Level_up_halfWay   = 25;
    private static final int Level_up_50        = 48;
    private static final int Level_up_100       = 97;
    private static final int Level_up_150       = 148;
    private static final int Level_up_200       = 198;


    private static final String[] messages = {
            "You are moving up the levels. Already at 10! Don't forget to check out the bonuses you get by levelling up at Slot America!",
            "You reached level 25! Congratulations. You are halfway to the level 50 bonus!",
            "You are getting close to the level 50 bonus! The diamond bonus baseline will give you more free coins. Click here for a final push...",
            "You are getting close to the level 100 bonus with a personal permanent coin discount Click here for a final push...",
            "You are getting close to the level 150 bonus! The new diamond bonus baseline will give you even more free coins. Click here for a final push...",
            "You are getting close to the level 200 bonus with an INCREASED personal permanent coin discount Click here for a final push...",
    };



LevelUpCampaign(int priority, CampaignState activation){

        super(Name, priority, activation);
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
        int message;

        if(user.level == Level_up_started){
            message = 0;
        }else if(user.level == Level_up_halfWay){
            message = 1;
        }else if(user.level == Level_up_50){
            message = 2;
        }else if(user.level == Level_up_100){
            message = 3;
        }else if(user.level == Level_up_150){
            message = 4;
        }else if(user.level == Level_up_200){
            message = 5;
        }else{

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
                return new NotificationAction(messages[message], user, getPriority(), createTag(Name),  Name, (message + 1), getState());


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
