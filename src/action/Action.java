package action;

import remoteData.dataObjects.User;

/*************************************************************************
 *
 *          Abstract common functionality of actions
 */

public abstract class Action {


    private final ActionType type;
    private User user;
    private final String message;

    public Action(ActionType type, User user, String message){

        this.type = type;
        this.user = user;
        this.message = message;
    }
}
