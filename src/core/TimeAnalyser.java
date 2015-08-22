package core;

/**************************************************************************
 *
 *
 *              Calculate the timing for sending a message
 *
 */
public class TimeAnalyser {


    private PlayerInfo playerInfo;

    public TimeAnalyser(PlayerInfo playerInfo) {

        this.playerInfo = playerInfo;
    }

    public int eligibilityForCommunication(){

        System.out.println(" -- Not implemented evaluating eligibility for communication. Sending to all");
        return 100;
    }

}
