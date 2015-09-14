package localData;

import remoteData.dataObjects.GenericTable;
import remoteData.dataObjects.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-04-16
 * Time: 13:17
 * To change this template use File | Settings | File Templates.
 */
public class ResponseTable extends GenericTable {

    private static final String getRemote =
            "select *" +
            "     from response where 1=1" +
            "      $(RESTRICTION)  $(LIMIT)";
    private Connection connection;


    public ResponseTable(String restriction, int limit){

        super(getRemote, restriction, limit);
        maxLimit = limit;
    }

    public ResponseTable(Connection connection){

        this( "", -1);
        this.connection = connection;
    }

    public Response getNext(){

        try {
            if(!resultSet.next())
                return null;

            return new Response(resultSet.getString(1), resultSet.getString(2), resultSet.getInt(3), resultSet.getInt(4), resultSet.getTimestamp(5));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Response> getAll(){

        List<Response> responses = new ArrayList<Response>();
        Response response = getNext();

        while(response != null){

            responses.add(response);
            response = getNext();
        }

        return responses;

    }



}
