package remoteData.dataObjects;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-04-21
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
public class GenericTable {

    ResultSet resultSet;
    private final String getRemoteSQL;
    private String startTime;
    private String restriction = "";
    int maxLimit = -1;

    public GenericTable(String getRemote, String startTime, String restriction, int limit) {

        this.getRemoteSQL = getRemote;
        this.startTime = startTime;
        this.restriction = restriction;

        this.maxLimit = limit;
    }

    public void load(Connection connection){

        load(connection, getRemoteSQL);
    }


    public void load(Connection connection, String queryString) {

        if(connection == null){

            System.out.println("Using dummy database... No loading");
        }

        try{

            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(queryString);

        }catch(SQLException e){

            System.out.println("Error accessing data in database");
            e.printStackTrace();
        }
    }



    public String getRemoteSQL(String threshold, int limit){

        return getRemoteSQL
                .replace("$(THRESHOLD)", threshold)
                .replace("$(LIMIT)", ""+limit);
    }




}
