package test;


import action.ActionResponse;
import action.TriggerEventAction;
import campaigns.CampaignState;
import dbManager.ConnectionHandler;
import events.EventRepository;
import org.junit.Test;
import output.DeliveryException;
import output.RequestHandler;
import remoteData.dataObjects.User;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Calendar;

import static org.junit.Assert.assertTrue;

/*****************************************************************************''
 *
 *                  Sending a test email
 *
 *                                              10152409426034632
 */

public class PersonalEventTest {

    private static final User linus = new User("627716024", "627716024",                "Linus",        "linusdalin@gmail.com",     "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male", Timestamp.valueOf("2016-01-01 00:00:00"));
    private static final User stageLinus = new User("10152816515441025", "10152816515441025",        "LinusTest",    "linusdalin@gmail.com",     "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male", Timestamp.valueOf("2016-01-01 00:00:00"));
    private static final User knif = new User("10206348427411666", "10206348427411666",        "LinusTest",    "linusdalin@gmail.com",     "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male", Timestamp.valueOf("2016-01-01 00:00:00"));
    private static final User wrongUser  = new User("1111111", "1111111",                  "Mr avreggad",  "linusdalin@gmail.com",     "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male", Timestamp.valueOf("2016-01-01 00:00:00"));

    private static final String StageEventService = "http://slotamerica:fruitclub@dev.slot-america.com:3302/api/player-events/";
    private static final String EventService      = "https://slotamerica:fruitclub@data-warehouse.slot-america.com/api/player-events/";


    String personalEventStage = "8715646f-4cb3-4000-8878-4a52ab108958";

    @Test
    public void activateTest(){


        try{

            Calendar calendar = Calendar.getInstance();
            long time = calendar.getTimeInMillis() + 24*3600*1000;

            String request = StageEventService + stageLinus.id;
            String response;

            RequestHandler requestHandler = new RequestHandler( request ).withBasicAuth("5b09eaa11e4bcd80800200c", "X");
            response = requestHandler.executeGet( "" );
            System.out.println("response:" + response);


            response = requestHandler.executePut( "[{\"eventId\":\"8715646f-4cb3-4000-8878-4a52ab108958\",\"endTime\":"+ time+"}]", "application/json" );
            System.out.println("response:" + response);


            response = requestHandler.executeGet( "" );
            System.out.println("response:" + response);


        }catch(DeliveryException e){

            assertTrue(false);
        }

    }



    @Test
    public void deActivateTest(){


        try{


            String request = StageEventService + stageLinus.id;
            String response;

            RequestHandler requestHandler = new RequestHandler( request ).withBasicAuth("5b09eaa11e4bcd80800200c", "X");
            response = requestHandler.executeGet( "" );
            System.out.println("response:" + response);


            response = requestHandler.executePut( "[]", "application/json" );


            System.out.println("response:" + response);

            response = requestHandler.executeGet( "" );
            System.out.println("response:" + response);


        }catch(DeliveryException e){

            assertTrue(false);
        }

    }


    @Test
    public void actionTest(){


        try{

            Calendar calendar = Calendar.getInstance();
            Timestamp executionTime = new Timestamp(calendar.getTime().getTime());
            Connection connection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);

            TriggerEventAction action = new TriggerEventAction(EventRepository.Conversion1Stage, 48, stageLinus, executionTime, 100, "testCampaign", 1, CampaignState.ACTIVE, 1.0);
            action.useStage();
            ActionResponse response = action.execute(false, null, executionTime, connection, 0, 0);
            System.out.println("response:" + response.toString());


        }catch(Exception e){

            assertTrue(false);
        }

    }


    @Test
    public void actionTestLive(){


        try{

            Calendar calendar = Calendar.getInstance();
            Timestamp executionTime = new Timestamp(calendar.getTime().getTime());
            Connection connection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);

            TriggerEventAction action = new TriggerEventAction(EventRepository.Conversion1, 48, linus, executionTime, 100, "testCampaign", 1, CampaignState.ACTIVE, 1.0);
            ActionResponse response = action.execute(false, null, executionTime, connection, 0, 0);
            System.out.println("response:" + response.toString());


        }catch(Exception e){

            e.printStackTrace();
            assertTrue(false);
        }

    }

    @Test
    public void deActivateAllLive(){


        try{


            String request = EventService + linus.id;
            String response;

            RequestHandler requestHandler = new RequestHandler( request ).withBasicAuth("5b09eaa11e4bcd80800200c", "X");
            response = requestHandler.executeGet( "" );
            System.out.println("response:" + response);


            response = requestHandler.executePut( "[]", "application/json" );


            System.out.println("response:" + response);

            response = requestHandler.executeGet( "" );
            System.out.println("response:" + response);


        }catch(DeliveryException e){

            assertTrue(false);
        }

    }



}
