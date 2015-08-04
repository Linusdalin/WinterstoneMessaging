package action;

/**********************************************************************************************''
 *
 *              The response of an action
 */
public class ActionResponse {

    private final ActionResponseStatus status;
    private final String message;
    public static final ActionResponse NOT_IMPLEMENTED = new ActionResponse(ActionResponseStatus.FAIL, "Not implemented");

    public ActionResponse(ActionResponseStatus status, String message){

        this.status = status;
        this.message = message;
    }
}
