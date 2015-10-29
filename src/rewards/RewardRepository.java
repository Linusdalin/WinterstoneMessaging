package rewards;

import output.DeliveryException;
import output.RequestHandler;
import remoteData.dataObjects.User;

/*********************************************************************************'
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-09-16
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */

public class RewardRepository {

    public static final Reward freeCoinAcitivationPaying  = new Reward("Free Coin Activation Paying", "512853e3-8389-453a-b92d-479e330414ba", 3000, true);
    public static final Reward freeCoinAcitivationFree    = new Reward("Free Coin Activation Non-Paying", "8606fa50-6c62-4c25-ba21-40600fc79d42", 1000, true);

    public static final Reward homeRun      = new Reward("Home Run", "3670d282-34a0-4337-a4f8-3a3da70a9378", 3000, true);
    public static final Reward witchesWild  = new Reward("Witches Wild", "faea4b9c-5c87-4433-89c9-a8c24ceb0968", 3000, true);

    /****************************
     *
     *          Create a reward
     *
     *

     http://slot-america-magic-box.elasticbeanstalk.com/api/rewards/aad91e9a-2b30-4bbd-ac6c-034e78531799

     {"type":"COINS","value":5000,"userCategories":[1,2,3,4,5],"groups":[],"message":"Here are your 5,000 coins, celebrating May Day with 'Ten X Pay'. Good Luck!","expires":1431361720,"id":"aad91e9a-2b30-4bbd-ac6c-034e78531799"}


     */


    /***************************************
     *
     *          Claimed rewards


     http://slot-america-magic-box.elasticbeanstalk.com/api/players/858808397491713/claimed-rewards

     {"rewardIds":["baf9b578-7011-4df0-8fb3-24d8bd39ba24","aad91e9a-2b30-4bbd-ac6c-034e78531799"]}
     */


    public static boolean hasClaimed(User user, Reward reward){

        try {

            RequestHandler requestHandler = new RequestHandler("http://www.aftonbladet.se/nyheter/krim/article21492347.ab");
            String response = requestHandler.executeGet();
            return(response.contains(reward.getCode()));

        } catch (DeliveryException e) {

            e.printStackTrace();
            return false;
        }

    }

}
