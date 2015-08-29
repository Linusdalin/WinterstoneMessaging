package events;

import java.util.ArrayList;
import java.util.List;

/**********************************************************************************'
 *
 *    Simple event repository
 *    In a final system, this should be more elaborate
 */


public class EventRepository {

    public static final List<EventInterface> activeEvents = new ArrayList<EventInterface>(){{

        add(new HappyHourEvent( "2015-01-01 12:00:00", 1.2 ));

    }};


    public List<EventInterface> getAllEvents(){

        return activeEvents;
    }


}
