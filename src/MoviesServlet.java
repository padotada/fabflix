import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/movielist")
public class MoviesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Change this to your own mysql username and password
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb"; // is this right

        // Set response mime type
        response.setContentType("text/html");

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
        htmlOutput.println("<html>");
        htmlOutput.println("<head><title>Fabflix</title></head>");
        htmlOutput.println("<body>");
        htmlOutput.println("<h1>MovieDB Movies</h1>");
        htmlOutput.println("<table border>");
        htmlOutput.println("<tr>");
        htmlOutput.println("<td>ID</td>");
        htmlOutput.println("<td>title</td>");
        htmlOutput.println("<td>year</td>");
        htmlOutput.println("<td>director</td>");
        htmlOutput.println("<td>genres</td>");
        htmlOutput.println("<td>stars</td>");
        htmlOutput.println("<td>rating</td>");
        htmlOutput.println("</tr>");

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            PreparedStatement movieStatement = connection.prepareStatement(movieQuery);
            PreparedStatement genreStatement = connection.prepareStatement(genreQuery);
            PreparedStatement starStatement = connection.prepareStatement(starQuery);
            ResultSet resultSet = movieStatement.executeQuery()) {

            while (resultSet.next()) {
                String movieId = resultSet.getString("id");
                String title = resultSet.getString("title");
                String year = resultSet.getString("year");
                String director = resultSet.getString("director");
                String rating = resultSet.getString("rating");
                htmlOutput.println("<tr>");
                htmlOutput.println("<td><a href=\"/cs122b_spring_cruisers_war/single_movie?movie=" +
                        movieId + "\">" + movieId + "</a></td>");
                htmlOutput.println("<td>" + title + "</td>");
                htmlOutput.println("<td>" + year + "</td>");
                htmlOutput.println("<td>" + director + "</td>");
                htmlOutput.println("<td>");
                genreStatement.setString(1, movieId);
                try (ResultSet genreResultSet = genreStatement.executeQuery()) {
                    boolean hasGenre = false;
                    while (genreResultSet.next()) {
                        hasGenre = true;
                        String genreName = genreResultSet.getString("name");
                        htmlOutput.println(genreName + "<br>");
                    }
                    if (!hasGenre) {
                        htmlOutput.println("N/A");
                    }
                }

                htmlOutput.println("</td>");
                htmlOutput.println("<td>");
                starStatement.setString(1, movieId);
                try (ResultSet starResultSet = starStatement.executeQuery()) {
                    boolean hasStar = false;
                    while (starResultSet.next()) {
                        hasStar = true;
                        String starName = starResultSet.getString("name");
                        htmlOutput.println(starName);
                    }
                    if (!hasStar) {
                        htmlOutput.println("N/A");
                    }
                }
                htmlOutput.println("</td>");
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
