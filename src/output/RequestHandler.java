package output;

import java.io.*;
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


    public String executeGet() throws DeliveryException {

        return executeGet(null);

    }


    public String executeGet(String urlParameters) throws DeliveryException {

        String url = targetURL;

        if(urlParameters != null)
            url += "?" + urlParameters;

        System.out.println("Trying to GET: " + url);
        return execute(url, "", "GET");

    }

    public String executePost(String urlParameters) throws DeliveryException {

        System.out.println("Trying to POST: " + targetURL + "with "+ urlParameters);
        return execute(targetURL, urlParameters, "POST");

    }


    /*************************************************************
     *
     *          Perform a POST call to the target URL
        *
     *
     *
        *
        * @param urlParameters           - the parameters
        * @return                        - page content
        */


    /*****************************************************************
     *
     *          Making a request
     *
     * @param targetURL
     * @param urlParameters
     * @param method
     * @return          - last line back
     */

    public String execute(String targetURL, String urlParameters, String method) throws DeliveryException {

        URL url;
        HttpURLConnection connection = null;

        try {
           //Create connection

            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod(method);

            if(method.equals("POST")){

                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                connection.setRequestProperty("Content-Length", "" +
                        Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

            }


          connection.setUseCaches (false);
          connection.setDoInput(true);
          connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                      connection.getOutputStream ());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();

            int httpCode = connection.getResponseCode();

            if(httpCode != 200){

                System.out.println("Got: " + httpCode);
                throw new DeliveryException(httpCode);
            }

          //Get Response
          InputStream is = connection.getInputStream();
          BufferedReader rd = new BufferedReader(new InputStreamReader(is));
          String line;
          StringBuilder response = new StringBuilder();

            while((line = rd.readLine()) != null) {

                response.append(line);
                response.append('\n');

            }

            rd.close();
            return response.toString();

        } catch (IOException e) {

            e.printStackTrace(System.out);
            return null;

        } finally {

          if(connection != null) {
            connection.disconnect();
          }
        }
      }




}
