package events;

import java.sql.Timestamp;

/***************************************************************************
 *
 *          Generic functionality for an event
 *
 *
 */

public abstract class AbstractEvent implements EventInterface {


    private String name;
    private final String expiry;
    private final String id;

    public AbstractEvent(String name, String code, String expiry) {

        this.name = name;
        this.expiry = expiry;
        this.id = code;
    }


        // TODO: Not Implemented: evaluation of promotable events

    public boolean isPromotable(Timestamp analysisTime){

        return false;

    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

}
