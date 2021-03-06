package campaigns;

import constraints.ConstraintInterface;
import core.PlayerInfo;
import localData.Exposure;
import receptivity.ReceptivityProfile;
import remoteData.dataObjects.User;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/******************************************************************************''
 *
 *          Generic functionality for campaigns
 *
 *
 */

public abstract class AbstractCampaign implements CampaignInterface{

    private static final int HIGH_SPEND_AVERAGE = 10;   // Average spend of $15

    private String name;
    private int priority;         // Campaign base priority before persoal or situational adjustments
    private int coolDown;
    protected final CampaignState state;
    private String shortName;
    private int[] messageIds = { 0 };  // Default is to not distinguish between any different message ids. All messages are grouped into one
    private List<ConstraintInterface> constraints = new ArrayList<>();


    AbstractCampaign(String name, int priority, CampaignState state){

        this.name = name;
        this.shortName = generateShortName(name);
        this.priority = priority;
        this.state = state;
    }

    private String generateShortName(String name){
        if(name.length() < 12)
            return name;

        return name.substring(0,11);
    }


    protected void setCoolDown(int coolDown) {

        this.coolDown = coolDown;
    }

    protected void registerMessageIds(int[] messageIds) {

        this.messageIds = messageIds;
    }



    /****************************************************************
     *
     *              Test should be n calender days before reference
     *
     * @param test               - date
     * @param reference          - reference date
     * @param days               - how many days
     * @return                   - is test date n days before the reference date
     */

    public static boolean isDaysBefore(Timestamp test, Timestamp reference, int days) {

        reference = getDay(new Timestamp(reference.getTime() - 24*3600*1000*days));
        test = getDay(test);

        return test.equals(reference);

    }

    /*********************************************************'''
     *
     *              test should be BEFORE reference
     *
     * @param test
     * @param reference
     * @return                   - number of full days between
     */

    public static int getDaysBetween(Timestamp test, Timestamp reference) {

        long between = reference.getTime() - test.getTime();

        // special case for handling yesterday

        if(between > 13*3600*1000 && between < 24*3600*1000){

            // Between 13 and 24 hours ago. This is typically yesterday, even if it is not a full day

            return 1;
        }


        between /= 24*3600*1000;

        return (int)between;
    }


    /***********************************************************************
     *
     *              Minimum n hours
     *
     *              test is at least n hours before
     *
     * @param test
     * @param reference
     * @param hours
     * @return
     */

    public static boolean hoursBefore(Timestamp test, Timestamp reference, int hours) {

        reference = new Timestamp(reference.getTime() - hours * 3600*1000);
        return test.before(reference);

    }



