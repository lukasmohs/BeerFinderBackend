package BeerFinder;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author lukasmohs
 */
@WebServlet(name = "BeerFinder",
        urlPatterns = {"/getBeer"})
public class BeerFinderServlet extends HttpServlet {
    
     @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        String res = "";
        APIConnection apiConnection = new APIConnection();
        String os = request.getParameter("os");
        String device = request.getParameter("device");
        String lat = request.getParameter("lat");
        String lon = request.getParameter("lon");
        String radius = request.getParameter("radius");
        if(lat != null && lon != null && radius != null && os != null && device != null &&
                !lat.isEmpty()&&!lon.isEmpty()&&!radius.isEmpty()&&!os.isEmpty() && !device.isEmpty()) {
            res = apiConnection.getBars(lat, lon, radius, os, device);
            response.setStatus(200);
        } else {
            res = "Please check the provided parameters";
            response.setStatus(404);
        }
        

        PrintWriter out = response.getWriter(); 

        // Wrap the status message into XML and send it to the client
        out.println(res);
    }
    
}
