import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.xml.transform.Result;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/single_star")
public class SingleStarServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Change this to your own mysql username and password
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb"; // is this right

        // Set response mime type
        response.setContentType("text/html");

        String webQuery = request.getParameter("starId");
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
        htmlOutput.println("<html>");
        htmlOutput.println("<head><title>Fabflix</title></head>");
        htmlOutput.println("<body>");
        htmlOutput.println("<h1>Movie Star</h1>");

        if (webQuery == null) {
            htmlOutput.println("<p>");
            htmlOutput.println("No star specified");
            htmlOutput.println("</p>");
            htmlOutput.println("<a href=\"/cs122b_spring_cruisers_war/movielist\">Back to Movies</a>");
            htmlOutput.print("</body>");
            htmlOutput.close();
            return;
        }

        htmlOutput.println("<table border>");
        htmlOutput.println("<tr>");
        htmlOutput.println("<td>name</td>");
        htmlOutput.println("<td>year of birth</td>");
        htmlOutput.println("<td>movies</td>");
        htmlOutput.println("</tr>");

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            PreparedStatement starStatement = connection.prepareStatement(starQuery);
            PreparedStatement starMoviesStatement = connection.prepareStatement(moviesQuery)){

            starStatement.setString(1, webQuery);

            ResultSet resultSet = starStatement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String birth_year = resultSet.getString("birth_year");
                if(birth_year == null) birth_year = "N/A";

                htmlOutput.println("<tr>");
                htmlOutput.println("<td>"+name+"</td>");
                htmlOutput.println("<td>"+birth_year+"</td>");
                htmlOutput.println("<td>");
                StringBuilder movieList = new StringBuilder();
                starMoviesStatement.setString(1, webQuery);
                try(ResultSet movieResultSet = starMoviesStatement.executeQuery()){
                    boolean first = true;
                    while(movieResultSet.next()){
                        String movieId = movieResultSet.getString("id");
                        String movieTitle = movieResultSet.getString("title");
                        if(!first){
                            movieList.append(", ");
                        }
                        movieList.append("<a href='single_movie?id=")
                                .append(movieId)
                                .append("'>")
                                .append(movieTitle)
                                .append("</a>");
                        first = false;
                    }
                    if(movieList.isEmpty()) htmlOutput.println("N/A");
                }
                htmlOutput.println(movieList.toString());
                htmlOutput.println("</td>");
                htmlOutput.println("</tr>");
            }

            htmlOutput.println("</table>");
            htmlOutput.println("<p>This is a single star page</p>");
            htmlOutput.println("<a href=\"/cs122b_spring_cruisers_war/movielist\">Back to Movies</a>");
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
            htmlOutput.println("<a href=\"/cs122b_spring_cruisers_war/movielist\">Back to Movies</a>");
            htmlOutput.print("</body>");
        }
        htmlOutput.println("</html>");
        htmlOutput.close();
    }
}
