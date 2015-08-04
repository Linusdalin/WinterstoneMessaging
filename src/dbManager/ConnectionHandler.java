package dbManager;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-04-10
 * Time: 13:33
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionHandler {

    public enum Location { local, remote, dummy }

    public static Connection getConnection(Location location){

        Connection connection = null;

        try{

            Class.forName("com.mysql.jdbc.Driver");

            switch (location) {

                case local:
                    connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/winterstone", "", "");
                    if(connection == null)
                        throw new RuntimeException("Could not open connection to database");
                    System.out.println("Connected to local database");
                    break;

                case remote:
                    connection = DriverManager.getConnection("jdbc:mysql://db.slot-america.com:3306/slotamerica", "linus", "KqgiC84Jwf$#");
                    if(connection == null)
                        throw new RuntimeException("Could not open connection to database");
                    System.out.println("Connected to remote database");
                    break;

                case dummy:
                    connection = null;
                    System.out.println("Using dummy database");
                    break;
            }

        }catch(Exception e){

            e.printStackTrace();

        }
        return connection;
    }

}
