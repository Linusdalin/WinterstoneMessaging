package events;

import java.sql.Timestamp;

/********************************************************************************'
 *
 *              Interface for events
 */



public interface EventInterface {

    // Is it ok to promote the campaign right now?
    boolean isPromotable(Timestamp analysisTime);

    String getName();
    String getId();
}
