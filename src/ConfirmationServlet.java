import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Object confirmationData = request.getSession().getAttribute("confirmationData");

        if (confirmationData == null) {
            response.setStatus(400);

            JsonObject error = new JsonObject();
            error.addProperty("errorMessage", "No confirmation data found.");

            response.getWriter().write(error.toString());
            return;
        }

        response.getWriter().write(confirmationData.toString());
    }
}