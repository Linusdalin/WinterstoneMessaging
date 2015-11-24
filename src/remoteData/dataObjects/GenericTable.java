package remoteData.dataObjects;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*******************************************************************************************'
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-04-21
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */

public class GenericTable {

    protected ResultSet resultSet;
    private final String getSQL;
    private String restriction = "";
    protected int maxLimit = -1;
    protected String order = "DESC";
    private Statement statement;

    public GenericTable(String getSQL, String restriction, int limit) {

        this.getSQL = getSQL;
        this.restriction = restriction;

        this.maxLimit = limit;
    }

    public void setOrder(String order){

        this.order = order;
    }


    public void load(Connection connection){

        load(connection, restriction, "ASC", -1);
    }

    public void load(Connection connection, String restriction){

        load(connection, restriction, "ASC", -1);

    }

    public void load(Connection connection, String restriction, String order, int limit){

        String queryString = getQueryString(restriction, limit, -1, order);

        //System.out.println("Query: " + queryString);

        loadFromDB(connection, queryString);
    }


    public void load(Connection connection, String restriction, String order, int limit, int offset){

        String queryString = getQueryString(restriction, limit, offset, order);
        //System.out.println("Query: " + queryString);

        loadFromDB(connection, queryString);
    }



    protected void loadFromDB(Connection connection, String queryString) {

        if(connection == null){

            System.out.println("Using dummy database... No loading");
        }

        try{

            //System.out.println("Query: " + queryString);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(queryString);

        }catch(SQLException e){

            System.out.println("Error accessing data in database with the query:\n" + queryString);
            e.printStackTrace();
        }
    }



    public String getQueryString(String restriction, int limit, int offset, String order){

        return getQueryString(getSQL, restriction, limit, offset, order);
    }



    protected String getQueryString(String query, String restriction, int limit, int offset, String order) {

        query = query.replaceAll("-RESTRICTION-",  restriction);

        if(limit != -1)
            if(offset != -1)
                query = query.replaceAll("-LIMIT-", "LIMIT " + offset + ", "+limit);
            else
                query = query.replaceAll("-LIMIT-", "LIMIT "+limit);
        else
            query = query.replaceAll("-LIMIT-", "");

        query = query.replaceAll("-ORDER-", order);

        return query;
    }


    public void close() {

        try {

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }



}
