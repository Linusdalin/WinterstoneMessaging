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

    public static final int DAY     = 0;
    public static final int EVENING = 1;
    public static final int NIGHT   = 2;

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

    public ReceptivityProfile(String userId, int[] sessionsPerDay){

        this.userId = userId;

        this.profile = new int[][] {
                {sessionsPerDay[0], 0, 0},
                {sessionsPerDay[1],0,0},
                {sessionsPerDay[2],0,0},
                {sessionsPerDay[3],0,0},
                {sessionsPerDay[4],0,0},
                {sessionsPerDay[5],0,0},
                {sessionsPerDay[6],0,0},


        };

        this.totalSessions = sum(sessionsPerDay);

    }

    public ReceptivityProfile(String userId, int[][] sessions){

        this.userId = userId;

        this.profile = new int[][] {
                {sessions[0][0], sessions[0][1], sessions[0][2]},
                {sessions[1][0], sessions[1][1], sessions[1][2]},
                {sessions[2][0], sessions[2][1], sessions[2][2]},
                {sessions[3][0], sessions[3][1], sessions[3][2]},
                {sessions[4][0], sessions[4][1], sessions[4][2]},
                {sessions[5][0], sessions[5][1], sessions[5][2]},
                {sessions[6][0], sessions[6][1], sessions[6][2]},


        };

        this.totalSessions = sum(sessions);

    }



    public ReceptivityProfile(String userId, int[][] data, Timestamp lastUpdate){

        this.userId = userId;
        this.profile = data;
        this.lastUpdate = lastUpdate;

        this.totalSessions = sum(data);
    }


    public void registerSession(GameSession session){

        totalSessions ++;

        Timestamp timeZoneAdjusted = new Timestamp(session.timeStamp.getTime() - 5*3600*1000);


        int day = getDayNumber(timeZoneAdjusted) - 1;
        int timeOfDay = getTimeOfDay(timeZoneAdjusted);

        if(isTooClose(session.timeStamp)){

            //System.out.println("Ignoring session too close");
            return;
        }

        System.out.println(" - Registering a session on day " + day + " ( " + session.timeStamp + ") with timeOfDay = "+ timeOfDay +" Profile: " + toString());


        this.profile[day][timeOfDay]++;
        this.lastUpdate = session.timeStamp;


    }

    /**********************************************************************
     *
     *          If two observations are too close, we count them as just one
     *
     *
     * @param timeStamp     - time of this session
     * @return              - are they within one hour
     */

    private boolean isTooClose(Timestamp timeStamp) {

        return this.lastUpdate != null && timeStamp.getTime() < this.lastUpdate.getTime() + 60 * 60 * 1000;

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
            return DAY;

        if(hour >= 17 )
            return EVENING;

            return NIGHT;

    }

    /*********************************************************************************
     *
     *              Display the profile for a player
     *
     *
     *
     * @return      - description as text
     */


    public String toString(){

        StringBuilder output = new StringBuilder();
        output.append("Player " + userId + "[");

        for(int day = 0; day < 7; day++){

            int dayTotal = sum(profile[day]);

              output.append(dayTotal + ", ");
        }
        output.append("] Significance: " + hasSignificance(SignificanceLevel.SPECIFIC) + "( updated @ " + lastUpdate + ")");
        return output.toString();
    }


    /************************************************************************
     *
     *          Test if a user has significance according to a given level
     *
     *
     * @param significanceLevel            - GENERAL or SPECIFIC
     * @return                             - is there a significance for the player
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
     *
     * @return   - significant or not
     */

    public int getFavouriteDay(SignificanceLevel significanceLevel) {

        int bestDay = -1;
        int bestDaySessions = 0;
        int worstDaySessions = 999999999;

        for(int day = 0; day < 7; day++){

            int sessionsForDay = sum(profile[day]);
            if(sessionsForDay > bestDaySessions){

                bestDay = day;
                bestDaySessions = sessionsForDay;
            }

            if(sessionsForDay < worstDaySessions){

                worstDaySessions = sessionsForDay;
            }

        }

        double factor;

        // The factor will define how big the significant day is compared to the average

        if(significanceLevel == SignificanceLevel.GENERAL)
            factor = 1.3;
        else
            factor = 1.7;

        // Now check if the day with the most hits is significant
        // We use a simple formula saying that the number of sessions should be 6 times the average

        int threshold = (int)((factor * (totalSessions - worstDaySessions)) / 6);

        if(threshold >= 2 && bestDaySessions > threshold){

            return bestDay;
        }

        return Not_significant;

    }


    /**************************************************************************
     *
     *              Get the preferred time of day for the player
     *
     *
     *
     * @param significanceLevel
     * @return
     */

    public int getFavouriteTimeOfDay(SignificanceLevel significanceLevel) {

        int bestTime = -1;
        int bestTimeSessions = 0;
        int worstTimeSessions = 999999999;

        for(int timeOfDay = 0; timeOfDay < 3; timeOfDay++){

            int sessionsForTime = sum(profile, timeOfDay);
            if(sessionsForTime > bestTimeSessions){

                bestTime = timeOfDay;
                bestTimeSessions = sessionsForTime;
            }

            if(sessionsForTime < worstTimeSessions){

                worstTimeSessions = sessionsForTime;
            }

        }

        double factor;

        // The factor will define how big the significant day is compared to the average

        if(significanceLevel == SignificanceLevel.GENERAL)
            factor = 1.2;
        else
            factor = 1.5;

        // Now check if the day with the most hits is significant
        // We use a simple formula saying that the number of sessions should be twice the average

        int threshold = (int)((factor * (totalSessions)) / 3);

        if(bestTimeSessions > threshold){

            return bestTime;
        }

        return Not_significant;

    }

    /*************************************************************
     *
     *          Sum all of a time bracket over all days
     *
     * @param profile
     * @param timeOfDay
     * @return
     */

    private int sum(int[][] profile, int timeOfDay) {

        int total = 0;

        for (int[] day : profile) {

            total += day[timeOfDay];
        }

        return total;

    }


    private int sum(int[] intArray) {

        int total = 0;
        for (int i : intArray) {
            total += i;
        }

        return total;
    }

    private int sum(int[][] intArray) {

        int total = 0;
        for (int[] i : intArray) {
            total += sum(i);
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
