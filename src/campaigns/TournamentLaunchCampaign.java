package campaigns;

import action.ActionInterface;
import action.EmailAction;
import action.MobilePushAction;
import core.PlayerInfo;
import email.EmailInterface;
import email.NotificationEmail;
import remoteData.dataObjects.User;
import response.ResponseStat;

import java.sql.Timestamp;


/************************************************************************'
 *
 *          Inform mobile players that the tournaments are launched on mobile
 *
 */

public class TournamentLaunchCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "Tournament";
    private static final int CoolDown_Days = 99999;            // Just once

    // Trigger specific config data
    private static final int Min_Sessions = 10;                         // Decrease this over time

    private static final int Min_Inactivity = 4;                          // This is pretty much everyone
    private static final int Max_Inactivity  = 250;
    private final String day = null;

    TournamentLaunchCampaign(int priority, CampaignState active){

        super(Name, priority, active);
        setCoolDown(CoolDown_Days);
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


        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }
        int inactivity = getDaysBetween(lastSession, executionDay);

        if(inactivity <  Min_Inactivity){

            System.out.println("    -- Campaign " + Name + " not firing. User is active (" + inactivity + " <" + Min_Inactivity + ")" );
            return null;
        }

        if(inactivity >  Max_Inactivity){

            System.out.println("    -- Campaign " + Name + " not firing. User inactive too long. (" + inactivity + " >" + Max_Inactivity + ")" );
            return null;
        }


        if(!playerInfo.getUsageProfile().isMobilePlayer()){

            System.out.println("    -- Campaign " + Name + " not firing. Only mobile players" );
            return null;

        }


        System.out.println("    -- Campaign " + Name + " firing. Sending mystery monday offer" );

        if(!playerInfo.fallbackFromMobile()){

                return new MobilePushAction("Tournament launch! Try it out", user, executionTime, getPriority(), getTag(), Name,  301, getState(), responseFactor);
        }

        return new EmailAction(tournamentLaunchEmail(user, createPromoCode(201)), user, executionTime, getPriority(), getTag(), 201, getState(), responseFactor);

    }



    public static EmailInterface tournamentLaunchEmail(User user, String promoCode) {

        return new NotificationEmail("Tournaments on SlotAmerica Mobile",
                "<p>We have now launched tournaments on SlotAmerica Mobile. Just by playing you also participate in the tournaments and can win extra coins. You climb in the leader board just by betting " +
                        "and can really jump high in positions when you hit a big win. Check out your position with the little tournament widget in the top left corner. Click on it to see more details! </p>" +
                "<p> Just click here <a href=\"http://smarturl.it/launch_slotAmerica?promoCode="+promoCode+"\"> to try it out NOW!</a></p>",
                "Hello "+ user.name+ ", we have now launched tournaments on SlotAmerica Mobile. Just by playing you also participate in the tournaments and can win extra coins. You climb in the leader board just by betting " +
                "and can really jump high in positions when you hit a big win. Check out your position with the little tournament widget in the top left corner. Click on it to see more details!");
    }




    /*********************************************************************
     *
     *              Campaign timing restrictions
     *
     * @param executionTime     - time of execution
     * @return                  - messgage or null if ok.
     */

    public String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {

        String specificWeekDay = isSpecificDay(executionTime, false, day);

        if(specificWeekDay != null)
            return specificWeekDay;


        return isTooEarly(executionTime, overrideTime);

    }



}
