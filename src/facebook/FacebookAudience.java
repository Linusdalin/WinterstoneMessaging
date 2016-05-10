package facebook;

import campaigns.CampaignInterface;
import dbManager.DatabaseException;
import localData.Exposure;
import localData.ExposureTable;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/******************************************************************
 *
 *              Create a facebook audience for failed communication
 *
 *              // Todo: Add flag for each campaign to decide if it is important enough to create audience for (for now testing with all)
 */


public class FacebookAudience {

    List<Exposure> failedExposures = null;

    public FacebookAudience(){

    }

    public void load(Connection connection, int hoursBack){

        try {

            ExposureTable table = new ExposureTable(connection);
            table.load(connection, "and exposureTime > date_sub(current_time(), INTERVAL "+ hoursBack+" HOUR) and success = 0");
            failedExposures = table.getAll();

        } catch (DatabaseException e) {
            e.printStackTrace();

        }

    }


    public String toString(List<CampaignInterface> campaigns, boolean toSQL){

        StringBuilder sql = new StringBuilder();

        sql.append("/***************************************************\n");
        sql.append("\n\n     Generated script for facebook audience for failed communication\n\n");
        sql.append(" *****/\n\n");

        int count = 0;

        List<String> unique = new ArrayList<>();


        for (CampaignInterface campaign : campaigns) {

            sql.append(campaign.getName() + " = (\n\n");

            for (Exposure failedExposure : failedExposures) {

                if(campaign.getName().equals(failedExposure.campaignName)  && !failedExposure.facebookId.startsWith("ap_") && !failedExposure.facebookId.startsWith("go_")){

                    if(!unique.contains(failedExposure.facebookId)){

                        unique.add(failedExposure.facebookId);
                        if(toSQL)
                            sql.append("  '" + failedExposure.facebookId + "', \n");
                        else
                            sql.append("  " + failedExposure.facebookId + "\n");
                        count++;
                    }

                }

            }

            sql.append("); /* " + campaign.getName() + " " + count +  " facebook Id */\n\n\n");

        }

        return sql.toString();

    }


}
