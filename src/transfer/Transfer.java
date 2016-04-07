package transfer;

import dbManager.ConnectionHandler;
import dbManager.DatabaseException;
import remoteData.dataObjects.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**************************************************************************
 *
 *          Transfer payments and sessions
 *
 */

public class Transfer {

    private static final int MAX_RECORDS = 1000000;     // Max records at a time

    Connection localConnection = null;
    Connection remoteConnection = null;

    public static void main(String[] args){

        Transfer transfer = new Transfer();
        transfer.executeTransfer();

    }


    public void executeTransfer(){

        System.out.println("*********************************************************\n* Transferring data from the main database to local mirror...");

        paymentTransfer();
        sessionTransfer();
        //close();


    }


    private void close() {

        if(remoteConnection != null){

            try {
                remoteConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(localConnection != null){

            try {
                localConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    public Transfer(){

        localConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);

         if(localConnection == null){

             System.out.println("failed local Connection");
             return;
         }
        //System.out.println("Local Connection established");

        remoteConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.remote);

        if(remoteConnection == null){

            System.out.println("failed remote Connection");

        }


    }

    /********************************************************************
     *
     *
     *          Read from payments and write locally
     *
     *
     */


    private void paymentTransfer(){

        System.out.println("\n ****************************************************\n * Starting transfer of payments");

        try{


            PaymentTable table = new PaymentTable(  );
            PaymentAnalyser analyser = new PaymentAnalyser();
            Timestamp last = table.getLast(localConnection);

            if(last == null){
                last = Timestamp.valueOf("2014-01-01 00:00:00");
            }
            System.out.println(" -- Last entry is: " + last.toString());

            table.loadRemote(last, MAX_RECORDS, remoteConnection);

            Payment payment = table.getNext();

            int count = 0;

            while(payment!= null){

                analyser.updateForSize(payment, localConnection);
                analyser.setUnknownBehaviour(payment, localConnection);

                payment.store(localConnection);
                payment = table.getNext();
                count++;

                if(count % 100 == 0)
                    System.out.println(" -- Processed: " + count + "...");


            }

            System.out.println(" -- A total of " + count + " payments transferred. Now anlaysing behavior of old payments.");
            int updated = analyseBehaviour(localConnection);
            System.out.println(" -- Analysed " + updated + " old payments.");

        }catch(Exception e){

            e.printStackTrace();

        }
    }


    /*****************************************************************************************'
     *
     *          For a first payment store it to match with the players campaigns
     *
     *
     * @param payment       -   the payment
     */


    private void handleFirstPayment(Payment payment, User user) {



        System.out.println(" !! First payment for player " + payment.facebookId + " but this is not implemented...");


    }

    /*****************************************************************
     *
     *          Check if the payment is the first for the player
     *
     *          If there are no existing payments it is the first
     *
     * @param payment     - the payment
     * @return            - first or not
     */

    private boolean isFirstPayment(Payment payment, Connection connection) {


        try {

            PaymentTable table = new PaymentTable();
            table.load(connection, "playerId = '" + payment.facebookId + "'");
            Payment existingPayment = table.getNext();


            return (existingPayment == null);


        } catch (DatabaseException e) {

            e.printStackTrace();
            return false;
        }


    }


    /***********************************************************************'
     *
     *          Read sessions and store locally
     *
     */



    private void sessionTransfer(){

        System.out.println("\n **********************************************\n * Starting transfer of sessions");

        try{


            GameSessionTable table = new GameSessionTable(  );

            Timestamp last = table.getLast(localConnection);
            System.out.println(" -- Last entry is: " + last.toString());

            table.loadRemote(last, MAX_RECORDS, remoteConnection);

            GameSession session = table.getNext();

            int count = 0;

            while(session != null){

                session.store(localConnection);
                session = table.getNext();
                count++;

                if(count % 2000 == 0)
                    System.out.println(" -- Processed: " + count + "... (" + session.timeStamp.toString() + ")");


            }

            System.out.println(" -- A total of " + count + " game sessions transferred");

        }catch(Exception e){

            e.printStackTrace();

        }
    }


    /***********************************************************************
     *
     *          Analysing old payments to see if the behaviour changed
     *
     * @param connection
     * @return
     */


    private int analyseBehaviour(Connection connection) {

        PaymentTable table = new PaymentTable();
        PaymentAnalyser analyser = new PaymentAnalyser();

        List<Payment> unAnalysedPayments = table.getUnAnalysedPayments(connection);

        System.out.println("Found " + unAnalysedPayments.size() + " payments with unknown behaviour");

        for (Payment payment : unAnalysedPayments) {

            payment.behavior = analyser.getBehaviour(payment, table, connection);
            payment.ordinal  = analyser.getOrdinal(payment, connection);
            payment.updateBehaviour(connection);
        }

        return unAnalysedPayments.size();

    }


}
