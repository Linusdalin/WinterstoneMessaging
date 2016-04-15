package localData;

import remoteData.dataObjects.GenericTable;
import remoteData.dataObjects.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-04-16
 * Time: 13:17
 * To change this template use File | Settings | File Templates.
 */
public class ExposureTable extends GenericTable {

    private static final String getRemote =
            "select *" +
            "     from exposure where 1=1" +
            "      -RESTRICTION- order by exposureTime -ORDER- -LIMIT-";
    private Connection connection;


    public ExposureTable(String restriction, int limit){

        super(getRemote, restriction, limit);
        maxLimit = limit;
    }

    public ExposureTable(Connection connection){

        this( "", -1);
        this.connection = connection;
    }
                          /*
    public final String facebookId;
    public final String campaignName;
    public final int messageId;
    public final Timestamp exposureTime;
    public final String promoCode;
    public final Timestamp responseTime;

                           */

    public Exposure getNext(){

        try {
            if(!resultSet.next())
                return null;

            return new Exposure(resultSet.getString(1), resultSet.getString(2),resultSet.getInt(3),resultSet.getTimestamp(4),resultSet.getString(5), resultSet.getString(6),
                    (resultSet.getInt(7) == 1));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Exposure> getAll(){

        List<Exposure> sessions = new ArrayList<Exposure>();
        Exposure session = getNext();

        while(session != null){

            sessions.add(session);
            session = getNext();
        }

        return sessions;

    }


    /***********************************************************************************
     *
     *              Exposure is calculated as the number of successful messages the last week.
     *
     *
     * @param facebookId
     * @param personal_CoolOff
     * @return
     *
     *                  //TODO: This could be done with SQL count()
     */


    public int getUserExposure(String facebookId, String campaignName, int personal_CoolOff) {

        loadAndRetry(connection, "and user= '"+ facebookId+"' "+ (campaignName != null ? "and campaignName = '" + campaignName +"'" : "")+
                " and success = 1" +
                " and exposureTime > date_sub(current_date(), INTERVAL "+personal_CoolOff+" day)", "ASC", -1);
        List<Exposure> exposuresForUser = getAll();
        //System.out.println("Found " + exposuresForUser.size() + " exposures for user " + facebookId + "(in " + personal_CoolOff + " days)");

        int emailExposure = 0;
        int notificationExposure = 0;
        int pushExposure = 0;

        for (Exposure exposure : exposuresForUser) {

            if(exposure.type.equals("EMAIL"))
                 emailExposure++;

            if(exposure.type.equals("NOTIFICATION"))
                notificationExposure++;

            if(exposure.type.equals("PUSH"))
                pushExposure++;

        }

        if(emailExposure + notificationExposure +pushExposure == 0)
            return 0;

        int exposureLevel = ((emailExposure * 1) + (notificationExposure * 3) +(pushExposure * 2)) / (3 * (emailExposure + notificationExposure +pushExposure));

        // Reduce one for combined exposures

        if(pushExposure > 0 && notificationExposure > 0 && exposureLevel > 0)
            exposureLevel -=1;

        close();
        return exposureLevel;

    }


    public Exposure getLastExposure(String campaign, User user){

        loadAndRetry(connection, "and user= '"+ user.id+"' and success = 1 and campaignName='"+campaign+"'", "DESC", 1);
        Exposure exposure = getNext();

        close();

        return exposure;


    }

    public Exposure getLastExposure(User user){

        loadAndRetry(connection, "and success = 1 and user= '"+ user.id+"'", "DESC", 1);
        Exposure exposure = getNext();

        close();

        return exposure;


    }


}
