package transfer;

import remoteData.dataObjects.Payment;
import remoteData.dataObjects.PaymentTable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/******************************************************************
 *
 *      Analyse payments
 */

public class PaymentAnalyser {

    private static final double SIGNIFICANT_PAYMENT_CHANGE = 1.5;

    private static final double BETTER_AVERAGE_BET = 2.0;
    private static final double BETTER_TOTAL = 2.5;
    private static final double SIGNIFICANT_BETTER_TOTAL = 3.5;


    public PaymentAnalyser(){

    }

    /*****************************************************
     *
     *          0:      unanalyzed
     *          1:      unknown/first
     *          2:      regular
     *          3:      higher than normal
     *          -1      lower than normal
     *
     *
     *
     * @param payment              - the payment
     * @param connection           - connection to the local database
     *
     *       SIDE EFFECT: Updating the payment with analysis data
     *
     */


    public void updateForSize(Payment payment, Connection connection){

        PaymentTable table = new PaymentTable();
        int average = table.getAverage(connection, payment.facebookId);

        if(average == 0)
            payment.cprAverage = 1;
        else if(payment.amount > average * SIGNIFICANT_PAYMENT_CHANGE)
            payment.cprAverage = 3;
        else if(average > payment.amount * SIGNIFICANT_PAYMENT_CHANGE)
            payment.cprAverage = -1;
        else
            payment.cprAverage = 2;

    }

    public static final int UNKNOWN_BEHAVIOUR       = 1;
    public static final int UNDEFINED_BEHAVIOUR     = 2;
    public static final int SAME_BEHAVIOUR          = 3;
    public static final int INDICATION_BEHAVIOUR    = 4;
    public static final int ELEVATED_BEHAVIOUR      = 5;
    public static final int SIGNIFICANT_BEHAVIOUR   = 6;



    /************************************************************
     *
     *          1:      unknown
     *          2:      undefined (consecutive payments)
     *          3:      not more agressive
     *          4:      a small tendency to increase bet
     *          5:      a bit more aggressive
     *          6:      significantly more aggressive
     *
     *
     * @param payment
     * @param connection
     *
     *
     */


    public void setUnknownBehaviour(Payment payment, Connection connection) {

        payment.behavior = UNKNOWN_BEHAVIOUR;
    }


    public int getBehaviour(Payment payment, PaymentTable table, Connection connection){

        if(getPaymentsBefore(payment, connection) > 0){

            System.out.println(" -- Consecutive Payment. Ignoring this");
            return UNDEFINED_BEHAVIOUR;

        }
        else{

            System.out.println(" -- Found no payment before");

        }



        PaymentBehavior before = getBefore(payment, connection);
        PaymentBehavior after = getAfter(payment, connection);

        System.out.println(" -- Before: " + before.toString());
        System.out.println(" -- After:  " + after.toString());

        // Decide in order. Most significant first

        if(isMuchBetter(after, before)){

            System.out.println(" !! MuchBetter!");
            return SIGNIFICANT_BEHAVIOUR;

        }

        if(isBetterTotal(after, before)){

            System.out.println(" !! Higher Total!");
            return ELEVATED_BEHAVIOUR;

        }

        if(isBetterAverage(after, before)){

            System.out.println(" !! Higher Bet!");
            return INDICATION_BEHAVIOUR;

        }


        return SAME_BEHAVIOUR;


    }



    private int getPaymentsBefore(Payment payment, Connection connection) {

        String sql = "select count(*) from payment where playerId = '"+payment.facebookId+"' " +
        "and timeStamp > date_sub('"+payment.timeStamp.toString()+"', interval 1 day) " +
        "and timeStamp < '"+ payment.timeStamp.toString()+"'";

        try{


            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            resultSet.next();

            int noPayments = resultSet.getInt( 1 );
            System.out.println("   ** Found " + noPayments + " payments day before***\n" + sql);

            resultSet.close();
            statement.close();

            return noPayments;

        }catch(SQLException e){

                System.out.println("Error accessing data in database with the query:\n" + sql);
                e.printStackTrace();
        }

        return 0;

    }

    public int getOrdinal(Payment payment, Connection connection) {

        String sql = "select count(*) from payment where playerId = '"+payment.facebookId+"' " +
                "and timeStamp < '"+ payment.timeStamp.toString()+"'";

        try{


            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            resultSet.next();

            int noPayments = resultSet.getInt( 1 );
            System.out.println("   ** Found " + noPayments + " payments (total) ***\n" + sql);

            resultSet.close();
            statement.close();

            return noPayments;

        }catch(SQLException e){

            System.out.println("Error accessing data in database with the query:\n" + sql);
            e.printStackTrace();
        }

        return 0;

    }


    private boolean isBetterAverage(PaymentBehavior after, PaymentBehavior before) {
        return after.averageBet > before.averageBet * BETTER_AVERAGE_BET;
    }

    private boolean isBetterTotal(PaymentBehavior after, PaymentBehavior before) {
        return after.getTotal() > before.getTotal() * BETTER_TOTAL;
    }

    private boolean isMuchBetter(PaymentBehavior after, PaymentBehavior before) {
        return after.getTotal() > before.getTotal() * SIGNIFICANT_BETTER_TOTAL &&
        after.averageBet > before.averageBet * BETTER_AVERAGE_BET;
    }


    private PaymentBehavior getBefore(Payment payment, Connection connection) {

        String sql = "select sum(totalWager)/sum(spins) as 'average bet', sum(spins) as 'spins' from game_session where facebookId = '"+payment.facebookId+"' " +
        "and timeStamp > date_sub('"+payment.timeStamp.toString()+"', interval 1 day) " +
        "and timeStamp < '"+ payment.timeStamp.toString()+"'";

        return getBehaviour(sql, connection);
    }

    private PaymentBehavior getAfter(Payment payment, Connection connection) {

        String sql = "select sum(totalWager)/sum(spins) as 'average bet', sum(spins) as 'spins' from game_session where facebookId = '"+payment.facebookId+"' " +
        "and timeStamp < date_add('"+payment.timeStamp.toString()+"', interval 1 day) " +
        "and timeStamp > '"+ payment.timeStamp.toString()+"'";

        return getBehaviour(sql, connection);
    }


    private PaymentBehavior getBehaviour(String sql, Connection connection) {



        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            resultSet.next();

            PaymentBehavior behaviour = new PaymentBehavior(
            resultSet.getInt( 1 ),
            resultSet.getInt( 2 ));

            resultSet.close();
            statement.close();

            return behaviour;

        }catch(SQLException e){

            System.out.println("Error accessing data in database with the query:\n" + sql);
            e.printStackTrace();
        }

        return null;
    }


    public int getBehaviorForPlayer(String playerId){

        PaymentTable table = new PaymentTable();
        return UNKNOWN_BEHAVIOUR;       //TODO: Not implemented

    }

}
