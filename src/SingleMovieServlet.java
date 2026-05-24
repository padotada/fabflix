import MongoRetrieval.MongoMovies;
import MongoRetrieval.MoviePojo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/single_movie")
public class SingleMovieServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String movieId = request.getParameter("id");
        PrintWriter jsonOutput = response.getWriter();

        try {
            if (movieId == null || movieId.trim().isEmpty()) {
                response.setStatus(400);
                JsonObject error = new JsonObject();
                error.addProperty("errorMessage", "Missing required parameter: id");
                jsonOutput.write(error.toString());
                jsonOutput.close();
                return;
            }
            MongoMovies mongoMovies = new MongoMovies();
            MoviePojo movie = mongoMovies.getSingleMovieById(movieId);
            if (movie == null) {
                response.setStatus(404);
                JsonObject error = new JsonObject();
                error.addProperty("errorMessage", "Movie not found.");
                jsonOutput.write(error.toString());
                return;
            }
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(movie.toJson());
            jsonOutput.write(jsonArray.toString());
            response.setStatus(200);

        } catch (Exception e) {
            request.getServletContext().log("MongoDB single movie retrieval error: ", e);
            response.setStatus(500);

            JsonObject error = new JsonObject();
            error.addProperty("errorMessage", e.getMessage());
            jsonOutput.write(error.toString());

        } finally {
            jsonOutput.close();
        }
    }
}
