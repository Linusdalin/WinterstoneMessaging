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

    public static final Reward freeCoinAcitivation  = new Reward("Free Coin Activation Paying", "512853e3-8389-453a-b92d-479e330414ba", 3000, true);
    public static final Reward freeCoinAcitivationFree    = new Reward("Free Coin Activation Non-Paying", "8606fa50-6c62-4c25-ba21-40600fc79d42", 1000, true);

    public static final Reward homeRun              = new Reward("Home Run",        "3670d282-34a0-4337-a4f8-3a3da70a9378", 3000, true);
    public static final Reward witchesWild          = new Reward("Witches Wild",    "faea4b9c-5c87-4433-89c9-a8c24ceb0968", 3000, true);
    public static final Reward veteransWarmup       = new Reward("Veterans Warmup", "d7eeb526-8527-4606-888c-c10fd31f0081", 2000, true);
    public static final Reward veterans             = new Reward("Veterans day",    "3addaa32-8cbf-4e60-b7cd-ae2d9c87c8ab", 2000, true);
    public static final Reward clockwork            = new Reward("Clockwork",       "aa4f3a90-8383-4800-b745-883761344b1e", 2000, true);
    public static final Reward bellsFreespin        = new Reward("Bells",           "354b5b4d-48ef-49a8-9529-5535ecbc87c9", 5, true);
    public static final Reward hohoho10             = new Reward("HOHOHO",          "4c7eca6e-963a-47fd-af22-5d3644a178fd", 10, true);    //100
    public static final Reward burningSevens12      = new Reward("Burning",         "5431fc11-ac4c-4bde-b313-6821a7d396c1", 12, true);    //250
    public static final Reward doublePay12          = new Reward("Double Pay",      "12ecb802-9e2a-4a54-916c-30753e8886c8", 12, true);    //250

    public static final Reward mobileTest           = new Reward("Mobile T",        "ce48dc8c-4bc3-4835-8e41-27510285f857", 7777, true);
    public static final Reward mobile1              = new Reward("Mobile 1",        "ac805f4b-630f-41a2-b73d-addd750b1c11", 5000, true);
    public static final Reward mobileFrequent       = new Reward("Mobile Frequent", "309debd1-6e66-4734-8c65-fa806785dd0d", 8000, true);
    public static final Reward mobilePaying         = new Reward("Mobile Paying",   "5509ff05-bba2-45f5-8973-ee64b6f3c4fa", 10000, true);
    public static final Reward mobileHighRoller     = new Reward("Mobile High",     "4898cd43-02b0-49f6-af17-c220acacd3a6", 15000, true);

    public static final Reward OS2345High           = new Reward("OS High",         "b40a14c3-6738-4d3b-b78c-1d4ecddd8410", 25, true);    //500
    public static final Reward OS2345Paying         = new Reward("OS Paying",       "57894573-63fd-4044-a70f-ad60ec7b7788", 15, true);    //250
    public static final Reward OS2345Frequent       = new Reward("OS Fequent",      "31893e9c-bffe-4270-a281-22dd1743540f", 10, true);    //250
    public static final Reward OS2345Rest           = new Reward("OS Rest",         "4c1c1486-3841-444a-a7e5-d1950387d2da", 10, true);    //100


    public static final Reward ClockworkHigh           = new Reward("Clockwork High",         "3f82db71-9a79-4633-a29c-a14b9bfb698b", 25, true);    //500
    public static final Reward ClockworkPaying         = new Reward("Clockwork Paying",       "293b1f9b-9f9b-479d-b12b-30f47b1939dd", 15, true);    //250
    public static final Reward ClockworkFrequent       = new Reward("Clockwork Frequent",     "8b86ede9-11af-49cd-9987-ba733628e598", 10, true);    //250
    public static final Reward ClockworkRest           = new Reward("Clockwork Rest",         "7da67404-d01a-43ab-b17c-37a82e145497", 10, true);    //100

    public static final Reward SonicHigh           = new Reward("Sonic High",         "f0c53168-4639-415e-8fec-0c3de5442373", 25, true);    //500
    public static final Reward SonicPaying         = new Reward("Sonic Paying",       "b008fc89-4fb8-4f26-b5f0-f1482de0f5d6", 15, true);    //250
    public static final Reward SonicFrequent       = new Reward("Sonic Frequent",     "eca15263-d3bc-49d4-8309-2b2566713486", 10, true);    //250
    public static final Reward SonicRest           = new Reward("Sonic Rest",         "cfa54e21-3753-490e-a385-255444f68088", 10, true);    //100


    public static final Reward newYearPaying        = new Reward("NewYear1",        "0338c05d-066e-454a-87ad-fde52ece0d79",  5000, true);
    public static final Reward newYearFree          = new Reward("NewYear2",        "bc9d1bd9-08f6-48ab-b623-917c5593b4cd",  3000, true);


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


    // TODO: Improve this. Cache locally as this is now requested multiple times for all campaigns


    public static boolean hasClaimed(User user, Reward reward){

        try {

            RequestHandler requestHandler = new RequestHandler("https://data-warehouse.slot-america.com/api/players/"+user.facebookId+"/claimed-rewards/")
                    .withBasicAuth("5b09eaa11e4bcd80800200c", "X");
            String response = requestHandler.executeGet();

            System.out.println(" - Checking: " + reward.getCode());
            System.out.println(" - Got: " + response);
            return(response.indexOf(reward.getCode()) >= 0);

        } catch (DeliveryException e) {

            e.printStackTrace();
            return false;
        }

    }

}
