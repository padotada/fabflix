import Classes.User;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String customerQuery = """
                SELECT * FROM customers
                WHERE email = ? AND password = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement movieStatement = connection.prepareStatement(customerQuery)) {
            movieStatement.setString(1, username);
            movieStatement.setString(2, password);
            ResultSet resultSet = movieStatement.executeQuery();

            JsonObject responseJsonObject = new JsonObject();
            // Login success:
            if (resultSet.isBeforeFirst()) { // this checks if there are rows in the returned set aka match?
                // Add this user into the session
                request.getSession().setAttribute("user", new User(username));
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
            } else { // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.

                // !! these error messages aren't updated with new functionality

                if (!username.equals("anteater")) {
                    responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
                } else {
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            }
            response.getWriter().write(responseJsonObject.toString());
        }
        catch (Exception e)
        {
            request.getServletContext().log("Error: ", e);
            response.setStatus(500);
        }
    }
}