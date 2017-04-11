package BeerFinder;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;

/**
 * This class is used as a delegate to persist the client Activity on the 4rd party MongoDB instance.
 * In particular, two functions are used that generate an XML message and sent the update to the endpoint.
 * @author lukasmohs
 */
public class AnalyticsConnection {
    // MongoDB credentials
    private static String mLABAPIKey = "Jq17pqvUE3A3sDLkHCN0TKr7rmzyVa7l";
    private static String MONGODBNAME = "beerfinder";
    private static String MONGODBCOLLECTIONNAME = "activity";
    
    /**
     * This function is invokes to first wrap all provided arguments into a JSON message and to then sent it to the
     * REST endpoint of the 3rd party MongoDB instance provider mLab
     * @param timeStamp
     * @param lat
     * @param lon
     * @param radius
     * @param os
     * @param numberOfAnswers
     * @param device 
     */
    public static void logActivity(String timeStamp, String lat, String lon, String radius, String os, String numberOfAnswers, String device) {
        
        int status;
        // First, a JSON formatted message is created out of all provided information
        String JSONMessage = createXMLMessage(timeStamp, lat, lon, radius, device, os, numberOfAnswers);
        
        try {  
            // Make call to a particular URL
            URL url = new URL("https://api.mlab.com/api/1/databases/"
                    +MONGODBNAME+"/collections/"+MONGODBCOLLECTIONNAME+"?apiKey=" +mLABAPIKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // set request method to POST and send name value pair
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            // write to POST data area
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(JSONMessage);
            out.close();
            // get HTTP response code sent by server
            status = conn.getResponseCode();
            //close the connection
            conn.disconnect();
        } 
        // handle exceptions
        catch (MalformedURLException e) {
            System.out.println("A format exception was thrown");  
        } 
        catch (IOException e) {
            System.out.println("An IO Exception was thrown");
        }
    }
    
    private static String createXMLMessage(String timeStamp, String lat, String lon, String radius, String device, String os, String numberOfAnswers) {

        // Create root JSONObject
        JSONObject root = new JSONObject();
        // Put all the values as fields into JSONObject
        root.put("timeStamp", timeStamp);
        root.put("lat", lat);
        root.put("lon", lon);
        root.put("radius", radius);
        root.put("os", os);
        root.put("device", device);
        root.put("numberOfAnswers", numberOfAnswers);
        // Return String representation of JSONObject
        return root.toString();
    }
}
