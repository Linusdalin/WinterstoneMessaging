package events;

/************************************************************************'
 *
 *              Happy Hour is an event where there is a bonus on hte
 *
 */


public class HappyHourEvent extends AbstractEvent implements EventInterface {

    private final double coinMultiplier;

    public HappyHourEvent(String expiry, double coinMultiplier) {

        this.coinMultiplier = coinMultiplier;
        setExpiry(expiry);

    }

}
