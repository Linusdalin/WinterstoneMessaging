package action;

/**********************************************************************************
 *
 *          Type of action
 *
 *
 *
 */


public enum ActionType {

    NOTIFICATION,               // send a facebook notification
    PUSH,                       // send a iOS push notification
    MANUAL_ACTION,              // Perform a amnual action
    EMAIL,                      // Sending an email
    IN_GAME,                    // TODO: Future: Send an ingame message
    COIN_ACTION,                // Give the user some coins
    TRIGGER_EVENT               // Trigger a personalized event for the player
}
