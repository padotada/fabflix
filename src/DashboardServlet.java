import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/_dashboard")
public class DashboardServlet extends HttpServlet{
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws IOException{
        response.sendRedirect(request.getContextPath() + "/_dashboard.html");
    }
}
