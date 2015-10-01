package action;

/**********************************************************************************
 *
 *          Type of action
 *
 *
 *
 */


public enum ActionType {

    NOTIFICATION,             // send a notification
    MANUAL_ACTION,            // Perform a amnual action
    EMAIL,                    // Sending an email
    IN_GAME,                 // TODO: Future: Send an ingame message
    COIN_ACTION,              // Give the user some coins
}
