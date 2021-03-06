package BeerFinder;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * This class serves the purpose of a delegate for the Yelp API calls initialized by the Servlet.
 * It provides static methods to first request a authentication token: requestToken() 
 * and execute then the API call: sendRequestForBars().
 * Besides that, two parsing methods evaluate the API responses: getTokenFromJSON(), parseServerResponseIntoBars()
 * and one method converts a message to JSON: createJSONResponse().
 * The logActivity() method basically writes all activities to a third party MongoDB instance.
 * @author lukasmohs
 */
public class APIConnection {
    
    // Credentials for Yelp API
    private static String AUTHENTICATIONURL = Credentials.getAUTHENTICATIONURL();
    private static String CLIENTSECRET = Credentials.getCLIENTSECRET();
    private static String CLIENTID = Credentials.getCLIENTID();
    private static String APIURL = Credentials.getAPIURL();
    
    
    /**
     * This method is capable of querying the third party (Yelp) API for Bars by first requesting a token
     * based on application credentials and all of the provided details from the user so that it finally returns
     * a JSON formatted message for the Android client device.
     * The message exchange is realized in JSON and all the request and some response details are sent 
     * to the MongoDB instance for persistence purposes.
     * @param latitude
     * @param longitude
     * @param radius
     * @param os
     * @param device
     * @return JSON formatted String containing the search results
     */
    public static String getBars(String latitude, String longitude, String radius, String os, String device) {
       String res="";
        try {
            // Request the API Authentication token
            String tokenMessage = requestToken();
            String token = getTokenFromJSON(tokenMessage);
            // Query the Yelp API for results based on latitude, longitude and the radius
            res = sendRequestForBars(latitude, longitude, radius, token);
            // Parse the API response to a list of bar entities
            ArrayList barList = parseServerResponseIntoBars(res);
            // Create a JSON formatted response containing all bars that were found
            res = createJSONResponse(barList);
            // Persist the request for analytics pruposes on the MongoDB instance
            AnalyticsConnection.logActivity(new Date().getTime()+"",latitude, longitude, radius, os, barList.size() + "", device);
           
        } catch (Exception ex) {
            System.out.println("An Exception was thrown");
        }
        // Return the JSON formatted client message
        return res;
    }
    
    /**
     * This method takes the response from the Yelp API into entity Bar objects
     * @param response
     * @return the retrieved list of Bars
     */
    private static ArrayList<Bar> parseServerResponseIntoBars(String response) {
        ArrayList barList = new ArrayList<Bar>();
        // Instantiate a JSON Tokener with the provided JSON message from API in form of a String
        JSONTokener tokener = new JSONTokener(response);
        // Instatiate a JSON object from the Tokenener
        JSONObject js = new JSONObject(tokener);     
        // Loop over all bars in the in the "business" array
        JSONArray ja =  js.getJSONArray("businesses");
        for(int i = 0; i< ja.length(); i++) {
            JSONObject o = ja.getJSONObject(i);
            // For each item, create a new Bar object based on the provided information and add it to the list
            String price = "?";
            // In case, no price is provided
            if(o.has("price")) {
                price = o.getString("price");
            }
            barList.add(new Bar(
            o.getString("name"), o.getJSONObject("location").getString("address1"),o.getJSONObject("coordinates").getDouble("latitude") + "",
            o.getJSONObject("coordinates").getDouble("longitude") + "",price));
        }
        // Return the list
        return barList;
    }
    
    /**
     * This method takes a list of bars and creates a JSON message out of it to sent it to the client
     * @param barList
     * @return String representation of JSON formatted list of bar objects
     */
    private static String createJSONResponse(ArrayList<Bar> barList) {
        String response = "";
        // Create root JSONObject
        JSONObject root = new JSONObject();
        // Create JSON Array to add all bars to
        JSONArray bars = new JSONArray();
        // For each bar, create a JSONObject with all vlaues and add it to the bars array
        for(Bar bar : barList) {    
            JSONObject o = new JSONObject();
            o.put("name",bar.getName());
            o.put("price", bar.getPrice());
            o.put("lat", bar.getLat());
            o.put("lon", bar.getLon());
            o.put("address", bar.getAddress());
            bars.put(o);
        }
        
        // Add the bars array to the root  JSONObject
        root.put("bars", bars);
        // Return a String representation
        return root.toString();
    }
    
    /**
     * This method takes the response of the authentication endpoint and retrieves the token out of it
     * @param message
     * @return String representation of Yelp API access token
     */
    private static String getTokenFromJSON(String message) {
        // Instantiate a JSON Tokener
        JSONTokener tokener = new JSONTokener(message);
        // Instatiate a JSON object from the Tokenener
        JSONObject js = new JSONObject(tokener);
        // Get the access_token Strin out of the object and return it
        return js.getString("access_token");
    }
        
    /**
     * This method sends a HTTP request to the authentication URL of the Yelp API by providing
     * the required credentials.
     * @return the  answer message from the authentication endpoint
     * @throws MalformedURLException
     * @throws ProtocolException
     * @throws IOException 
     */
    private static String requestToken() throws MalformedURLException, ProtocolException, IOException{
        String res = null;
        String url = AUTHENTICATIONURL;
        URL obj;
        obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        
        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Cache-Control", "no-cache");
        
        // Add the credentials together in a Strnig of key:value pairs
        String message = "client_id=" + CLIENTID
            + "&client_secret=" + CLIENTSECRET 
            + "&grant_type=client_credentials";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(message);
        wr.flush();
        // Close the connection
        wr.close();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        // read the answer
        while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
        }
        in.close();
        // return the answer as a String
        res = response.toString();
        return res;
    }
    
    /**
     * This method realizes the Yelp API call by sending several parameters via HTTP to the interface endpoint.
     * @param latitude
     * @param longitude
     * @param radius
     * @param token
     * @return String representation of the answer of the server API
     * @throws Exception 
     */
    private static String sendRequestForBars(String latitude, String longitude, String radius, String token) throws Exception {
        String url = APIURL;
        
        // Add the GET parameters to the URL
        url += "&latitude="+latitude;
        url += "&longitude="+longitude;
        url += "&radius="+radius;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        
        // Add authorization property
        con.setRequestProperty("Authorization", "Bearer "+token);
        int responseCode = con.getResponseCode();
        
        // Create BufferedReader
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        // Read the response line per line
        while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
        }
        // CLose reader
        in.close();
        // Return result as String
        return response.toString();
    }
}
