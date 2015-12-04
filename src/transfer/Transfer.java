package transfer;

import dbManager.ConnectionHandler;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.GameSessionTable;
import remoteData.dataObjects.Payment;
import remoteData.dataObjects.PaymentTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

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
        close();


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

            Timestamp last = table.getLast(localConnection);
            System.out.println(" -- Last entry is: " + last.toString());

            table.loadRemote(last, MAX_RECORDS, remoteConnection);

            Payment payment = table.getNext();

            int count = 0;

            while(payment!= null){

                //System.out.println("Got payment: " + payment.toString());
                payment.store(localConnection);

                payment = table.getNext();
                count++;

                if(count % 100 == 0)
                    System.out.println(" -- Processed: " + count + "...");


            }

            System.out.println(" -- A total of " + count + " payments transferred");

        }catch(Exception e){

            e.printStackTrace();

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



}
