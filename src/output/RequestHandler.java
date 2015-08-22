package output;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/*************************************************************************
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2014-10-06
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */

public class RequestHandler {


    private String targetURL;

    public RequestHandler(String targetURL){

        this.targetURL = targetURL;
    }

    /*************************************************************
     *
     *          Perform a POST call to the target URL
     *
     *
     * @param urlParameters           - the parameters
     * @return                        - page content
     */

    public String executePost(String urlParameters){

        URL url;
        HttpURLConnection connection = null;

        System.out.println("Trying to POST: " + targetURL + "?"+ urlParameters);

        try {
          //Create connection
          url = new URL(targetURL);
          connection = (HttpURLConnection)url.openConnection();
          connection.setRequestMethod("POST");
          connection.setRequestProperty("Content-Type",
               "application/x-www-form-urlencoded");

          connection.setRequestProperty("Content-Length", "" +
                   Integer.toString(urlParameters.getBytes().length));
          connection.setRequestProperty("Content-Language", "en-US");

          connection.setUseCaches (false);
          connection.setDoInput(true);
          connection.setDoOutput(true);

          //Send request
          DataOutputStream wr = new DataOutputStream (
                      connection.getOutputStream ());
          wr.writeBytes (urlParameters);
          wr.flush ();
          wr.close ();

          //Get Response
          InputStream is = connection.getInputStream();
          BufferedReader rd = new BufferedReader(new InputStreamReader(is));
          String line;
          StringBuilder response = new StringBuilder();
          while((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
          }
          rd.close();
          return response.toString();

        } catch (Exception e) {

            e.printStackTrace(System.out);
            return null;

        } finally {

          if(connection != null) {
            connection.disconnect();
          }
        }
      }

}
