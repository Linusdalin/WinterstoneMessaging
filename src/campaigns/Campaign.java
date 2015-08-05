package campaigns;

import action.Action;

/*********************************************************************
 *
 *          A campaign is an action together with the trigger
 */
public abstract class Campaign {


    private Action action;

    public Campaign(Action action){

        this.action = action;
    }
}
