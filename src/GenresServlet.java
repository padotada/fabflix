import MongoRetrieval.GenrePojo;
import MongoRetrieval.MongoGenres;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/genres")
public class GenresServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter jsonOutput = response.getWriter();

        try {
            MongoGenres mongoGenres = new MongoGenres();
            List<GenrePojo> genrePojos = mongoGenres.retrieveGenres();
            JsonObject genreList = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            JsonArray genres = new JsonArray();
            for (GenrePojo genre : genrePojos) {
                genres.add(genre.getGenreName());
            }
            genreList.add("genres", genres);
            jsonArray.add(genreList);
            jsonOutput.write(jsonArray.toString());
            response.setStatus(200);
        } catch (Exception e) {
            request.getServletContext().log("MongoDB genres retrieval error: ", e);
            response.setStatus(500);
            // error is stored in json object
            JsonObject error = new JsonObject();
            error.addProperty("errorMessage", e.getMessage());
            jsonOutput.write(error.toString());
        } finally {
            jsonOutput.close();
        }
    }
}


