package BeerFinder;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;

/**
 *
 * @author lukasmohs
 */
public class AnalyticsConnection {
    private static String mLABAPIKey = "Jq17pqvUE3A3sDLkHCN0TKr7rmzyVa7l";
    
    public static void logActivity(String timeStamp, String lat, String lon, String radius, String os, String numberOfAnswers, String device) {
        
        int status;
        String JSONMessage = createXMLMessage(timeStamp, lat, lon, radius, device, os, numberOfAnswers);
        
        try {  
                // Make call to a particular URL
		URL url = new URL("https://api.mlab.com/api/1/databases/beerfinder/collections/activity?apiKey=" +mLABAPIKey);
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

        JSONObject root = new JSONObject();

        root.put("timeStamp", timeStamp);
        root.put("lat", lat);
        root.put("lon", lon);
        root.put("radius", radius);
        root.put("os", os);
        root.put("device", device);
        root.put("numberOfAnswers", numberOfAnswers);
        System.out.println("logging to server: " + root.toString());
        return root.toString();
    }
}
