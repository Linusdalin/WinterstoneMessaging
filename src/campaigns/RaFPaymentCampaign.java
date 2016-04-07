package campaigns;

import action.ActionInterface;
import core.PlayerInfo;
import email.EmailInterface;
import email.NotificationEmail;
import raf.RAFHandler;
import remoteData.dataObjects.User;
import response.ResponseStat;
import rewards.Reward;
import rewards.RewardRepository;

import java.sql.Connection;
import java.sql.Timestamp;


/************************************************************************'
 *
 *          Giving free spins to players that have not tried a specific game
 *
 */

public class RaFPaymentCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "referAFriendPayment";
    private static final int CoolDown_Days = 0;            // No cooldown

    private static final int Max_Age = 5;       // Ony look 5 days back. (Just for optimization)

    private String day = null;

    RaFPaymentCampaign(int priority, CampaignState active){

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

        Connection connection = playerInfo.getConnection();


        RAFHandler rafHandler = new RAFHandler(user, connection);
        User rafParent = rafHandler.getRAFParent(user);
        ActionInterface action = null;
        int coins = 0;

        if(rafParent == null){

            System.out.println("    -- Campaign " + Name + " not firing. No RaF parent");
            return action;
        }

        if(user.payments == 1){

            // Check for first payment

            if(rafHandler.isFirstPayment()){

                coins = rafHandler.getCoinsForPayment();
            }

        }



        int age = getDaysBetween(user.created, executionDay );
        if(age < Max_Age){


            if(rafHandler.isNewRegistration()){

                System.out.println("    -- Campaign " + Name + " firing. New RAF registration" );

                if(coins == 0){

                    coins = rafHandler.getCoinsForRegistration();
                    //TODO: Action for new registration
                    //TODO: Action for Other player!!
                }
                else{

                    coins += rafHandler.getCoinsForRegistration();
                    //TODO: Action for new registration with payment
                    //TODO: Action for Other player!!
                }


            }

        }

        if(coins > 0){

            //TODO: Add reward

        }

        return action;
    }



    public static EmailInterface getEmail(User user, int code) {

        String link = "https://apps.facebook.com/slotamerica/?reward="+RewardRepository.rafClockwork.getCode()+"&promoCode=raf_"+code +"_"+ user.facebookId;

        return new NotificationEmail("Want some free coins?", "<p>Do you want to get some more coins? The only thing you have to do " +
                "is to tell a friend about SlotAmerica and have him or her to sign up through your personal refer-a-friend offer link</p>" +
                "<p><b>So whats in it for me?</b></p>" +
                "<li>Once your friend signs up at SlotAmerica you will get a notification and 5000 coins in your account.</li>" +
                "<li>When your friend makes the first purchase, we will match that purchase and give you the same amount of coins. (If he or she buys 15,000 coins, you will also get 15,000 coins</li>" +
                "<p><b>How do I do it?</b></p>" +
                "<p>To be eligible for the bonus, your friend has to register through your personal link: <a href=\""+link+"\">"+link+"</a> The offer is only available for friends that have not played SlotAmerica before and only once per friend.</p>" +
                "<p><b>What's in it for the friend?</b></p>" +
                        "<p>Your personal link contains 20 free spins on one of our favourite games Clockwork for the friend. These are only available when registering through your personal link. " +
                        "(You can test it out and you will actually  get the free spins yourself, but of course no refer-a-friend bonuses, you can't recruit yourself... :-)  )</p>" +
                "<p><b>Note this</b></p>" +
                        "<p>Do not send this out to people you do not know. It is considered spamming and not nice. This offer is only intended for communication between friends. Also note that your friends do not " +
                        "necessarily share your passion for slots, so always use common sense. ",
                        "Do you want to get some more coins? The only thing you have to do is to tell a friend about SlotAmerica and have him or her to sign up through your personal link: " + link +
                                "Do not send this out to people you do not know. It is considered spamming and not nice. This offer is only intended for communication between friends.");
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

    protected Reward decideReward(User user) {

        if(isHighSpender(user))
            return RewardRepository.OS2345High;

        if(isPaying(user))
            return RewardRepository.OS2345Paying;

        if(isFrequent(user))
            return RewardRepository.OS2345Frequent;

        return RewardRepository.OS2345Rest;
    }




}
