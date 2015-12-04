package core;

import dbManager.ConnectionHandler;

import java.sql.Timestamp;
import java.util.Calendar;

/***************************************************************************'''
 *
 *          This is the second pass through the generated actions stored in the action table.
 */

public class Send {

    private static final int     Send_Cap               = 10000;
    private static final boolean DRY_RUN                = true;
    private static final String  TEST_USER              = "627716024";                       // "627716024";          // Tina:     "105390519812878";

    private static final boolean SEND_EMAIL             = true;
    private static final int BACK_TRACK_DAYS            = 0;

    private static final int TimeSlot                   = -1;                                   // 0 = day (17:00), 1 = evening (00:00), 2 = night (06:00)


    /********************************************************************
     *
     *              Main execution  for scheduling messages
     *
     *
     * @param args        -
     */

    public static void main(String[] args){

        System.out.println("****************************************************\n*  Executing the WinterStone Send Tool");

        ConnectionHandler.Location dataSource = ConnectionHandler.Location.local;
        SendEngine engine = new SendEngine(dataSource, DRY_RUN, SEND_EMAIL,  Send_Cap, TEST_USER);

        System.out.println(" -- DRY_RUN is      " + DRY_RUN);
        System.out.println(" -- SEND_CAP is     " + Send_Cap);

        if(Send_Cap == 1 && TEST_USER != null){

            System.out.println(" -- TEST USER ONLY!");

        }
        else if(!DRY_RUN && Send_Cap > 0){

            if(SEND_EMAIL)
                System.out.println(" -- THIS WILL BE A LIVE EXECUTION!");
            else
                System.out.println(" -- THIS IS A LIVE EXECUTION, BUT NO EMAILS!");

        }

        int pendingActions = engine.countActions(BACK_TRACK_DAYS);

        System.out.println("Executing " + ( Send_Cap < pendingActions ? Send_Cap + " of " : "") + pendingActions + " generated actions");


        System.out.print("Start Run?\n>");
        CampaignEngine.waitReturn();

        Calendar calendar = Calendar.getInstance();
        Timestamp executionTime = new java.sql.Timestamp(calendar.getTime().getTime());


        engine.executeSend(executionTime, BACK_TRACK_DAYS, TimeSlot);

    }



}
