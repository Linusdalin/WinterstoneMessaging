package tempAnalysis;

import dbManager.ConnectionHandler;
import dbManager.DatabaseException;
import remoteData.dataObjects.Payment;
import remoteData.dataObjects.PaymentTable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*********************************************************************
 *
 *              Go through all payments to see if the player exibits different behaviour before and after
 *
 */


public class PaymentEffect {

    private static final String START = "2016-01-02";
    private static final int SESSIONS = 15000;


    private static final double BETTER_AVERAGE_BET = 2.0;
    private static final double BETTER_TOTAL = 2.5;
    private static final double SIGNIFICANT_BETTER_TOTAL = 3.5;


    public static void main(String[] args){

        PaymentEffect analyser = new PaymentEffect();
        analyser.analyseAll(START, SESSIONS);
    }

    private void analyseAll(String startDate, int sessions) {

        Connection connection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);
        Connection remoteConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.remote);
        int countTotalUp = 0;
        int countMuchBetter = 0;
        int countBetUp = 0;
        int countConsecutive = 0;
        int count = 0;


        try {

            PaymentTable payments = new PaymentTable();
            payments.load(connection, " and payment.timeStamp >= '" + startDate + "'", "ASC", sessions, -1);      // Restriction for testing

            Payment payment = payments.getNext();


            if(payment == null)
                return;

            while(payment != null){

                System.out.println(" ----------------------------------------------------------\n  " + count++ + "- Evaluating Payment "+ payment.toString());

                if(getPaymentsBefore(payment, connection) > 0){

                    System.out.println(" -- Consecutive Payment. Ignoring this");
                    countConsecutive++;

                }


                Behaviour before = getBefore(payment, connection);
                Behaviour after = getAfter(payment, connection);

                System.out.println(" -- Before: " + before.toString());
                System.out.println(" -- After:  " + after.toString());

                if(isMuchBetter(after, before)){

                    System.out.println(" !! MuchBetter!");
                    countMuchBetter++;

                }else if(isBetterTotal(after, before)){

                    System.out.println(" !! Higher Total!");
                    countTotalUp++;

                }else if(isBetterAverage(after, before)){

                    System.out.println(" !! Higher Bet!");
                    countBetUp++;
                }


                payment = payments.getNext();
            }

        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        System.out.println("Total Ignored  : " + countConsecutive + " (" + (100* countConsecutive / (count))  + "%)");
        System.out.println("Significant    : " + countMuchBetter + " (" + (100* countMuchBetter / (count - countConsecutive))  + "%)");
        System.out.println("Total Increased: " + countTotalUp + " (" + (100* countTotalUp / (count - countConsecutive))  + "%)");
        System.out.println("Bet   Increased: " + countBetUp + " (" + (100* countBetUp /(count - countConsecutive)) + "%)");
        System.out.println("Done!");

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

            resultSet.close();
            statement.close();

            return noPayments;

        }catch(SQLException e){

            System.out.println("Error accessing data in database with the query:\n" + sql);
            e.printStackTrace();
        }

        return 0;
    }


    private boolean isBetterAverage(Behaviour after, Behaviour before) {
        return after.averageBet > before.averageBet * BETTER_AVERAGE_BET;
    }

    private boolean isBetterTotal(Behaviour after, Behaviour before) {
        return after.getTotal() > before.getTotal() * BETTER_TOTAL;
    }

    private boolean isMuchBetter(Behaviour after, Behaviour before) {
        return after.getTotal() > before.getTotal() * SIGNIFICANT_BETTER_TOTAL &&
                after.averageBet > before.averageBet * BETTER_AVERAGE_BET;
    }


    private Behaviour getBefore(Payment payment, Connection connection) {

        String sql = "select sum(totalWager)/sum(spins) as 'average bet', sum(spins) as 'spins' from game_session where facebookId = '"+payment.facebookId+"' " +
                "and timeStamp > date_sub('"+payment.timeStamp.toString()+"', interval 1 day) " +
                "and timeStamp < '"+ payment.timeStamp.toString()+"'";

        return getBehaviour(sql, connection);
    }

    private Behaviour getAfter(Payment payment, Connection connection) {

        String sql = "select sum(totalWager)/sum(spins) as 'average bet', sum(spins) as 'spins' from game_session where facebookId = '"+payment.facebookId+"' " +
                "and timeStamp < date_add('"+payment.timeStamp.toString()+"', interval 1 day) " +
                "and timeStamp > '"+ payment.timeStamp.toString()+"'";

        return getBehaviour(sql, connection);
    }


    private Behaviour getBehaviour(String sql, Connection connection) {



        try{

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            resultSet.next();

            Behaviour behaviour = new Behaviour(
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


    private class Behaviour {

        private final int averageBet;
        private final int spins;

        Behaviour(int averageBet, int spins){


            this.averageBet = averageBet;
            this.spins = spins;
        }

        public String toString(){

            return "Bet:" + averageBet + " Spins:" + spins + " (Total: "+ getTotal()/1000+")";
        }

        private int getTotal() {

            return averageBet * spins;
        }

    }
}
