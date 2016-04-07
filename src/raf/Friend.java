package raf;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**********************************************************************
 *
 *          Response contains responses for all different campaigns and message types
 */


public class Friend {


    public final String facebookId;
    public final String referrer;
    public final Timestamp registration;
    public final int compensation;
    public final Timestamp firstPayment;
    public final int compensation2;

    public Friend(String facebookId, String referrer, Timestamp registration, int compensation, Timestamp firstPayment, int compensation2){


        this.facebookId = facebookId;
        this.referrer = referrer;
        this.registration = registration;
        this.compensation = compensation;
        this.firstPayment = firstPayment;
        this.compensation2 = compensation2;
    }

    public String toString(){

        return "(" + facebookId + ", " +referrer + ", " + registration.toString()+ ", " + firstPayment.toString()+  ")";

    }

    private String toSQLValues() {

        return "'" + facebookId + "', '" + referrer + "', '" +registration.toString() + "', " +compensation + ", '" +firstPayment.toString() + "', " + compensation2 ;


    }

    public void store(Connection connection) {

        String insert = "insert into friend values (" + toSQLValues() + ")";

        System.out.println("Insert with: " + insert);

        try{

            Statement statement = connection.createStatement();
            statement.execute(insert);

        }catch(SQLException e){

            System.out.println("Error accessing data in database. SQL:" + insert);
            e.printStackTrace();
        }

    }


    public void updateCount(Connection connection) {

        String update = "update friend set count = count + 1 where user = '"+ facebookId+"'";

        System.out.println("Update with: " + update);

        try{

            Statement statement = connection.createStatement();
            statement.execute(update);

        }catch(SQLException e){

            System.out.println("Error accessing data in database. SQL:" + update);
            e.printStackTrace();
        }
    }
}
