package campaigns;

import action.ActionInterface;
import action.EmailAction;
import core.PlayerInfo;
import email.EmailInterface;
import email.NotificationEmail;
import remoteData.dataObjects.User;
import response.ResponseStat;
import rewards.Reward;
import rewards.RewardRepository;

import java.sql.Timestamp;


/************************************************************************'
 *
 *          Giving free spins to players that have not tried a specific game
 *
 */

public class RaFEmailCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "referAFriend";
    private static final int CoolDown_Days = 30;            // Just once per month

    private static final int DailyCap = 100;          // Just for testing now


    // Trigger specific config data
    private static final int Min_Sessions       =  30;
    private static final int Max_Age            =  50;              // inclusive
    private static final int Min_Age            =  5;              // inclusive


    private int count = 0;
    private String day = null;

    RaFEmailCampaign(int priority, CampaignState active){

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


        if(count >= DailyCap){

            System.out.println("    -- Campaign " + Name + " not applicable. Daily Count reach (" + DailyCap + ")" );
            return null;
        }

        if(user.email == null || user.email.equals("")){

            System.out.println("    -- Campaign " + Name + " not applicable. No email address" );
            return null;

        }

        if(playerInfo.getUsageProfile().isMobilePlayer()){

            System.out.println("    -- Campaign " + Name + " not firing. Not for mobile players");
            return null;
        }

        int age = getDaysBetween(user.created, executionDay );

        if(age < Min_Age || age > Max_Age){

            System.out.println("    -- Campaign " + Name + " not applicable. Age "+ age+" is outside range (" + Min_Age + " - " + Max_Age + ")" );
            return null;

        }

        if(user.sessions < Min_Sessions){

            System.out.println("    -- Campaign " + Name + " not applicable. User is not frequent enough (" + user.sessions + " < " + Min_Sessions + ")" );
            return null;

        }


        if(isHighSpender(user)){

            System.out.println("    -- Campaign " + Name + " not applicable. Not for high spenders." );
            return null;

        }


        Timestamp lastSession = playerInfo.getLastSession();
        if(lastSession == null){

            System.out.println("    -- Campaign " + Name + " not firing. No sessions for user" );
            return null;

        }




        count++;
        System.out.println("    -- Campaign " + Name + " firing." );

        if(user.payments > 0)
            return new EmailAction(getEmail(user, 1), user, executionTime, getPriority(), getTag(), 201, getState(), responseFactor);

        return new EmailAction(getEmail(user, 2), user, executionTime, getPriority(), getTag(), 202, getState(), responseFactor);

    }



    public static EmailInterface getEmail(User user, int code) {

        String link = "https://apps.facebook.com/slotamerica/?reward="+RewardRepository.rafClockwork.getCode()+"&promoCode=raf_"+code +"_"+ user.id;

        return new NotificationEmail("Want some free coins?", "<p>Do you want to get some more coins? The only thing you have to do " +
                "is to tell a friend about SlotAmerica and have him or her to sign up through your personal refer-a-friend offer link</p>" +
                "<p><b>So whats in it for me?</b></p>" +
                "<li>Once your friend signs up at SlotAmerica you will get a notification and 5,000 coins in your account.</li>" +
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
