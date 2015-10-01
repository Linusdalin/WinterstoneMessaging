package events;

import java.sql.Timestamp;

/***************************************************************************
 *
 *          Generic functionality for an event
 *
 *
 */

public abstract class AbstractEvent implements EventInterface {

    private Timestamp expiry;

    protected void setExpiry(String expiry) {

        this.expiry = Timestamp.valueOf(expiry);
    }


        // TODO: Not Implemented: evaluation of promotable events

    public boolean isPromotable(Timestamp analysisTime){

        return false;

    }

}
