package action;

/*******************************************************************************
 *
 *          Status of the outcome of an action
 *
 */

public enum ActionResponseStatus {

    OK,
    FAILED,
    FAILED_PERMANENTLY,
    FAILED_REMOTE_ERROR,
    FAILED_INTERNAL_ERROR,
    ABORTED,
    IGNORED,
    MANUAL;

    public boolean isPermanentError() {

        return this == FAILED_PERMANENTLY;
    }
}
