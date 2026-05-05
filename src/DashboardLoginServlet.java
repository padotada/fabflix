import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;

@WebServlet("/api/dashboard-login")
public class DashboardLoginServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config){
        try{
            dataSource = (DataSource) new InitialContext().lookup("java:com/env/jdbc/moviedb");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        JsonObject responseJson = new JsonObject();

        String query = """
                SELECT email, password, fullname
                FROM employees
                WHERE email = ?
        """;
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)){

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.next()){
                responseJson.addProperty("status", "fail");
                responseJson.addProperty("message", "Invalid email or password.");
                response.getWriter().write(responseJson.toString());
                return;
            }

            String encryptedPassword = resultSet.getString("password");
            StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

            if(!passwordEncryptor.checkPassword(password, encryptedPassword)){
                responseJson.addProperty("status", "fail");
                responseJson.addProperty("message", "Invalid email or password");
                response.getWriter().write(responseJson.toString());
                return;
            }
            HttpSession session = request.getSession();
            session.setAttribute("employeeEmail", resultSet.getString("email"));
            session.setAttribute("employeeFullname", resultSet.getString("fullname"));

            responseJson.addProperty("status", "success");
            responseJson.addProperty("message", "Login successful.");
            response.getWriter().write(responseJson.toString());
        }catch(Exception e){
            request.getServletContext().log("Dashboard login error", e);

            responseJson.addProperty("status", "fail");
            responseJson.addProperty("message", "Server error.");
            response.getWriter().write(responseJson.toString());
        }
    }
}
