package campaigns;

import action.ActionInterface;
import action.EmailAction;
import action.MobilePushAction;
import action.NotificationAction;
import core.PlayerInfo;
import email.EmailInterface;
import email.NotificationEmail;
import remoteData.dataObjects.User;
import response.ResponseStat;

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
    private static final String Name = "LevelUp";
    private static final int CoolDown_Days = 11;      // This should really be once per level. Especially when players are not playing too much

    // Trigger specific config data

    private static final int Level_up_started       = 10;
    private static final int Level_up_20            = 20;
    private static final int Level_up_close         = 23;
    private static final int Level_up_2500          = 25;
    private static final int Level_up_50            = 48;
    private static final int Level_up_100           = 97;
    private static final int Level_up_150           = 148;
    private static final int Level_up_200           = 198;


    public static final String[] messages = {
            "You are moving up the levels. Already at 10! Don't forget to check out the bonuses you get by levelling up at Slot America! Click here!",
            "You reached level 20! Click here for a surprise free coin reward",
            "You are getting close to the level 50 bonus! The diamond bonus baseline will give you more free coins. Click here for a final push...",
            "You are getting close to the level 100 bonus with a personal permanent coin discount Click here for a final push...",
            "You are getting close to the level 150 bonus! The new diamond bonus baseline will give you even more free coins. Click here for a final push...",
            "You are getting close to the level 200 bonus with an INCREASED personal permanent coin discount Click here for a final push...",
            "You closing in on level 25! There will be a little extra bonus at 25. Click here to reach the final stage.",
            "You reached level 25! Congratulations. Click here for a little surprise bonus!",
    };



public LevelUpCampaign(int priority, CampaignState activation){

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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {


        User user = playerInfo.getUser();
        int message;

        if(user.level == Level_up_started){
            message = 0;
        }else if(user.level == Level_up_20){
            message = 1;
        }else if(user.level == Level_up_close){
            message = 6;
        }else if(user.level == Level_up_2500){
            message = 7;
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

        if(inactivation > 10){


            System.out.println("    -- Player is NOT active. Not sending a message" );
            return null;

        }


            System.out.println("    -- Campaign " + Name + " firing message for level " + user.level + " with message " + message + 1  );

        ActionInterface action;
        String reward = null;

        if(message == 1){

            reward = "0fcb000c-c417-429f-bf2b-4d9a5f5ccff7";
        }
        if(message == 7){

            reward = "9282b539-40b0-4744-a793-2e022bfd85a8";
        }
        if(playerInfo.getUsageProfile().isMobilePlayer()){

             if(playerInfo.fallbackFromMobile() && message != 7){

                 return new EmailAction(getLevelUpEmail(messages[message], user.level, reward), user, executionTime, getPriority(), getTag(), (message + 201), getState(), responseFactor);
             }


             return new MobilePushAction(messages[message], user, executionTime, getPriority(), getTag(), Name,  (message + 301), getState(), responseFactor);
        }
        else{

             action =  new NotificationAction(messages[message], user, executionTime, getPriority(), getTag(),  Name, (message + 1), getState(), responseFactor);

        }

        if(reward != null)
            action.withReward(reward);



        return action;

    }

    public static EmailInterface getLevelUpEmail(String message, int level, String reward) {

        String link = " <a href=\"http://smarturl.it/launch_slotamerica"+(reward != null ? "reward=" +reward : "")+"\"> Play! </a> ";

        return new NotificationEmail("Congratulation, you reached level " + level, "<p>"+ message + link +"</p>",
                message + link);
    }

        /*********************************************************************
        *
        *              Campaign timing restrictions
        *
        *              - Adding special restriction of thursday and friday
        *
        * @param executionTime     - time of execution
        * @return                  - messgage or null if ok.
        */

    public String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {

        // No time restriction here. Send both morning and evening

        return isTooEarly(executionTime, overrideTime);
    }



}
