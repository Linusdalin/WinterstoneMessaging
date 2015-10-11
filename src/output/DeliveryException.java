package output;

import action.ActionResponseStatus;

/***************************************************************
 *
 *              A connection exception is thrown when a connection to a service fails
 *
 *              It translates this to an action code depending on what happens
 */

public class DeliveryException extends Exception {


    private int httpCode;

    public DeliveryException(int httpCode) {

        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }


    public ActionResponseStatus  getStatus(){

        switch(httpCode){

            case 200:     // All OK
                return ActionResponseStatus.OK;

            case 400:       // Bad request
            case 403:     //Forbidden. This means that we cant access the player
                return ActionResponseStatus.FAILED_PERMANENTLY;

            case 500:     // System errors
            case 502:
                return ActionResponseStatus.FAILED_REMOTE_ERROR;

            case 404:     //Not found
            case 405:    // Method not allowed
                return ActionResponseStatus.FAILED_INTERNAL_ERROR;
        }

        return ActionResponseStatus.FAILED;
    }
}
