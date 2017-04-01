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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author lukasmohs
 */
public class APIConnection {
    
    private static String AUTHENTICATIONURL = "https://api.yelp.com/oauth2/token";
    private static String CLIENTSECRET = "FdzTGMvEVEUNz8DFF8lxxxna0MxERPSV78xgCYbOfdCS3Zzd5Ny81aq4KxMky62F";
    private static String CLIENTID = "vxocNW4lfJ46sqiEG7saXg";
    private static String APIURL = "https://api.yelp.com/v3/businesses/search?term=bar";
    
    
    public static String getBars(String latitude, String longitude, String radius, String os) {
       String res="";
        try {
            String tokenMessage = requestToken();
            System.out.println(tokenMessage);
            String token = getTokenFromJSON(tokenMessage);


            System.out.println("token: " + token);
            res = sendRequestForBars(latitude, longitude, radius, token);
            System.out.println("response: " + res);
            ArrayList barList = parseServerResponseIntoBars(res);
            
            res = createJSONResponse(barList);
            AnalyticsConnection.logActivity(new Date().toString(),latitude, longitude, radius, os);
           
        } catch (Exception ex) {
            Logger.getLogger(APIConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }
    
    private static ArrayList<Bar> parseServerResponseIntoBars(String response) {
        ArrayList barList = new ArrayList<Bar>();
        // Instantiate a JSON Tokener with the provided JSON message from a client in form of a String
        JSONTokener tokener = new JSONTokener(response);
        // Instatiate a JSON object from the Tokenener
        JSONObject js = new JSONObject(tokener);     
        // Loop over all installers in the in the company
        JSONArray ja =  js.getJSONArray("businesses");
        for(int i = 0; i< ja.length(); i++) {
            JSONObject o = ja.getJSONObject(i);
            System.out.println("object " + i + ": " + o.toString());
            barList.add(new Bar(
            o.getString("name"), o.getJSONObject("location").getString("address1"),o.getJSONObject("coordinates").getDouble("latitude") + "",
            o.getJSONObject("coordinates").getDouble("longitude") + "",o.getString("price")));
        }
        return barList;
    }
    
    private static String createJSONResponse(ArrayList<Bar> barList) {
        String response = "";
        
        JSONObject root = new JSONObject();
        
        JSONArray bars = new JSONArray();

        for(Bar bar : barList) {    
            JSONObject o = new JSONObject();
            o.put("name",bar.getName());
            o.put("price", bar.getPrice());
            o.put("lat", bar.getLat());
            o.put("lon", bar.getLon());
            o.put("address", bar.getAddress());
            bars.put(o);
        }
        
        root.put("bars", bars);

        
        return root.toString();
    }
    
    private static String getTokenFromJSON(String message) {
        // Instantiate a JSON Tokener with the provided JSON message from a client in form of a String
        JSONTokener tokener = new JSONTokener(message);
        // Instatiate a JSON object from the Tokenener
        JSONObject js = new JSONObject(tokener);
        // Loop over all installers in the in the company
        return js.getString("access_token");
    }
        
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

        String urlParameters = "client_id=" + CLIENTID
            + "&client_secret=" + CLIENTSECRET 
            + "&grant_type=client_credentials";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
        }
        in.close();

        res = response.toString();
        return res;
    }
    
    private static String sendRequestForBars(String latitude, String longitude, String radius, String token) throws Exception {

		String url = APIURL;
                
                url += "&latitude="+latitude;
                url += "&longitude="+longitude;
                url += "&radius="+radius;

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

                con.setRequestProperty("Authorization", "Bearer "+token);
                
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		return response.toString();

	}


}
