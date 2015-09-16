package rewards;

/*******************************************************************************
 *
 *          A reward is a coin handout for all players
 */

public class Reward {

    private final String name;
    private final String code;
    private final int coins;
    private boolean active;

    Reward(String name, String code, int coins, boolean isActive){


        this.name = name;
        this.code = code;
        this.coins = coins;
        active = isActive;
    }

    public String getCode() {
        return code;
    }

    public boolean isActive() {
        return active;
    }

    public int getCoins() {
        return coins;
    }
}
