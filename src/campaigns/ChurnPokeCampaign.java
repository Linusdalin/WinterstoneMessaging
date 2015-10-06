package campaigns;

import action.ActionInterface;
import action.EmailAction;
import action.NotificationAction;
import core.PlayerInfo;
import email.EmailInterface;
import email.NotificationEmail;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.User;

import java.sql.Timestamp;


/************************************************************************'
 *
 *              Sending a message to players that stop playing
 *
 *              This is a simple first take, sending a message 3 to all players that have played at lease 10 sessions and then stopped
 *
 */

public class ChurnPokeCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "ChurnPoke";
    private static final int CoolDown_Days = 5;
    private int[] MessageIds = {1, 2, 3, 4};


    // Trigger specific config data
    private static final int Min_Sessions = 8;

    ChurnPokeCampaign(int priority, CampaignState active){

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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime) {

        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        if(user.sessions < Min_Sessions){

            System.out.println("    -- Campaign " + Name + " not applicable. User is not frequent enough (" + user.sessions + " < " + Min_Sessions + ")" );
            return null;

        }

        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }

        int idleDays = getDaysBetween(lastSession, executionDay);

        if(idleDays == 3 || idleDays == 4){


            System.out.println("    -- Sending a three day churn warning poke" );
            return new NotificationAction("Hello, you have new bonuses to collect at Slot America. Click here for more free slot FUN!",
                    user, getPriority(), getTag(),  Name, 1, getState());


        }
        else if(idleDays == 9 || idleDays == 10){



            System.out.println("    -- Sending a NINE day churn warning poke" );
            return new NotificationAction("Hello, don't miss out the latest slot game release at SlotAmerica. Click here to check it out!",
                    user, getPriority(), getTag(),  Name,  2, getState());


        }
        else if(idleDays == 14 || idleDays == 15){


            System.out.println("    -- Sending a Fifteen day churn warning poke" );
            return new NotificationAction("Hello, there are some new exiting releases at Slot America. Click here to check it out!",
                    user, getPriority(), getTag(),  Name,  3, getState());


        }
        else if(idleDays > 10 && idleDays < 27){


            System.out.println("    -- Sending a churn warning poke mail" );

                    return new EmailAction(churnPokeEmail(user), user, getPriority(), getTag(), 10, getState());

        }
        else if(idleDays >= 27 || idleDays < 31){



            System.out.println("    -- Sending a final day churn warning poke" );
            return new NotificationAction("It is party time at SlotAmerica today. Click here to check it out!",
                    user, getPriority(), getTag(),  Name,  4, getState());


        }
        else{

            System.out.println("    -- Campaign " + Name + " not firing. Not three day churn warning (last:" + lastSession.toString() );
            return null;

        }

    }


    private EmailInterface churnPokeEmail(User user) {
        return new NotificationEmail("where did you go", "<p>Don't miss out on all the new game releases here at Slot America. We try to put out a new prime game for you every week and you have some new games to check out!</p>" +
                "<p> Why don't you come in and use your free bonus to try them? Click <a href=\"https://apps.facebook.com/slotAmerica/?promocode=EcoinsLeft-30\">here</a> to test it out :-) </p>",
                "Hello "+ user.name+" Don't miss out on all the new game releases here at Slot America. We try to put out a new prime game for you every week and you have some new games to check out." +
        "Why don't you come in and use your free bonus to try them?");
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
