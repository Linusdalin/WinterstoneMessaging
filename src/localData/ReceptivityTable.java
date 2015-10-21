package localData;

import receptivity.ReceptivityProfile;
import remoteData.dataObjects.GenericTable;

import java.sql.*;

/*******************************************************************************
 *
 *
 *                  Storing a receptivity profile in the database
 *
 */

public class ReceptivityTable extends GenericTable {

    private static final String getRemote =
            "select * from receptivity where 1=1 -RESTRICTION- order by lastUpdate -ORDER- -LIMIT-";


    public ReceptivityTable(String restriction, int limit){

        super(getRemote, restriction, limit);
        maxLimit = limit;
    }

    public ReceptivityTable(){

        this( "", -1);
    }



    public ReceptivityProfile getNext(){

        try {
            if(!resultSet.next())
                return null;

            String facebookId = resultSet.getString( 1 );
            Timestamp lastUpdate = resultSet.getTimestamp( 2 );

            // Create the profile from hardcoded interpretation of the data in the record

            int[][] profile = {
                    {resultSet.getInt( 3), resultSet.getInt( 4), resultSet.getInt( 5) },
                    {resultSet.getInt( 6), resultSet.getInt( 7), resultSet.getInt( 8) },
                    {resultSet.getInt( 9), resultSet.getInt(10), resultSet.getInt(11) },
                    {resultSet.getInt(12), resultSet.getInt(13), resultSet.getInt(14) },
                    {resultSet.getInt(15), resultSet.getInt(16), resultSet.getInt(17) },
                    {resultSet.getInt(18), resultSet.getInt(19), resultSet.getInt(20) },
                    {resultSet.getInt(21), resultSet.getInt(22), resultSet.getInt(23) }
            };


            return new ReceptivityProfile( facebookId, profile, lastUpdate);


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }



    public void store(ReceptivityProfile profile, Connection connection) {

        String insertQuery = "INSERT INTO receptivity VALUES ('" + profile.getUserId() + "', '0000-00-00', "+

                profile.profile[0][0] + ", " +
                profile.profile[0][1] + ", " +
                profile.profile[0][2] + ", " +

                profile.profile[1][0] + ", " +
                profile.profile[1][1] + ", " +
                profile.profile[1][2] + ", " +

                profile.profile[2][0] + ", " +
                profile.profile[2][1] + ", " +
                profile.profile[2][2] + ", " +

                profile.profile[3][0] + ", " +
                profile.profile[3][1] + ", " +
                profile.profile[3][2] + ", " +

                profile.profile[4][0] + ", " +
                profile.profile[4][1] + ", " +
                profile.profile[4][2] + ", " +

                profile.profile[5][0] + ", " +
                profile.profile[5][1] + ", " +
                profile.profile[5][2] + ", " +

                profile.profile[6][0] + ", " +
                profile.profile[6][1] + ", " +
                profile.profile[6][2] + ")"

                ;

        try{

            Statement statement = connection.createStatement();
            System.out.println("Storing with " + insertQuery);
            statement.execute(insertQuery);

        } catch (SQLException e) {


            System.out.println(e.getMessage());

        }

    }


    public void update(ReceptivityProfile profile, Connection connection) {

        String query = "UPDATE receptivity set " +
                "lastUpdate='"+ profile.getLastUpdate().toString()+"'," +

                "sundayDay=" + profile.profile[0][0] + ", " +
                "sundayEvening=" +  profile.profile[0][1] + ", " +
                "sundayNight=" +  profile.profile[0][2] + ", " +

                "mondayDay=" + profile.profile[1][0] + ", " +
                "mondayEvening=" + profile.profile[1][1] + ", " +
                "mondayNight=" + profile.profile[1][2] + ", " +

                "tuesdayDay=" + profile.profile[2][0] + ", " +
                "tuesdayEvening=" + profile.profile[2][1] + ", " +
                "tuesdayNight=" + profile.profile[2][2] + ", " +

                "wednesdayDay=" + profile.profile[3][0] + ", " +
                "wednesdayEvening=" + profile.profile[3][1] + ", " +
                "wednesdayNight=" + profile.profile[3][2] + ", " +

                "thursdayDay=" + profile.profile[4][0] + ", " +
                "thursdayEvening=" + profile.profile[4][1] + ", " +
                "thursdayNight=" + profile.profile[4][2] + ", " +

                "fridayDay=" + profile.profile[5][0] + ", " +
                "fridayEvening=" + profile.profile[5][1] + ", " +
                "fridayNight=" + profile.profile[5][2] + ", " +

                "saturdayDay=" + profile.profile[6][0] + ", " +
                "saturdayEvening=" + profile.profile[6][1] + ", " +
                "saturdayNight=" + profile.profile[6][2]
                    +" where facebookId = '"+ profile.getUserId()+"'"

                ;

        System.out.println("Updating with " + query );

        try{

            Statement statement = connection.createStatement();
            statement.execute(query);

        } catch (SQLException e) {


            System.out.println(e.getMessage());

        }

    }


    public Timestamp getLast(Connection connection){

        String sql = "select lastUpdate from receptivity order by lastUpdate desc limit 1";

        try{

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            if(!rs.next())
                return null;  // There are no entries in the database

            return rs.getTimestamp(1);

        }catch(SQLException e){

            System.out.println("Error accessing data in database");
            e.printStackTrace();
        }


        return null;
    }

    public ReceptivityProfile getReceptivityForPlayer(String facebookId, Connection connection) {


        load(connection, "and facebookId = '"+facebookId+"'");
        return getNext();
    }
}
