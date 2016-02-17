package executionStatistics;

import action.ActionType;
import statistics.Display;

/*******************************************************************************
 *
 *
 *          Statistics for the execution of a specific campaign
 *
 */

public class CampaignStatistics {

    private int messageId;

    private int timesFiredNotification = 0;
    private int timesFiredPush = 0;
    private int timesFiredEmail = 0;
    private int timesCoolingDown = 0;
    private int timesOverrun = 0;
    private int timesPotential = 0;

    private String name;

    public CampaignStatistics(String name, int messageId){

        this.name = name;
        this.messageId = messageId;
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

        return " -- id: "+ Display.fixedLengthLeft(messageId, 3) +
                " Fired: ( N:" + Display.fixedLengthLeft(timesFiredNotification, 6) + " + MP:" + Display.fixedLengthLeft(timesFiredPush, 6) +
                " + E:" + Display.fixedLengthLeft(timesFiredEmail, 6)+ " ) Cooling Down: "+ Display.fixedLengthLeft(timesCoolingDown, 6)+
                ", Overrun: " + Display.fixedLengthLeft(timesOverrun, 6) + (timesPotential > 0 ? " Potential (if live): " + timesPotential : "");
    }

    public String getName() {
        return name;
    }

    public int getId(){

        return messageId;
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
