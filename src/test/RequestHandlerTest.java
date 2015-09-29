package test;


import action.ActionResponseStatus;
import org.junit.Test;
import output.DeliveryException;
import output.RequestHandler;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/*****************************************************************************''
 *
 *                  HTTP request and error handling
 *
 *
 */

public class RequestHandlerTest {


    @Test
    public void failPostTest(){

        try{

            RequestHandler requestHandler = new RequestHandler("https://www.google.se");
            String response = requestHandler.executePost("");

            assertFalse(true);

        }catch(DeliveryException e){

            assertThat(e.getHttpCode(), is( 405 ));
            assertThat(e.getStatus(), is( ActionResponseStatus.FAILED_INTERNAL_ERROR ));

        }catch(Exception e){

            e.printStackTrace();
            assertFalse(true);
        }
    }



    @Test
    public void getTest(){

        try{

            RequestHandler requestHandler = new RequestHandler("http://www.aftonbladet.se/nyheter/krim/article21492347.ab");
            String response = requestHandler.executeGet();

            System.out.println(response);

            assertNotNull(response);

        }catch(Exception e){

            e.printStackTrace();
            assertFalse(true);
        }
    }

}