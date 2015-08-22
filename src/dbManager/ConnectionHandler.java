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
                    connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/slotamerica", "", "");
                    if(connection == null)
                        throw new RuntimeException("Could not open connection to database");
                    System.out.println(" -- Connected to local database!");
                    break;

                case remote:
                    connection = DriverManager.getConnection("jdbc:mysql://db.slot-america.com:3306/slotamerica?zeroDateTimeBehavior=convertToNull", "linus", "KqgiC84Jwf$#");
                    if(connection == null)
                        throw new RuntimeException("Could not open connection to database");
                    System.out.println(" -- Connected to remote database!");
                    break;

                case dummy:
                    connection = null;
                    System.out.println(" -- Using dummy database");
                    break;
            }

        }catch(Exception e){

            e.printStackTrace();

        }
        return connection;
    }

}
