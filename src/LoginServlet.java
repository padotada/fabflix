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

        String usernameQuery = """
                SELECT * FROM customers
                WHERE email = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement loginStatement = connection.prepareStatement(usernameQuery);
             PreparedStatement usernameStatement = connection.prepareStatement(usernameQuery)) {
            loginStatement.setString(1, username);
            loginStatement.setString(2, password);

            usernameStatement.setString(1, username);
            JsonObject responseJsonObject = new JsonObject();

            ResultSet resultSet = loginStatement.executeQuery();

            if (!captcha_success && resultSet.isBeforeFirst()) { // this checks if username exists
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");

                responseJsonObject.addProperty("message", "reCaptcha failed");
            } else if (!resultSet.isBeforeFirst()) { // Username doesn't exist
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");

                responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
            }
            else {
                resultSet.next();
                String encryptedPassword = resultSet.getString("password");
                if(new StrongPasswordEncryptor().checkPassword(password, encryptedPassword))
                {
                    responseJsonObject.addProperty("status", "success");
                    // Add this user into the session
                    request.getSession().setAttribute("user", new User(username));
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                }
                else {
                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Login failed");
                    responseJsonObject.addProperty("message", "incorrect password");
                }

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