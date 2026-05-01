import Classes.User;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
        boolean captcha_success = RecaptchaVerifyUtils.verify(gRecaptchaResponse);


        String customerQuery = """
                SELECT * FROM customers
                WHERE email = ? AND password = ?
                """;
        String usernameQuery = """
                SELECT * FROM customers
                WHERE email = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement loginStatement = connection.prepareStatement(customerQuery);
             PreparedStatement usernameStatement = connection.prepareStatement(usernameQuery)) {
            loginStatement.setString(1, username);
            loginStatement.setString(2, password);

            usernameStatement.setString(1, username);
            JsonObject responseJsonObject = new JsonObject();

            ResultSet resultSet = loginStatement.executeQuery();

            // Login success:
            if (captcha_success && resultSet.isBeforeFirst()) { // this checks if there are rows in the returned set aka match?
                // Add this user into the session
                request.getSession().setAttribute("user", new User(username));
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
            } else { // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");

                ResultSet usernameCheck = usernameStatement.executeQuery();
                if(!captcha_success) {
                    responseJsonObject.addProperty("message", "reCaptcha failed");
                }
                else if (usernameCheck.isBeforeFirst()) { // username exists in db
                    responseJsonObject.addProperty("message", "incorrect password");
                }
                else {
                    responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
                }
                usernameCheck.close();
            }
            resultSet.close();

            response.getWriter().write(responseJsonObject.toString());
        }
        catch (Exception e)
        {
            request.getServletContext().log("Error: ", e);
            response.setStatus(500);
        }
    }
}