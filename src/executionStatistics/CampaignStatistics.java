package executionStatistics;

import action.ActionType;

/*******************************************************************************
 *
 *
 *          Statistics for the execution of a specific campaign
 *
 */

public class CampaignStatistics {

    private int timesFiredNotification = 0;
    private int timesFiredPush = 0;
    private int timesFiredEmail = 0;
    private int timesCoolingDown = 0;
    private int timesOverrun = 0;
    private int timesPotential = 0;

    private String name;

    public CampaignStatistics(String name){

        this.name = name;
    }


    public void countFired(ActionType type){

        if(type == ActionType.NOTIFICATION)
            timesFiredNotification++;

        if(type == ActionType.PUSH)
            timesFiredPush++;

        if(type == ActionType.EMAIL)
            timesFiredEmail++;
    }


    public String toString(){

        return "Fired: ( N:" + timesFiredNotification + " + MP:" + timesFiredPush + " + E:" + timesFiredEmail+ " ) Cooling Down: "+ timesCoolingDown+", Overrun: " + timesOverrun + (timesPotential > 0 ? " Potential (if live): " + timesPotential : "");
    }

    public String getName() {
        return name;
    }

    public void countPotential() {

        timesPotential++;
    }

    // When another campaign had higher priority
    public void countOverrun() {

        timesOverrun++;
    }

    public void countCoolDown() {

        timesCoolingDown++;
    }


}
