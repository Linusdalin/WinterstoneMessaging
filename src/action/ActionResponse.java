package action;

/**********************************************************************************************''
 *
 *              The response of an action
 */
public class ActionResponse {

    private final ActionResponseStatus status;
    private final String message;

    public ActionResponse(ActionResponseStatus status, String message){

        this.status = status;
        this.message = message;
    }

    public boolean isExecuted() {

        return status == ActionResponseStatus.OK;
    }

    public ActionResponseStatus getStatus() {

        return status;
    }

}
