package campaigns;

import action.ActionInterface;
import action.EmailAction;
import action.NotificationAction;
import core.PlayerInfo;
import email.EmailInterface;
import email.NotificationEmail;
import recommendation.GameRecommendation;
import recommendation.GameRecommender;
import remoteData.dataObjects.Payment;
import remoteData.dataObjects.User;

import java.sql.Timestamp;


/************************************************************************'
 *
 *              Sending a message to players who made a first payment
 *
 */

public class FirstPaymentCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "GameActivationPoke";
    private static final int CoolDown_Days = 9;

    // Trigger specific config data
    private static final int Min_Sessions = 2;

    private static final String GroupA = "A";        // For A/B testing
    private static final String GroupB = "B";        // For A/B testing

    FirstPaymentCampaign(int priority, CampaignState active){

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


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime) {

        User user = playerInfo.getUser();

        if(user.sessions < Min_Sessions){

            System.out.println("    -- Campaign " + Name + " not applicable. User is not frequent enough (" + user.sessions + " < " + Min_Sessions + ")" );
            return null;

        }

        if(user.payments != 1){

            System.out.println("    -- Campaign " + Name + " not applicable. Player has made " + user.payments + " payments" );
            return null;

        }

        if(!user.group.equalsIgnoreCase(GroupA) && !user.group.equalsIgnoreCase(GroupB)){

            System.out.println("    -- Campaign " + Name + " ignoring for A/B test" );
            return null;

        }

        Payment payment = playerInfo.getLastPayment();

        if(isDaysBefore(payment.timeStamp, executionTime, 1)){

            // Sending a mail instead

            System.out.println("    -- Sending an nice VIP welcome EMAIL to new paying players\n" );
            return new EmailAction(firstDepositEmail(user, payment), user, getPriority(), getTag(), 2, getState());



        }

        System.out.println("    -- Campaign " + Name + " not applicable. No payment yesterday");
        return null;

    }

    public static EmailInterface firstDepositEmail(User user, Payment payment) {

        return new NotificationEmail("welcome to the SlotAmerica family!", "<p> Thank you for your coin purchase. " +

                "This is what keeps us working long days and nights bringing you the best slot games on facebook. You are now part of supporting this!</p>" +
                "<p> We always strive to give you the best and most genuine slot experience possible. That's why we do not offend our " +
                    "slots loving players with the kind of fake machines that are too common on facebook. With SlotAmerica you know you get the real deal!</p>" +

                (payment.amount >=24 ?
                "<p> As with any Casino, our most loyal players are always rewarded. If you feel you would qualify in our VIP program, please let us know immediately " +
                "at <a href=\"mailto://contact@slot-america.com?subject=VIP\">VIP host service</a> and we can set you up on an individual plan for all your further purchases.</p> " +
                    "slots loving players with the kind of fake machines that are too common on facebook. With SlotAmerica you know you get the real deal!</p>"
                        :""
                ) +
                "<p> Sometimes things go wrong! If there is any problem, please let us know. You can always email support@slot-america.com and we will do our " +
                    "best to help you out. We are of course also interested to hear what you think about the games and other functionality. Just send an email to support.</p>",
                "Hello "+ user.name+" Thank you for your coin purchase. " +
                "This is what keeps us working long days and nights bringing you the best slot games on facebook. You are now part of supporting this!\n\n" +
                "We always strive to give you the best and most genuine slot experience possible. That's why we do not offend our " +
                "slots loving players with the kind of fake machines that are too common on facebook. With SlotAmerica you know you get the real deal!\n\n" +
                        (payment.amount >=24 ?

                                "As with any Casino, our most loyal players are always rewarded. If you feel you would qualify in our VIP program, please let us know immediately " +
                "at contact@slot-america.com and we can set you up on an individual plan for all your further purchases.\n\n "
                                : "")+
                "slots loving players with the kind of fake machines that are too common on facebook. With SlotAmerica you know you get the real deal!\n\n" +
                "Sometimes things go wrong! If there is any problem, please let us know. You can always email support@slot-america.com and we will do our " +
                "best to help you out. We are of course also interested to hear what you think about the games and other functionality. Just send an email to support.\n\n");
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