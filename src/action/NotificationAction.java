package action;

import remoteData.dataObjects.User;

/*******************************************************************
 *
 *          Notification
 *
 *          An instance of the abstract Action resulting in a notification sent to the user
 */

public class NotificationAction extends Action implements ActionInterface{


    private String url;

    public NotificationAction(String message, User user, String url, int significance){

        super(ActionType.NOTIFICATION, user, message, significance );
        this.url = url;
    }

    public ActionResponse execute() {

        return ActionResponse.NOT_IMPLEMENTED;
    }
}
