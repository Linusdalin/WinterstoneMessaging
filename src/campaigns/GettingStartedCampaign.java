package campaigns;

import action.ActionInterface;
import action.EmailAction;
import action.MobilePushAction;
import action.NotificationAction;
import core.PlayerInfo;
import email.EmailInterface;
import email.NotificationEmail;
import recommendation.GameRecommender;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.User;
import response.ResponseStat;
import rewards.RewardRepository;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;


/************************************************************************'
 *
 *              The getting started campaign is sending messages to
 *              players that has only installed and played one session
 */

public class GettingStartedCampaign extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "GettingStarted";
    private static final int CoolDown_Days = 3;   // No real cool down. This will anyway only trigger once per message


    GettingStartedCampaign(int priority, CampaignState active){

        super(Name, priority, active);

        setCoolDown(CoolDown_Days);
    }

    /********************************************************************
     *
     *              Decide on the campaign
     *
     *              The output could be one of 4 different messages depending on the day
     *
     *
     *
     */

    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {


        //System.out.println("Registration Date: " + getDay(user.created).toString());
        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        if(user.amount != 0 || user.sessions > 10){

            System.out.println("    -- Campaign " + Name + " not Firing. Player already got going");
            return null;

        }

        int age = getDaysBetween(user.created, executionDay );

            if(age <= 2){

                List<GameSession> sessions = playerInfo.getSessions();

                if(sessions.size() < 2 && age < 2){

                    GameRecommender recommender = new GameRecommender(playerInfo, executionTime);
                    boolean hasTried = recommender.hasTried("os2x3x4x5x");

                    if(!hasTried){


                        if(playerInfo.getUsageProfile().isMobilePlayer()){

                            // Send a mobile push
                            System.out.println("    -- Campaign " + Name + " mobile freespin get going " + user.name );
                            return new MobilePushAction("Try a new Game. Here are some free spins for The 2x3x4x5x game", user, executionTime, getPriority(), getTag(), Name,  305, getState(), responseFactor)
                                    .withReward(RewardRepository.OS2345Rest);
                        }

                        // Send a facebook push

                        System.out.println("    -- Campaign " + Name + " fb freespin get going " + user.name );
                        return new NotificationAction("There are many new games at SlotAmerica. Here are some free spins to try out the Old School 2x3x4x5x game",
                                user, executionTime, getPriority(), getTag(),  Name, 5, getState(), responseFactor)
                                .withReward(RewardRepository.OS2345Rest);
                    }

                }

                System.out.println("    -- Campaign " + Name + " Running message 1 for " + user.name );

                if(playerInfo.getUsageProfile().isMobilePlayer()){

                    if(playerInfo.fallbackFromMobile()){

                        //TODO: Is there a point in adding an email here?
                        System.out.println("    -- Campaign " + Name + " not firing. No mobile communication." );
                        return null;
                    }

                    return new MobilePushAction("An extra diamond for every day in a row you are playing!",
                            user, executionTime, getPriority(), getTag(), Name, 301, getState(), responseFactor);
                }

                int messageId = 83;
                if(!registeredInTheMorning(user))
                    messageId = 84;

                if(randomize4(user, 2) || randomize4(user, 4)){


                    return new NotificationAction("The free coins are waiting. You get an extra diamond pick for every day in a row you are playing. Click here now!",
                            user, executionTime, getPriority(), getTag() , Name, messageId, getState(), responseFactor);

                }
                else{

                    System.out.println("    -- Campaign " + Name + " not Firing. Player not in morning test (evening)");
                    return null;

                }



            }


           if(age == 3 || age == 4){

               System.out.println("    -- Campaign " + Name + " Running message 3 for " + user.name );

               if(playerInfo.getUsageProfile().isMobilePlayer() && !playerInfo.fallbackFromMobile()){

                   return new MobilePushAction("We here at SlotAmerica are missing you! The thrilling slot machines are awaiting and you can use the FREE bonus to find your favorite game! Click here to get started",
                           user, executionTime, getPriority(), getTag(), Name, 302, getState(), responseFactor);
               }

               if(state == CampaignState.REDUCED){

                   System.out.println("    -- Campaign " + Name + " not firing. Reduced mode remove low priority messages" );
                   return null;

               }


               return new NotificationAction("We here at SlotAmerica are missing you! The thrilling slot machines are awaiting and you can use the FREE bonus to find your favorite game! Click here to get started",
                       user, executionTime, getPriority(), getTag(), Name, 2, getState(), responseFactor);

           }
           if(age == 5 || age == 6 ){

               return new EmailAction(gettingStartedEmail1(user, createPromoCode(207)), user, executionTime, getPriority(), getTag(), 207, getState(), responseFactor);

           }



           if(age == 7 || age == 8){

               if(playerInfo.getUsageProfile().isMobilePlayer() && !playerInfo.fallbackFromMobile()){

                   return new MobilePushAction("The games are waiting. Click here for your free bonus!",
                       user, executionTime, getPriority(), getTag(), Name, 303, getState(), responseFactor);
               }
           }

           if(age == 9 || age == 10){

               if(playerInfo.getUsageProfile().isMobilePlayer() && !playerInfo.fallbackFromMobile()){

                   System.out.println("    -- Campaign " + Name + " Pushing 8 day getting started message for " + user.name );
                   return new MobilePushAction("New game releases here at Slot America. Use your free bonus to try it out?",
                           user, executionTime, getPriority(), getTag(), Name, 304, getState(), responseFactor);
               }

               System.out.println("    -- Campaign " + Name + " Emailing 8 day getting started message for " + user.name );
               return new EmailAction(gettingStartedEmail1(user, createPromoCode(204)), user, executionTime, getPriority(), getTag(), 204, getState(), responseFactor);

           }

           if(age == 11 || age == 12){

               System.out.println("    -- Campaign " + Name + " Emailing 12 day getting started message for " + user.name );
               return new EmailAction(gettingStartedEmail1(user, createPromoCode(205)), user, executionTime, getPriority(), getTag(), 205, getState(), responseFactor);

           }

           if(age == 15 || age == 16){

               System.out.println("    -- Campaign " + Name + " Emailing 16 day getting started message for " + user.name );
               return new EmailAction(gettingStartedEmail2(user, createPromoCode(206)), user, executionTime, getPriority(), getTag(), 206, getState(), responseFactor);

           }

           System.out.println("    -- Campaign " + Name + " not applicable for player " + user.name + ". Timing is not correct ( day = " + getDaysBetween(user.created, executionDay) + ")" );
           return null;

    }


               public static EmailInterface gettingStartedEmail0(User user, String promoCode) {

                       return new NotificationEmail("the real deal", "<p>SlotAmerica games are the real deal. This is the old School Vegas casino fun. No fake facebook games as you may find at other places. Sometimes it is fun, sometimes it hurts, but it is <b>always thrilling!</b> :-)</p>" +
                               "<p> Why don't you come in and use your free bonus to try it out? Just click <a href=\"https://apps.facebook.com/slotAmerica/?promocode="+ promoCode+"\">here</a> to play now :-) </p>",
                               "SlotAmerica games are the real deal. This is the old School Vegas casino fun. No fake facebook games as you may find at other places. Sometimes it is fun, sometimes it hurts, but it is <b>always thrilling!" +
                                       "Why don't you come in and use four free bonus to try them?");

               }

               public static EmailInterface gettingStartedEmail1(User user, String promoCode) {

                   return new NotificationEmail("the fun still awaits you!", "<p>Don't miss out on the new game releases here at Slot America. We try to put out a new prime game for you every week and there is a new games for you to check out now!</p>" +
                           "<p> Why don't you come in and use your free bonus to try it out? Just click <a href=\"https://apps.facebook.com/slotAmerica/?promocode="+ promoCode+"\">here</a> to play now :-) </p>",
                           "Don't miss out on all the new game releases here at Slot America. We try to put out a new prime game for you every week and you have some new games to check out." +
                                   "Why don't you come in and use four free bonus to try them?");

               }

               public static EmailInterface gettingStartedEmail2(User user, String promoCode) {

                   return new NotificationEmail("join the family!", "<p>Did you know Slot America has the most loyal players on facebook? It is true! And the new games are truly amazing!</p>" +
                           "<p> To allow you to try it out again, we added <b>1000 coins EXTRA</b> free bonus to try it out! Just click <a href=\"https://apps.facebook.com/slotAmerica/?promocode="+promoCode+"&reward="+ RewardRepository.freeCoinAcitivationFree.getCode()+"\">here</a> to play now :-) </p>",
                           "Did you know Slot America has the most loyal players on facebook? It is true! We added 1000 coins EXTRA free bonus to your account to try it out");

               }

               /****************************************************************
                *
                *              Test should be exactly n calender days before reference
                *
                * @param test                   - the date to test
                * @param reference              - reference date
                * @param days                   - how many days
                * @return                       - is it on that day
                */

    public static boolean daysBefore(Timestamp test, Timestamp reference, int days) {

        reference = getDay(new Timestamp(reference.getTime() - 24*3600*1000*days));
        test = getDay(test);

        return test.equals(reference);

    }

    public static Timestamp getDay(Timestamp fecha) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime( fecha );
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return new Timestamp(calendar.getTime().getTime());

    }


    /*********************************************************************
     *
     *              Campaign timing restrictions
     *
     * @param executionTime     - time of execution
     * @return                  - messgage or null if ok.
     */

    public String testFailCalendarRestriction(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {

        return isTooEarly(executionTime, overrideTime);

    }




}
