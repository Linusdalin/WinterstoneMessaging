package core;

import localData.ExposureTable;

/**************************************************************************
 *
 *
 *              Calculate the timing for sending a message
 *
 */
public class TimeAnalyser {


    private PlayerInfo playerInfo;
    private final int Personal_CoolOff = 7;

    public TimeAnalyser(PlayerInfo playerInfo) {

        this.playerInfo = playerInfo;
    }

    public int eligibilityForCommunication(ExposureTable campaignExposures){

        int exposures = campaignExposures.getUserExposure(playerInfo.getUser(), Personal_CoolOff);

        if(exposures > 1 ){

            System.out.println("Already tvo messages this week. Not sending any more");
            return 0;
        }

        if(exposures == 1 ){

            System.out.println("Already one message this week. Only high priority messages");
            return 65;
        }

        return 100;
    }

}
