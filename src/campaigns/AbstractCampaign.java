package campaigns;

import core.PlayerInfo;
import localData.Exposure;
import remoteData.dataObjects.User;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    private final CampaignState state;
    private String shortName;


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


    protected String isTooEarly(Timestamp executionTime, boolean overrideTime) {

       if(executionTime.getHours() < 16)
           if(!overrideTime)
               return "No point in sending messages before 16:00";
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

        Timestamp lastSession = info.getLastSession();
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


}
