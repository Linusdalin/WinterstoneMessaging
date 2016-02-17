package rewards;

/*********************************************************************************'
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-09-16
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 *
 *
 *              TODO:
 *                      - Favourite game email with screen shots
 *                      - 17% prostitution email  (freespin and coin rewards) "three strike out"
 *
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
    public static final Reward bonusBonanza10       = new Reward("Bonus Bonanza",   "0bcd4ea2-e2b2-4cd6-8661-3d965d9dc049", 10, true);    //250
    public static final Reward eightX8              = new Reward("Eight Times Pay", "9999cec8-d3d4-4686-941e-e706c121d4b3", 8, true);     //250
    public static final Reward president            = new Reward("President",       "5629e78e-f67f-43f5-a8f5-7b6e6df1ff12", 10, true);     //250

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

    public static final Reward AbsoluteHigh           = new Reward("Absolute High",         "c5d345af-7885-42aa-b169-f09e55daaca6", 25, true);    //500
    public static final Reward AbsolutePaying         = new Reward("Absolute Paying",       "52c54892-2a17-41da-9cf0-ec167e19e94a", 15, true);    //250
    public static final Reward AbsoluteFrequent       = new Reward("Absolute Frequent",     "68244e09-fede-4a14-8290-74e3746bdf5c", 10, true);    //250
    public static final Reward AbsoluteRest           = new Reward("Absolute Rest",         "42552327-0630-4191-b989-a4e4ca23a7e7", 10, true);    //100


    public static final Reward OS6XHigh           = new Reward("O6S High",         "b0f9cdf8-1f02-45dc-adbc-f6e405cfc1f7", 25, true);      //500
    public static final Reward OS6XPaying         = new Reward("OS6 Paying",       "c72ec377-57e4-41ec-af61-f49d350d0382", 15, true);      //250
    public static final Reward OS6XFrequent       = new Reward("OS6 Fequent",      "1c4896b9-5455-4b3c-8f86-f86b1217d722", 10, true);      //250
    public static final Reward OS6XRest           = new Reward("OS6 Rest",         "f6704811-14c7-4ef3-9a88-e27a324f104c", 10, true);      //100

    public static final Reward M_OS6XHigh           = new Reward("O6S High",         "58ccaf1c-0cab-4461-b011-2aaee28c2cd1", 8000, true);
    public static final Reward M_OS6XPaying         = new Reward("OS6 Paying",       "fea7d7cc-8e32-4336-8b3b-70e35e720535", 6000, true);
    public static final Reward M_OS6XFrequent       = new Reward("OS6 Fequent",      "3cc546c1-3576-436e-acc9-ebe53cb4ec89", 4000, true);
    public static final Reward M_OS6XRest           = new Reward("OS6 Rest",         "3d980abf-f321-4335-89ab-26a396ee0bd5", 2000, true);

    public static final Reward newYearPaying        = new Reward("NewYear1",        "0338c05d-066e-454a-87ad-fde52ece0d79",  5000, true);
    public static final Reward newYearFree          = new Reward("NewYear2",        "bc9d1bd9-08f6-48ab-b623-917c5593b4cd",  3000, true);

    public static final Reward mysteryMonday1       = new Reward("MysteryMonday1",        "a78e5d46-2fc8-4197-82d6-31968ae8834c",  2000, true);
    public static final Reward mysteryMonday2       = new Reward("MysteryMonday2",        "9088446c-7c8a-456c-9205-233e53046783",  3333, true);

    public static final Reward loyaltyMystery1       = new Reward("LoyaltyMystery1",      "af8759d0-40a8-41e1-bd57-46b865c029af",  2000, true);
    public static final Reward loyaltyMystery2       = new Reward("LoyaltyMystery2",      "af8759d0-40a8-41e1-bd57-46b865c029af",  20, true);

    public static final Reward valentineCoins       = new Reward("Valentine",      "170d3ce3-c8c9-4b30-b795-011e31f6f319",  10000, true);


    /****************************
     *
     *          Create a reward
     *
     *

     http://slot-america-magic-box.elasticbeanstalk.com/api/rewards/aad91e9a-2b30-4bbd-ac6c-034e78531799

     {"type":"COINS","value":5000,"userCategories":[1,2,3,4,5],"groups":[],"message":"Here are your 5,000 coins, celebrating May Day with 'Ten X Pay'. Good Luck!","expires":1431361720,"id":"aad91e9a-2b30-4bbd-ac6c-034e78531799"}


     */



}
