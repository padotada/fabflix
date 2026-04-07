import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/movielist")
public class MoviesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Change this to your own mysql username and password
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb"; // is this right

        // Set response mime type
        response.setContentType("text/html");

        // Get the PrintWriter for writing response
        PrintWriter htmlOutput = response.getWriter();
        htmlOutput.println("<html>");
        htmlOutput.println("<head><title>Fabflix</title></head>");
        htmlOutput.println("<body>");
        htmlOutput.println("<h1>MovieDB Movies</h1>");
        htmlOutput.println("<table border>");
        htmlOutput.println("<tr>");
        htmlOutput.println("<td>title</td>");
        htmlOutput.println("<td>year</td>");
        htmlOutput.println("<td>genres</td>");
        htmlOutput.println("<td>director</td>");
        htmlOutput.println("<td>stars</td>");
        htmlOutput.println("<td>rating</td>");
        htmlOutput.println("</tr>");

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "WITH top_movies AS (SELECT movie_id, rating FROM ratings ORDER BY rating DESC LIMIT 20)" +
                             "SELECT m.id, m.title, m.year, m.director, tm.rating, " +
                             "g.name AS genre_name, s.id AS star_id, s.name AS star_name FROM top_movies tm " +
                             "JOIN movies m ON m.id = tm.movie_id " +
                             "LEFT JOIN genres_in_movies gim ON m.id=gim.movie_id " +
                             "LEFT JOIN genres g ON gim.genre_id = g.id " +
                             "LEFT JOIN stars_in_movies sim ON m.id = sim.movie_id " +
                             "LEFT JOIN stars s ON sim.star_id = s.id " +
                             "ORDER BY tm.rating DESC;")) {

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String year = resultSet.getString("year");
                String director = resultSet.getString("director");
                String genres = resultSet.getString("genre_name");
                String stars = resultSet.getString("star_name");
                String rating = resultSet.getString("rating");
                htmlOutput.println("<tr>");
                htmlOutput.println("<td>" + title + "</td>");
                htmlOutput.println("<td>" + year + "</td>");
                htmlOutput.println("<td>" + genres + "</td>");
                htmlOutput.println("<td>" + director + "</td>");
                htmlOutput.println("<td>" + stars + "</td>");
                htmlOutput.println("<td>" + rating + "</td>");
                htmlOutput.println("</tr>");
            }

            htmlOutput.println("</table>");
            htmlOutput.println("</body>");
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

            htmlOutput.println("<body>");
            htmlOutput.println("<p>");
            htmlOutput.println("Exception in doGet: " + e.getMessage());
            htmlOutput.println("</p>");
            htmlOutput.print("</body>");
        }
        htmlOutput.println("</html>");
        htmlOutput.close();
    }
}
