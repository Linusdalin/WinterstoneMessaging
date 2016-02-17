package remoteData.dataObjects;


import dbManager.DatabaseException;

import java.io.IOException;
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
    protected Statement statement;

    public GenericTable(String getSQL, String restriction, int limit) {

        this.getSQL = getSQL;
        this.restriction = restriction;

        this.maxLimit = limit;
    }

    public void setOrder(String order){

        this.order = order;
    }


    public void load(Connection connection) throws DatabaseException {

        load(connection, restriction, "ASC", -1);
    }

    public void load(Connection connection, String restriction) throws DatabaseException {

        load(connection, restriction, "ASC", -1);

    }

    public void loadAndRetry(Connection connection, String restriction, String order, int limit){

        boolean retry = false;

        do{

            String queryString = getQueryString(restriction, limit, -1, order);
            try {

                //System.out.println("QueryString: " + queryString);
                loadFromDB(connection, queryString);

            } catch (DatabaseException e) {
                e.printStackTrace();
                System.out.println("Error reading from database. Retry?\n>");
                waitReturn();
                retry = true;
            }

        }while(retry);

    }


    public void load(Connection connection, String restriction, String order, int limit) throws DatabaseException {

        String queryString = getQueryString(restriction, limit, -1, order);
        loadFromDB(connection, queryString);
    }



    public void load(Connection connection, String restriction, String order, int limit, int offset) throws DatabaseException {

        String queryString = getQueryString(restriction, limit, offset, order);
        //System.out.println("Query: " + queryString);

        loadFromDB(connection, queryString);
    }



    protected void loadFromDB(Connection connection, String queryString)throws DatabaseException {

        if(connection == null){

            System.out.println("Using dummy database... No loading");
        }

        try{

            if(statement != null && !statement.isClosed())
                statement.close();

            //System.out.println("Query: " + queryString);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(queryString);
            //statement.close();

        }catch(SQLException e){

            System.out.println("Error accessing data in database with the query:\n" + queryString);
            e.printStackTrace();
            throw new DatabaseException();

        }catch(Exception e){

            System.out.println("Communication fail\n" + queryString);
            e.printStackTrace();
            throw new DatabaseException();
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

            if(resultSet != null)
                resultSet.close();

            if(statement != null)
                statement.close();


        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public static void waitReturn() {
        try {

            System.in.read();

        } catch (IOException e) {

            System.out.println("Error getting input. Ignoring");
        }

    }


}