    public static Timestamp getDay(Timestamp fecha) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime( fecha );
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return new Timestamp(calendar.getTime().getTime());

    }




    public String getTag() {

        return name.replaceAll(" ", "");

    }

    public String getName() {
        return name;
    }


    protected void setShortName(String abbr){

        this.shortName = abbr;
    }

    public String getShortName() {
        return shortName;
    }

    public int getCoolDown() {
        return coolDown;
    }

    public boolean failCoolDown(Exposure lastExposure, Timestamp executionTime) {

        int daysBetween = getDaysBetween(lastExposure.exposureTime, executionTime);
        return(daysBetween < getCoolDown());
    }

    @Override
    public int[] getAllMessageIds() {

        return messageIds;
    }


    protected String isTooLate(Timestamp executionTime, boolean overrideTime) {

       if(executionTime.getHours() > 12)
           if(!overrideTime)
               return "No morning messages after 12";
           else{

               return null;

           }

        return null;
    }

    protected String isTooEarly(Timestamp executionTime, boolean overrideTime) {

        if(executionTime.getHours() < 13)
            if(!overrideTime)
                return "No point in sending messages before 13:00";
            else{

                //System.out.println("        (Dry run ignoring too early restriction for campaign "+ getName()+")");
                return null;

            }

        return null;
    }

    protected String isTooEarlyForUser(PlayerInfo playerInfo, Timestamp executionTime, boolean overrideTime) {

        if(playerInfo.getReceptivityForPlayer().getFavouriteTimeOfDay(ReceptivityProfile.SignificanceLevel.GENERAL) != ReceptivityProfile.DAY &&
                executionTime.getHours() < 12 ){

            // This is a morning player. Send message in the morning
            return null;


        }


        if(executionTime.getHours() < 13)
            if(!overrideTime)
                return "No point in sending messages before 13:00";
            else{

                //System.out.println("        (Dry run ignoring too early restriction for campaign "+ getName()+")");
                return null;

            }


        if(playerInfo.getReceptivityForPlayer().getFavouriteTimeOfDay(ReceptivityProfile.SignificanceLevel.GENERAL) == ReceptivityProfile.DAY &&
                executionTime.getHours() > 14 && !overrideTime){

            return "Not sending to morning players in the evening";

        }

        return null;
    }


    protected String isSpecificDay(Timestamp executionTime, boolean overrideTime, String acceptedDays) {

        if(acceptedDays == null)
            return null;

        String day = (new SimpleDateFormat("EEEE")).format(executionTime.getTime()); // "Tuesday"

        if(acceptedDays.indexOf(day) >= 0)
            return null;

        if(overrideTime){
            //System.out.println("        (Dry run ignoring weekday restriction "+ dayOfWeek+" for campaign "+ getName()+")");
            return null;
        }

        return "Only applicable on " + acceptedDays + ". today is " + day;


    }


    protected boolean isHighSpender(User user) {

        return user.payments >= 3 &&
                (user.amount / user.payments) > HIGH_SPEND_AVERAGE;
    }


    protected boolean isFrequent(User user) {

        return user.sessions > 40;
    }

    protected boolean isPaying(User user) {

        return user.payments > 0;
    }

    protected int getPriority() {
        return priority;
    }

    protected int getEmailPriority() {

        return 51;
    }

    protected boolean isMale(User user) {

        return user.sex.equalsIgnoreCase("male");
    }



    protected int getInactivity(PlayerInfo info, Timestamp executionTime) {

        Timestamp lastSession = info.getLastSessionLocalCache();
        return getDaysBetween(lastSession, executionTime);

    }

    protected CampaignState getState() {
        return this.state;
    }


    protected boolean abSelect1(User user) {

        return  user.id.endsWith("1") ||
                user.id.endsWith("2") ||
                user.id.endsWith("3") ||
                user.id.endsWith("4") ||
                user.id.endsWith("5");

    }

    protected boolean abSelect2(User user) {

        return  user.id.endsWith("1") ||
                user.id.endsWith("3") ||
                user.id.endsWith("5") ||
                user.id.endsWith("7") ||
                user.id.endsWith("9");

    }


    public static boolean randomize3(User user, int expected) {

        if((user.id.endsWith("1") || user.id.endsWith("2") || user.id.endsWith("3") ||
                user.id.endsWith("10")|| user.id.endsWith("20")|| user.id.endsWith("30") ) && expected == 0)
            return true;
        if((user.id.endsWith("4") || user.id.endsWith("5") || user.id.endsWith("6") ||
                user.id.endsWith("40")|| user.id.endsWith("50")|| user.id.endsWith("60")) && expected == 1)
            return true;
        if((user.id.endsWith("7") || user.id.endsWith("8") || user.id.endsWith("9") ||
                user.id.endsWith("70")|| user.id.endsWith("80")|| user.id.endsWith("90") ||user.id.endsWith("00")) && expected == 2)
            return true;

        return false;

    }

    public static boolean randomize4(User user, int expected) {

        if((user.id.endsWith("0") || user.id.endsWith("1")  ||
                user.id.endsWith("02")|| user.id.endsWith("12")|| user.id.endsWith("22") || user.id.endsWith("32")|| user.id.endsWith("42") )
                && expected == 0)
            return true;

        if((user.id.endsWith("3") || user.id.endsWith("4")  ||
                user.id.endsWith("52")|| user.id.endsWith("62")|| user.id.endsWith("72") || user.id.endsWith("82")|| user.id.endsWith("92") )
                && expected == 1)
            return true;

        if((user.id.endsWith("5") || user.id.endsWith("6") ||
                user.id.endsWith("07")|| user.id.endsWith("17")|| user.id.endsWith("27") || user.id.endsWith("37")|| user.id.endsWith("47") )
                && expected == 2)
            return true;

        if((user.id.endsWith("8") || user.id.endsWith("9")  ||
                user.id.endsWith("57")|| user.id.endsWith("67")|| user.id.endsWith("77") || user.id.endsWith("87")|| user.id.endsWith("97") )
                && expected == 3)
            return true;


        return false;

    }

    public static boolean randomize2(User user, int expected) {

        if((user.id.endsWith("0") || user.id.endsWith("1")  || user.id.endsWith("2")||
                user.id.endsWith("3")|| user.id.endsWith("4") )
                && expected == 0)
            return true;

        if((user.id.endsWith("5") || user.id.endsWith("6")  || user.id.endsWith("7")||
                user.id.endsWith("8")|| user.id.endsWith("9") )
                && expected == 0)
            return true;

        return false;

    }

    /**************************************************************************
     *
     *          Check if this is the right day for the player
     *
     *
     * @param playerInfo            - the player
     * @param executionTime         - now
     * @param significanceLevel     - how significant do we want it
     * @return
     */


    protected boolean isRightDay(PlayerInfo playerInfo, Timestamp executionTime, ReceptivityProfile.SignificanceLevel significanceLevel) {

        ReceptivityProfile profileForPlayer =  playerInfo.getReceptivityForPlayer();
        int favouriteDay = profileForPlayer.getFavouriteDay(significanceLevel);
        int currentDay = getDayOfWeek( executionTime );

        if(favouriteDay != currentDay)
            return false;

        System.out.println("This is the right day for the player " + favouriteDay);
        return true;

    }

    protected boolean isOkDay(PlayerInfo playerInfo, Timestamp executionTime) {

        ReceptivityProfile profileForPlayer =  playerInfo.getReceptivityForPlayer();
        int favouriteDay = profileForPlayer.getFavouriteDay(ReceptivityProfile.SignificanceLevel.GENERAL);
        int currentDay = getDayOfWeek( executionTime );

        if(favouriteDay != currentDay && favouriteDay != -1)
            return false;

        System.out.println("This is an ok day for the player " + favouriteDay + ( favouriteDay == -1 ? "(No favourite day anyway)" : "general"));
        return true;

    }

    protected boolean betterTomorrow(PlayerInfo playerInfo, Timestamp executionTime) {

        ReceptivityProfile profileForPlayer =  playerInfo.getReceptivityForPlayer();
        int favouriteDay = profileForPlayer.getFavouriteDay(ReceptivityProfile.SignificanceLevel.GENERAL);
        int tomorrow = getDayOfWeek( executionTime );

        if(favouriteDay !=tomorrow)
            return false;

        System.out.println("Tomorrow ( " + favouriteDay + ") would be a better day for the player");
        return true;


    }



    protected int getDayOfWeek(Timestamp ts){

        Calendar cal = Calendar.getInstance();
        cal.setTime(ts);
        return cal.get(java.util.Calendar.DAY_OF_WEEK);
    }



    protected void setConstraint(ConstraintInterface constraint) {

        this.constraints.add(constraint);
    }


    protected ConstraintInterface getFailConstraint() {

        return null;  //TODO: Not implemented
    }

    protected String createPromoCode(int messageId) {
        return name.replaceAll(" ", "") + "-" + messageId;
    }


    protected int tagMessageIdTimeOfDay(int messageId, Timestamp executionTime) {

        if(messageId >= 50)               // Only apply one tag
            return messageId;

        if(executionTime.getHours() < 14)
            messageId += 80;                      // Use 80-series for morning messages
        return messageId;
    }

    /*********************************************************************
     *
     *          This works on the db timezone, so it indicates players registered in the morning US time
     *
     *          It is used to guess that a player is a morning player before we have any data.
     *          (e.g. the GettingStarted message)
     *
     * @param user    - the user
     * @return        - morning player or not
     */

    protected boolean registeredInTheMorning(User user) {

        int hour = user.created.getHours();

        return (hour > 10 && hour < 18);

    }

}
