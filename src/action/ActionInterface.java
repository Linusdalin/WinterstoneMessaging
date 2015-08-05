package action;

/***********************************************************************
 *
 *              Common interfaces for actions
 */


public interface ActionInterface {

    ActionResponse execute();
    int getSignificance();
}
