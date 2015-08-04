package remoteData.dataObjects;

import java.sql.*;

/**
 *
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-04-16
 * Time: 13:17
 * To change this template use File | Settings | File Templates.
 */
public class PaymentTable extends GenericTable {

    private static final String getRemote =
            "select sessions.playerid, amount, game, timestamp, promocode, firstLogin " +
            "     from payments, sessions " +
            "     where sessions.sessionId = payments.sessionId " +
            "      and timestamp > \"$(THRESHOLD)\" order by timestamp";


    public PaymentTable(String startTime, String  restriction, int limit){

        super(getRemote, startTime, restriction, limit);
        maxLimit = limit;
    }

    public PaymentTable(){

        this("2015-01-01", "", -1);
    }



    public Payment getNext(){

        try {
            if(!resultSet.next())
                return null;

            return new Payment(resultSet.getString(1), resultSet.getInt(2),resultSet.getString(3),resultSet.getTimestamp(4),resultSet.getString(5),resultSet.getTimestamp(6));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void storeLocally(Connection connection, Payment p){

        p.wash();

        String insert = "insert into Payment values (" + p.toSQLValues() + ")";

        //System.out.println("Insert with: " + insert);

        try{

            Statement statement = connection.createStatement();
            statement.execute(insert);

        }catch(SQLException e){

            System.out.println("Error accessing data in database");
            e.printStackTrace();
        }

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
}
