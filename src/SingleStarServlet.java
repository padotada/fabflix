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

@WebServlet("/single_star")
public class SingleStarServlet extends HttpServlet {
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

        String webQuery = request.getParameter("id");

        String starQuery = """
                SELECT s.id, s.name, s.birth_year
                FROM stars s
                WHERE s.id = ?
                """;

        String moviesQuery = """
                SELECT m.id, m.title
                FROM stars_in_movies sim
                JOIN movies m ON sim.movie_id = m.id
                WHERE sim.star_id = ?
                ORDER BY m.title
                """;

        // Get the PrintWriter for writing response
        PrintWriter htmlOutput = response.getWriter();

//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }

        try (Connection connection = dataSource.getConnection();
            PreparedStatement starStatement = connection.prepareStatement(starQuery);
            PreparedStatement starMoviesStatement = connection.prepareStatement(moviesQuery)){

            starStatement.setString(1, webQuery);

            ResultSet resultSet = starStatement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String birth_year = resultSet.getString("birth_year");
                if(birth_year == null) birth_year = "N/A";

                JsonObject starObject = new JsonObject();
                starObject.addProperty("star_name", name);
                starObject.addProperty("birth_year", birth_year);

                starMoviesStatement.setString(1, webQuery);
                JsonArray movieList = new JsonArray();
                try(ResultSet movieResultSet = starMoviesStatement.executeQuery()){
                    while(movieResultSet.next()){
                        JsonObject movieObject = new JsonObject();
                        String movieId = movieResultSet.getString("id");
                        String movieTitle = movieResultSet.getString("title");
                        movieObject.addProperty("movie_id", movieId);
                        movieObject.addProperty("movie_title", movieTitle);
                        movieList.add(movieObject);
                    }
                }
                starObject.add("movies", movieList);
                jsonArray.add(starObject);
            }
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
            JsonObject error = new JsonObject();
            error.addProperty("errorMessage", e.getMessage());
            htmlOutput.write(error.toString());
        }
        htmlOutput.close();
    }
}
