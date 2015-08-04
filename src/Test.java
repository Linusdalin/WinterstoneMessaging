import dbManager.ConnectionHandler;
import remoteData.dataObjects.Payment;
import remoteData.dataObjects.PaymentTable;
import remoteData.dataObjects.User;
import remoteData.dataObjects.UserTable;

import java.sql.Connection;
import java.sql.SQLException;

/************************************************************''
 *
 *          Test
 *
 *
 */

public class Test {

    public static void main(String[] arg){

        System.out.println("Open DB");
        Connection dbConnection = null;
        try{

            ConnectionHandler.Location dataSource = ConnectionHandler.Location.local;

            dbConnection = ConnectionHandler.getConnection(dataSource);

            PaymentTable payments = new PaymentTable();
            payments.load(dbConnection);

            Payment payment = payments.getNext();
            int count = 0;

            while(payment != null){

                System.out.println("Got payment of "+ payment.amount+" @" + payment.timeStamp );
                payment = payments.getNext();
                count++;
            }


            System.out.println("Executed " + count + " payments");

            UserTable users = new UserTable();
            users.load(dbConnection);

            User user = users.getNext();
            count = 0;

            while(user != null){

                System.out.println("Got user "+ user.toString());
                user = users.getNext();
                count++;
            }


            System.out.print("Executed " + count + " users");


        }catch(Exception e){

            e.printStackTrace();

        }finally{

            if(dbConnection != null){

                try {
                    dbConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
