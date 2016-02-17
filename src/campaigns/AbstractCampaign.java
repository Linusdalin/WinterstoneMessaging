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

        if(executionTime.getHours() < 14)
            if(!overrideTime)
                return "No point in sending messages before 14:00";
            else{

                //System.out.println("        (Dry run ignoring too early restriction for campaign "+ getName()+")");
                return null;

            }

        return null;
    }



    protected String isSpecificDay(Timestamp executionTime, boolean overrideTime, String dayOfWeek) {

        String day = (new SimpleDateFormat("EEEE")).format(executionTime.getTime()); // "Tuesday"

        if(day.equals(dayOfWeek))
            return null;

        if(overrideTime){
            //System.out.println("        (Dry run ignoring weekday restriction "+ dayOfWeek+" for campaign "+ getName()+")");
            return null;
        }

        return "Only applicable on " + dayOfWeek + " today is " + day;


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

        return  user.facebookId.endsWith("1") ||
                user.facebookId.endsWith("2") ||
                user.facebookId.endsWith("3") ||
                user.facebookId.endsWith("4") ||
                user.facebookId.endsWith("5");

    }

    protected boolean abSelect2(User user) {

        return  user.facebookId.endsWith("1") ||
                user.facebookId.endsWith("3") ||
                user.facebookId.endsWith("5") ||
                user.facebookId.endsWith("7") ||
                user.facebookId.endsWith("9");

    }


    public static boolean randomize3(User user, int expected) {

        if((user.facebookId.endsWith("1") || user.facebookId.endsWith("2") || user.facebookId.endsWith("3") ||
                user.facebookId.endsWith("10")|| user.facebookId.endsWith("20")|| user.facebookId.endsWith("30") ) && expected == 0)
            return true;
        if((user.facebookId.endsWith("4") || user.facebookId.endsWith("5") || user.facebookId.endsWith("6") ||
                user.facebookId.endsWith("40")|| user.facebookId.endsWith("50")|| user.facebookId.endsWith("60")) && expected == 1)
            return true;
        if((user.facebookId.endsWith("7") || user.facebookId.endsWith("8") || user.facebookId.endsWith("9") ||
                user.facebookId.endsWith("70")|| user.facebookId.endsWith("80")|| user.facebookId.endsWith("90") ||user.facebookId.endsWith("00")) && expected == 2)
            return true;

        return false;

    }

    public static boolean randomize4(User user, int expected) {

        if((user.facebookId.endsWith("0") || user.facebookId.endsWith("1")  ||
                user.facebookId.endsWith("02")|| user.facebookId.endsWith("12")|| user.facebookId.endsWith("22") || user.facebookId.endsWith("32")|| user.facebookId.endsWith("42") )
                && expected == 0)
            return true;

        if((user.facebookId.endsWith("3") || user.facebookId.endsWith("4")  ||
                user.facebookId.endsWith("52")|| user.facebookId.endsWith("62")|| user.facebookId.endsWith("72") || user.facebookId.endsWith("82")|| user.facebookId.endsWith("92") )
                && expected == 1)
            return true;

        if((user.facebookId.endsWith("5") || user.facebookId.endsWith("6") ||
                user.facebookId.endsWith("07")|| user.facebookId.endsWith("17")|| user.facebookId.endsWith("27") || user.facebookId.endsWith("37")|| user.facebookId.endsWith("47") )
                && expected == 2)
            return true;

        if((user.facebookId.endsWith("8") || user.facebookId.endsWith("9")  ||
                user.facebookId.endsWith("57")|| user.facebookId.endsWith("67")|| user.facebookId.endsWith("77") || user.facebookId.endsWith("87")|| user.facebookId.endsWith("97") )
                && expected == 3)
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

}
