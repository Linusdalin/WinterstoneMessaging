package rewards;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-09-16
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */
public class RewardRepository {

    public static final Reward freeCoinAcitivationPaying  = new Reward("Free Coin Activation Paying", "512853e3-8389-453a-b92d-479e330414ba", 3000, true);
    public static final Reward freeCoinAcitivationFree    = new Reward("Free Coin Activation Non-Paying", "8606fa50-6c62-4c25-ba21-40600fc79d42", 1000, true);

    public static final Reward homeRun    = new Reward("Home Run", "3670d282-34a0-4337-a4f8-3a3da70a9378", 3000, true);

}
