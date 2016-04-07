package events;

import java.util.ArrayList;
import java.util.List;

/**********************************************************************************'
 *
 *    Simple event repository
 *    In a final system, this should be more elaborate
 */


public class EventRepository {

    public static final EventInterface Conversion1      = new SaleEvent( "conversion 1",     "dbb80cfd-e674-48ae-a2bd-9f3ddaf577af", "2015-01-01 12:00:00", 2);
    public static final EventInterface Conversion1Stage = new SaleEvent( "conversion stage", "8715646f-4cb3-4000-8878-4a52ab108958", "2015-01-01 12:00:00", 2);


    public static final List<EventInterface> activeEvents = new ArrayList<EventInterface>(){{

        add(Conversion1);

    }};


    public List<EventInterface> getAllEvents(){

        return activeEvents;
    }


}
