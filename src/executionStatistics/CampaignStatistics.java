package executionStatistics;

/*******************************************************************************
 *
 *
 *          Statistics for the execution of a specific campaign
 *
 */

public class CampaignStatistics {

    private int timesFired = 0;
    private int timesOverrun = 0;
    private int timesPotential = 0;

    private String name;

    public CampaignStatistics(String name){

        this.name = name;
    }


    public void countFired(){

        timesFired++;
    }


    public String toString(){

        return "Fired: " + timesFired + " Overrun: " + timesOverrun + " Potential (if live): " + timesPotential;
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


}
