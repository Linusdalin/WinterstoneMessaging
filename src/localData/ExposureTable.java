package localData;

import core.Execute;
import remoteData.dataObjects.GenericTable;
import remoteData.dataObjects.Payment;
import remoteData.dataObjects.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
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

            return new Exposure(resultSet.getString(1), resultSet.getString(2),resultSet.getInt(3),resultSet.getTimestamp(4),resultSet.getString(5), resultSet.getString(6));

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
     *              Exposure is calculated as the number of messages the last week.
     *
     *
     * @param user
     * @param personal_CoolOff
     * @return
     *
     *
     */


    public int getUserExposure(User user, int personal_CoolOff) {

        load(connection, "and user= '"+ user.facebookId+"' and exposureTime > date_sub(current_date(), INTERVAL "+personal_CoolOff+" day)");
        List<Exposure> exposuresForUser = getAll();
        System.out.println("Found " + exposuresForUser.size() + " exposures for user " + user.name);
        return exposuresForUser.size();

    }


    public Exposure getLastExposure(String campaign, User user){

        load(connection, "and user= '"+ user.facebookId+"' and campaignName='"+campaign+"'", "DESC", 1);
        Exposure exposure = getNext();

        close();

        return exposure;


    }



}
