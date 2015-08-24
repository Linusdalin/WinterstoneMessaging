package core;

import campaigns.AbstractCampaign;
import remoteData.dataObjects.GameSession;

import java.sql.Timestamp;
import java.util.List;

/***************************************************************************'
 *
 *              yesterday is information on what happened yesterday
 *              to be able to make decisions on CRM actions
 */
public class Yesterday {

    private int actions;
    private int totalWager;
    private int totalWin;

    public Yesterday(int actions, int totalWager, int totalWin){
        this.actions = actions;
        this.totalWager = totalWager;
        this.totalWin = totalWin;
    }

    /******************************************************************'
     *
     *          Create the statistics from the sessions registered by the user.
     *
     *
     * @param info                   - all sessions (not only yesterday)
     * @param executionTime          - execution time
     */


    public Yesterday(PlayerInfo info, Timestamp executionTime){

        List<GameSession> sessions = info.getSessionsForUser();

        for (GameSession session : sessions) {

            if(AbstractCampaign.isDaysBefore(session.timeStamp, executionTime, 1)){

                totalWager += session.totalWager;
                totalWin += session.totalWin;
                actions += session.spins;
            }
        }

    }


    public boolean didPlay(){

        return actions > 0;
    }

    public int getPayout(){

        if(!didPlay())
            return 0;

        return (100*totalWin)/totalWager;
    }

    public int getAverageBet(){

        if(!didPlay())
            return 0;

        return (totalWager)/actions;
    }


    public int getActions() {
        return actions;
    }

    public String toString(){

        return getPayout() + "% in " + actions + " actions betting " + getAverageBet() + " coins on average";
    }


}