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

@WebServlet("/single_star")
public class SingleStarServlet extends HttpServlet {
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
             ResultSet resultSet = statement.executeQuery("SELECT * from movies natural join ratings order by rating limit 20")) {

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String year = resultSet.getString("year");
                String director = resultSet.getString("director");
                String genres = resultSet.getString("name");
                String stars = resultSet.getString("birthyear");
                String rating = resultSet.getString("birthyear");
                htmlOutput.println("<tr>");
                htmlOutput.println("<td>" + title + "</td>");
                htmlOutput.println("<td>" + year + "</td>");
                htmlOutput.println("<td>" + director + "</td>");
                htmlOutput.println("<td>" + genres + "</td>");
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
