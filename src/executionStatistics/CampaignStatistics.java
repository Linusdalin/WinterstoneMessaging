package executionStatistics;

/*******************************************************************************
 *
 *
 *          Statistics for the execution of a specific campaign
 *
 */

public class CampaignStatistics {

    private int timesFired = 0;
    private String name;

    public CampaignStatistics(String name){

        this.name = name;
    }


    public void countFired(){

        timesFired++;
    }


    public String toString(){

        return "Fired: " + timesFired;
    }

    public String getName() {
        return name;
    }
}
