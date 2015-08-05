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
    private int significance;

    public Action(ActionType type, User user, String message, int significance){

        this.type = type;
        this.user = user;
        this.message = message;
        this.significance = significance;
    }

    public int getSignificance() {
        return significance;
    }
}
