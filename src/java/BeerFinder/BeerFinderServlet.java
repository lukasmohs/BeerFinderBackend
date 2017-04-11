package BeerFinder;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class extends a HTTPServlet and overrides the corresponding doGet() method to answer
 * client requests for Bars in the provided location.
 * @author lukasmohs
 */
@WebServlet(name = "BeerFinder",
        urlPatterns = {"/getBeer"})
public class BeerFinderServlet extends HttpServlet {
    
    /**
     * This method response to HTTP GET requests to the "/getBeer" endpoint and responds with a
     * JSON formatted message containing a list of bars for the provided parameters
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
     @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        String res = "";
        // Instantiate API Connection
        APIConnection apiConnection = new APIConnection();
        // Retrieve request GET parameters and save as Strings
        String os = request.getParameter("os");
        String device = request.getParameter("device");
        String lat = request.getParameter("lat");
        String lon = request.getParameter("lon");
        String radius = request.getParameter("radius");
        // Check if all required parameters are present and not empty
        if(lat != null && lon != null && radius != null && os != null && device != null &&
                !lat.isEmpty()&&!lon.isEmpty()&&!radius.isEmpty()&&!os.isEmpty() && !device.isEmpty()) {
            // Delegate the request and save JSON formatted client response as String
            res = apiConnection.getBars(lat, lon, radius, os, device);
            // Set status to 200
            response.setStatus(200);
        } else {
            // Alternatively set the response to a descriptive message
            res = "Please check the provided parameters";
            // Set status code to 404 ( Not found )
            response.setStatus(404);
        }
        PrintWriter out = response.getWriter(); 
        // Send response message to the client
        out.println(res);
    }
}
