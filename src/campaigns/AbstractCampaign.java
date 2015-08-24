package campaigns;

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

    private static final int HIGH_SPENDER       = 10;   // Average spend of $15

    private String name;
    private int priority;         // Campaign base priority before persoal or situational adjustments
    private int coolDown;

    AbstractCampaign(String name, int priority){

        this.name = name;
        this.priority = priority;
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
     * @return
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
     * @param test
     * @param reference
     * @param hours
     * @return
     */

    public static boolean hoursBefore(Timestamp test, Timestamp reference, int hours) {

        reference = getDay(new Timestamp(reference.getTime() - hours * 3600*1000));
        test = getDay(test);

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


    /********************************************************************'
     *
     *          Create a promo code based on the characteristics of a player.
     *
     *
     * @param name                   - the name of the campaign
     * @param user                   - the name of the user
     * @return                       - the promocode
     */

    protected String createPromoCode(String name, User user, int inactivity) {

        String tag = name.replaceAll(" ", "");
        return tag + "-C" + user.userCategory + "-P" + user.payments + "-S"+user.sessions+ "-G"+user.group+ "-L"+user.level+"-D"+user.nextNumberOfPicks+"-M"+inactivity;

    }

    protected String createTag(String name) {

        return name.replaceAll(" ", "");

    }

    public String getName() {
        return name;
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

        return user.payments > 0 && user.amount / user.payments > HIGH_SPENDER;
    }

    protected boolean isLowSpender(User user) {

        return user.payments > 0 && user.amount / user.payments < HIGH_SPENDER;
    }


    protected int getPriority() {
        return priority;
    }
}
