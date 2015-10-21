package receptivity;

import remoteData.dataObjects.GameSession;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;


/********************************************************************************'
 *
 *
 *              Profile for a player when the game sessions are played
 *
 *              It uses three sections of a day in an average US timezone
 *
 *               - Day
 *               - Evening
 *               - Night
 *
 *              NOTE: This is justfor profiling. No permanent storage
 */


public class ReceptivityProfile {

    public static final int Inconclusive = 0;
    public static final int Significant = 1;
    public static final int Not_significant = -1;

    // Level of significance requested
    public enum SignificanceLevel { SPECIFIC, GENERAL}

    int totalSessions;
    public int[][] profile;
    private Timestamp lastUpdate;
    private String userId;

    public ReceptivityProfile(String userId){

        this.userId = userId;
        this.profile = new int[][] {
                {0,0,0},
                {0,0,0},
                {0,0,0},
                {0,0,0},
                {0,0,0},
                {0,0,0},
                {0,0,0},
        };
    }


    public ReceptivityProfile(String userId, int[][] data, Timestamp lastUpdate){

        this.userId = userId;
        this.profile = data;
        this.lastUpdate = lastUpdate;
    }


    public void registerSession(GameSession session){

        totalSessions ++;

        Timestamp timeZoneAdjusted = new Timestamp(session.timeStamp.getTime() - 5*3600*1000);


        int day = getDayNumber(timeZoneAdjusted) - 1;
        int timeOfDay = getTimeOfDay(timeZoneAdjusted);

        if(isTooClose(session.timeStamp)){

            System.out.println("Ignoring session too close");
            return;
        }

        System.out.println("Registering a session on day " + day + " ( " + session.timeStamp + ") with timeOfDay = "+ timeOfDay +" Profile: " + toString());


        this.profile[day][timeOfDay]++;
        this.lastUpdate = session.timeStamp;


    }

    private boolean isTooClose(Timestamp timeStamp) {

        if(this.lastUpdate == null)
            return false;

        return timeStamp.getTime() < this.lastUpdate.getTime() + 60*60*1000;

    }

    /***************************************************************
     *
     *          Time of day divides the day in three sections according to an average US tiezone.
     *          As the database is in GMT, this is transposed
     *
     *                          US              GMT
     *          0: day          06:00 - 17:00     11:00 - 23:00
     *          1: evening      17:00 - 23:00     23:00 - 04:00
     *          2: night        23:00 - 06:00     04:00 - 11:00
     *
     *
     * @param timeStamp    - session time
     * @return             - index
     */


    private int getTimeOfDay(Timestamp timeStamp) {

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(timeStamp);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        if(hour >= 6 && hour < 17 )
            return 0;

        if(hour >= 17 )
            return 1;

            return 2;

    }

    public String toString(){

        StringBuilder output = new StringBuilder();
        output.append("Player " + userId + "[");

        for(int day = 0; day < 7; day++){

              output.append(profile[day][0] + ", ");
        }
        output.append("] Significance: " + hasSignificance(SignificanceLevel.SPECIFIC) + "( updated @ " + lastUpdate + ")");
        return output.toString();
    }


    /************************************************************************
     *
     *          Test if a user has significance according to a given level
     *
     *
     * @param significanceLevel
     * @return
     */


    public int hasSignificance(SignificanceLevel significanceLevel){

        if(totalSessions < 10)
            return Inconclusive;

        int day = getFavouriteDay(significanceLevel);

        if(day != -1){

            System.out.println("Found a favourite day "+ day +" for player " + userId);
            return Significant;
        }

        if(getFavouriteTime() != -1)
            return Significant;

        return Not_significant;
    }

    private int getFavouriteTime() {
        return -1;
    }

    /********************************************************************************
     *
     *          Calculate if there is a favourite day.
     *
     *          //TODO: Use the day with the lowest significance here too
     *
     *
     * @return   - significant or not
     */

    public int getFavouriteDay(SignificanceLevel significanceLevel) {

        int bestDay = -1;
        int bestDaySessions = 0;



        for(int day = 0; day < 7; day++){

            int sessionsForDay = sum(profile[day]);
            if(sessionsForDay > bestDaySessions){

                bestDay = day;
                bestDaySessions = sessionsForDay;
            }

        }

        double factor;

        // The factor will define how big the significant day is compared to the average

        if(significanceLevel == SignificanceLevel.GENERAL)
            factor = 1.1;
        else
            factor = 1.6;

        // Now check if the day with the most hits is significant
        // We use a simple formula saying that the number of sessions should be twice the average




        int threshold = (int)((factor * totalSessions) / 7);

        if(bestDaySessions > threshold){

            return bestDay;
        }

        return Not_significant;

    }

    private int sum(int[] intArray) {

        int total = 0;
        for (int i : intArray) {
            total += i;
        }

        return total;
    }


    public String getUserId() {
        return userId;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    private int getDayNumber(Timestamp timeStamp) {

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(timeStamp);
        return cal.get(java.util.Calendar.DAY_OF_WEEK);
    }



}
