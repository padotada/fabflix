import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.*;

@WebServlet("/single_movie")
public class SingleMovieServlet extends HttpServlet {
    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movieId = request.getParameter("id");

        // Get the PrintWriter for writing response
        PrintWriter jsonOutput = response.getWriter();

        if (movieId == null || movieId.trim().isEmpty()) {
            response.setStatus(500);
            JsonObject error = new JsonObject();
            error.addProperty("errorMessage", "Missing required parameter: id");
            jsonOutput.write(error.toString());
            jsonOutput.close();
            return;
        }
        // Set response mime type
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String movieQuery = """
                SELECT m.id, m.title, m.year, m.director, m.price, r.rating FROM movies m
                LEFT JOIN ratings r ON m.id = r.movie_id WHERE m.id = ?
                """;

        String genreQuery = """
                SELECT g.name
                FROM genres_in_movies gim
                JOIN genres g ON gim.genre_id = g.id
                WHERE gim.movie_id = ?
                ORDER BY g.name
                """;
        String starQuery = """
                SELECT s.id, s.name
                FROM stars_in_movies sim
                JOIN stars s ON sim.star_id = s.id
                WHERE sim.movie_id = ?
                ORDER BY s.name
                """;

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement movieStatement = connection.prepareStatement(movieQuery);
             PreparedStatement genreStatement = connection.prepareStatement(genreQuery);
             PreparedStatement starStatement = connection.prepareStatement(starQuery)) {
            JsonArray jsonArray = new JsonArray();
            movieStatement.setString(1, movieId);
            genreStatement.setString(1, movieId);
            starStatement.setString(1, movieId);

            ResultSet resultSet = movieStatement.executeQuery();

            while (resultSet.next()) {
                String movie_id = resultSet.getString("id");
                String title = resultSet.getString("title");
                String year = resultSet.getString("year");
                String director = resultSet.getString("director");
                String rating = resultSet.getString("rating");
                String movie_price = resultSet.getString("price");

                JsonObject singleMovieObject = new JsonObject();
                singleMovieObject.addProperty("movie_id", movie_id);
                singleMovieObject.addProperty("movie_title", title);
                singleMovieObject.addProperty("movie_year", year);
                singleMovieObject.addProperty("movie_director", director);
                singleMovieObject.addProperty("movie_rating", rating != null ? rating : "N/A");
                singleMovieObject.addProperty("movie_price", movie_price);

                JsonArray genres = new JsonArray();
                try (ResultSet genreResultSet = genreStatement.executeQuery()) {
                    while(genreResultSet.next()){
                        String genreName = genreResultSet.getString("name");
                        genres.add(genreName);
                    }
                }
                singleMovieObject.add("genres", genres);

                JsonArray starsList = new JsonArray();
                try (ResultSet starResultSet = starStatement.executeQuery()) {
                    while (starResultSet.next()) {
                        JsonObject starObj = new JsonObject();
                        String starId = starResultSet.getString("id");
                        String starName = starResultSet.getString("name");
                        starObj.addProperty("star_id", starId);
                        starObj.addProperty("star_name", starName);
                        starsList.add(starObj);
                    }
                }
                singleMovieObject.add("stars", starsList);
                jsonArray.add(singleMovieObject);
            }

            request.getServletContext().log("getting " + jsonArray.size() + " results");
            jsonOutput.write(jsonArray.toString());
            response.setStatus(200);
            resultSet.close();

        } catch (Exception e) {
            /*
             * After you deploy the WAR file through tomcat manager webpage,
             *   there's no console to see the print messages.
             * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.htmlOutput
             *
             * To view the last n lines (for example, 100 lines) of messages you can use:
             *   tail -100 catalina.htmlOutput
             * This can help you debug your program after deploying it on AWS.
             */
            request.getServletContext().log("Error: ", e);
            response.setStatus(500);
            JsonObject error = new JsonObject();
            error.addProperty("errorMessage", e.getMessage());
            jsonOutput.write(error.toString());
        }
        jsonOutput.close();
    }
}
