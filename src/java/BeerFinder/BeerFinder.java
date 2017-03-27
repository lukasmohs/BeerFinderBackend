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
public class BeerFinder extends HttpServlet {
    
     @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        
        APIConnection apiConnection = new APIConnection();
        
        String res = apiConnection.getBars("40.4446102", "-79.948537", "400");

        PrintWriter out = response.getWriter(); 
        response.setStatus(200);
        // Wrap the status message into XML and send it to the client
        out.println(res);
    }
    
}
