package events;

/************************************************************************'
 *
 *              Happy Hour is an event where there is a bonus on hte
 *
 */


public class SaleEvent extends AbstractEvent implements EventInterface {

    private final double coinMultiplier;

    public SaleEvent(String name, String code, String expiry, double coinMultiplier) {

        super(name, code, expiry);
        this.coinMultiplier = coinMultiplier;

    }

}
