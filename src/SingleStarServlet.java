import MongoRetrieval.MongoStars;
import MongoRetrieval.StarPojo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;


@WebServlet("/single_star")
public class SingleStarServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Set response mime type
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String starId = request.getParameter("id");
        PrintWriter jsonOutput = response.getWriter();

        try{
            if (starId == null || starId.trim().isEmpty()) {
                response.setStatus(400);

                JsonObject error = new JsonObject();
                error.addProperty("errorMessage", "Missing required parameter: id");

                jsonOutput.write(error.toString());
                return;
            }
            MongoStars mongoStars = new MongoStars();
            StarPojo star = mongoStars.getStarById(starId);

            if (star == null) {
                response.setStatus(404);

                JsonObject error = new JsonObject();
                error.addProperty("errorMessage", "Star not found.");

                jsonOutput.write(error.toString());
                return;
            }

            JsonArray jsonArray = new JsonArray();
            jsonArray.add(star.toJson());

            jsonOutput.write(jsonArray.toString());
            response.setStatus(200);

        } catch (Exception e) {
            request.getServletContext().log("MongoDB single star retrieval error: ", e);
            response.setStatus(500);

            JsonObject error = new JsonObject();
            error.addProperty("errorMessage", e.getMessage());
            jsonOutput.write(error.toString());

        } finally {
            jsonOutput.close();
        }
    }
}
