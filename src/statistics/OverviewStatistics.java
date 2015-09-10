package statistics;

import campaigns.CampaignInterface;
import campaigns.CampaignRepository;
import dbManager.ConnectionHandler;

import java.sql.*;
import java.util.Calendar;
import java.util.List;

/***********************************************************************************''
 *
 *          Statistics output
 *                      Churnpoke               Activation
 *                      Tot
 *          2015-08-01  100 12 12%
 *                  1                 47 6 15%
 *                  2                 53 2  4%
 *
 *          2015-08-02
 *
 */
public class OverviewStatistics {

    private static final int DAYS = 10;

    /***********************************************************
     *
     *          Get and display the latest exposure and session statistics
     *
     * @param args     -
     */

    public static void main(String[] args){


        OverviewStatistics generator = new OverviewStatistics();
        generator.generateOverview(DAYS);
    }

    private void generateOverview(int days){

        Connection localConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);
        Connection remoteConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.remote);

        Calendar calendar = Calendar.getInstance();
        Timestamp executionTime = new java.sql.Timestamp(calendar.getTime().getTime());
        CampaignRepository repository = new CampaignRepository();
        List<CampaignInterface> campaigns = repository.getActiveCampaigns();

        printHeadline( campaigns );


        for(int count = days-1; count >= 0; count--){

            Timestamp day = new Timestamp(executionTime.getTime() - (long)count * 24*3600*1000);
                 System.out.print(day.toString().substring(0, 10) + ": ");

            for (CampaignInterface campaign : campaigns) {

                printCampaignOverview(day, campaign, localConnection, remoteConnection);
            }

            System.out.println("");
        }


    }

    /***************************************************************************************
     *
     *              The overview for a campaign is the number of sent messages and the number of sessions tagged with the promo code
     *
     *
     * @param day                   - the day
     * @param campaign              - the campaign
     * @param localConnection       - the connection to the local database (for exposure)
     * @param remoteConnection      - the connection to teh remote database (for tagged sessions)
     */


    private void printCampaignOverview(Timestamp day, CampaignInterface campaign, Connection localConnection, Connection remoteConnection) {

        int sent        = getSentMessages(day, campaign, localConnection);
        int sessions    = getSessions(day, campaign, remoteConnection);
        int ctr = (100*sessions)/sent;

        System.out.print(sent+"\t"+sessions+"\t"+ ctr+"%\t\t");

    }

    private static void printHeadline(List<CampaignInterface> campaigns) {

        System.out.print("\t\t\t");

        for (CampaignInterface campaign : campaigns) {

            System.out.print(campaign.getShortName() + "\t\t\t");

        }
        System.out.println("\n");

    }


    private static int getSentMessages(Timestamp day, CampaignInterface campaign, Connection connection){

        String sql = "select count(*) from exposure where date(timestamp) = '"+ day.toString().substring(0, 10)+"' and promoCode like ('%"+campaign.getTag()+"%')";

        try{

            //System.out.println("Query: " + sql);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            if(resultSet == null)
                return 0;

            if(!resultSet.next())
                return 0;

            return resultSet.getInt( 1 );

        }catch(SQLException e){

            System.out.println("Error accessing data in database with the query:\n" + sql);
            e.printStackTrace();
        }


        return 0;


    }

    private int getSessions(Timestamp day, CampaignInterface campaign, Connection connection){

        String sql = "select count(*) from sessions where date(sessions.timestamp) = '"+ day.toString().substring(0, 10)+"' and promoCode like ('%"+campaign.getTag()+"%')";

        try{

            //System.out.println("Query: " + sql);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            if(resultSet == null)
                return 0;

            if(!resultSet.next())
                return 0;

            return resultSet.getInt( 1 );

        }catch(SQLException e){

            System.out.println("Error accessing data in database with the query:\n" + sql);
            e.printStackTrace();
        }


        return 0;

    }

}
