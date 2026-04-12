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

@WebServlet("/movielist")
public class MoviesServlet extends HttpServlet {
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
        // Set response mime type
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // String queries
        String movieQuery = """
                SELECT m.id, m.title, m.year, m.director, r.rating FROM movies m
                JOIN ratings r ON m.id = r.movie_id ORDER BY r.rating DESC LIMIT 20
                """;
        String genreQuery = """
                SELECT g.name
                FROM genres_in_movies gim
                JOIN genres g ON gim.genre_id = g.id
                WHERE gim.movie_id = ?
                ORDER BY g.name
                LIMIT 3
                """;
        String starQuery = """
                SELECT s.id, s.name
                FROM stars_in_movies sim
                JOIN stars s ON sim.star_id = s.id
                WHERE sim.movie_id = ?
                ORDER BY s.name
                LIMIT 3
                """;

        // Get the PrintWriter for writing response
        PrintWriter htmlOutput = response.getWriter();
//        htmlOutput.println("<html>");
//        htmlOutput.println("<head><title>Fabflix</title></head>");
//        htmlOutput.println("<body>");
//        htmlOutput.println("<h1>MovieDB Movies</h1>");
//        htmlOutput.println("<table border>");
//        htmlOutput.println("<tr>");
//        htmlOutput.println("<td>ID</td>");
//        htmlOutput.println("<td>title</td>");
//        htmlOutput.println("<td>year</td>");
//        htmlOutput.println("<td>director</td>");
//        htmlOutput.println("<td>genres</td>");
//        htmlOutput.println("<td>stars</td>");
//        htmlOutput.println("<td>rating</td>");
//        htmlOutput.println("</tr>");

//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        } // may not be needed

        try (Connection connection = dataSource.getConnection();
            PreparedStatement movieStatement = connection.prepareStatement(movieQuery);
            PreparedStatement genreStatement = connection.prepareStatement(genreQuery);
            PreparedStatement starStatement = connection.prepareStatement(starQuery)) {
            ResultSet resultSet = movieStatement.executeQuery();
            JsonArray jsonArray = new JsonArray();
            while (resultSet.next()) {
                String movieId = resultSet.getString("id");
                String title = resultSet.getString("title");
                String year = resultSet.getString("year");
                String director = resultSet.getString("director");
                String rating = resultSet.getString("rating");

                JsonObject movieObject = new JsonObject();
                movieObject.addProperty("movie_id", movieId);
                movieObject.addProperty("movie_title", title);
                movieObject.addProperty("movie_year", year);
                movieObject.addProperty("movie_director", director);
                movieObject.addProperty("movie_rating", rating);

                JsonArray genres = new JsonArray();
                genreStatement.setString(1, movieId);
                try (ResultSet genreResultSet = genreStatement.executeQuery()) {
                    while(genreResultSet.next()){
                        String genreName = genreResultSet.getString("name");
                        genres.add(genreName);
                    }
                }
                movieObject.add("genres", genres);

                JsonArray starsList = new JsonArray();
                starStatement.setString(1, movieId);
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
                movieObject.add("stars", starsList);
                jsonArray.add(movieObject);
//                htmlOutput.println(starsList);
            }

//            htmlOutput.println("</table>");
//            htmlOutput.println("</body>");
            request.getServletContext().log("getting " + jsonArray.size() + " results");
            htmlOutput.write(jsonArray.toString());
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
//            htmlOutput.println("<body>");
//            htmlOutput.println("<p>");
//            htmlOutput.println("Exception in doGet: " + e.getMessage());
//            htmlOutput.println("</p>");
//            htmlOutput.print("</body>");
            // error is stored in json object
            JsonObject error = new JsonObject();
            error.addProperty("errorMessage", e.getMessage());
            htmlOutput.write(error.toString());
        }finally {
//        htmlOutput.println("</html>");
            htmlOutput.close();
        }
    }
}
