package dbManager;

import java.sql.Connection;
import java.sql.DriverManager;

/*********************************************************
 *
 *              Handling connection to different databases
 *
 *
 */


public class ConnectionHandler {

    public enum Location { local, remote, dummy }

    public static Connection getConnection(Location location){

        Connection connection = null;

        try{

            Class.forName("com.mysql.jdbc.Driver");

            switch (location) {

                case local:

                    if(connection == null || connection.isClosed()){

                        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/slotamerica", "root", "12Twatwaommwl!");
                        if(connection == null)
                            throw new RuntimeException("Could not open connection to database");
                    }

                    //System.out.println(" -- Connected to local database!");
                    return connection;

                case remote:

                    if(connection == null || connection.isClosed()){

                        connection = DriverManager.getConnection("jdbc:mysql://db.slot-america.com:3306/slotamerica?zeroDateTimeBehavior=convertToNull", "linus", "KqgiC84Jwf$#");
                        if(connection == null)
                            throw new RuntimeException("Could not open connection to database");
                    }

                    System.out.println(" -- Connected to remote database!");
                    return connection;

                default:
                    throw new RuntimeException("Could not open " + location.name() + " to database");
            }

        }catch(Exception e){

            e.printStackTrace();

        }

        return null;
    }

}
