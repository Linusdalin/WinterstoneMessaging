package remoteData.dataObjects;

import java.sql.*;
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
public class PaymentTable extends GenericTable {

    private static final String getSQL =
            "select *" +
            "     from payment where 1=1" +
            "      -RESTRICTION- order by timestamp -LIMIT-";

    private static final String getRemoteSQL =
            "select playerid, amount, game, issued " +
            "     from payments where 1=1" +
            "      -RESTRICTION- order by issued -LIMIT-";

    public PaymentTable(String  restriction, int limit){

        super(getSQL, restriction, limit);
        maxLimit = limit;
    }

    public PaymentTable(){

        this( "", -1);
    }

    /*
    playerid varchar(20)
    amount int(8)
    game varchar(30)
    timestamp timestamp
    promocode varchar(80)
    firstLogin timestamp
     */


    public Payment getNext(){

        try {
            if(!resultSet.next())
                return null;

            return new Payment(resultSet.getString(1), resultSet.getInt(2),resultSet.getString(3),resultSet.getTimestamp(4));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Payment> getAll(){

        List<Payment> sessions = new ArrayList<Payment>();
        Payment session = getNext();

        while(session != null){

            sessions.add(session);
            session = getNext();
        }

        return sessions;

    }




    public List<Payment> getPaymentsForUser(User user, Connection connection) {

        load(connection, "sessions.playerId= '"+ user.facebookId+"'");
        List<Payment> paymentsForUser = getAll();
        System.out.println("Found " + paymentsForUser.size() + " payments for user " + user.name);
        return paymentsForUser;

    }


    public String getRemoteSQL(Timestamp fromTime, int maxRecords) {

        String queryString = getQueryString(getRemoteSQL, "and issued > '"+fromTime.toString()+"'", maxRecords, -1, order);
        //System.out.println("Query: " + queryString);

        return queryString;
    }

    public Timestamp getLast(Connection connection){

        String sql = "select timeStamp from payment order by timestamp desc limit 1;";

        try{

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            if(!rs.next())
                return null;  // There are no entries in the database

            return rs.getTimestamp(1);

        }catch(SQLException e){

            System.out.println("Error accessing data in database");
            e.printStackTrace();
        }


        return null;
    }

    public void loadRemote(Timestamp from, int records, Connection connection){

        String sql = getRemoteSQL(from, records);
        System.out.println(" -- Retrieving remote data with " + sql );

        loadFromDB(connection, sql);
    }





}
