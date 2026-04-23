import Classes.User;
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

@WebServlet("/return_movielist")
public class GoBackServlet extends HttpServlet {
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
                PrintWriter jsonOutput = response.getWriter();

                JsonObject userQuery = new JsonObject();
                User user = (User) request.getSession().getAttribute("user");
                if (user == null) {
                        JsonArray jsonArray = new JsonArray();
                        jsonArray.add(userQuery);
                        jsonOutput.println(jsonArray);
                        jsonOutput.close();
                        return;
                }

                userQuery.addProperty("title", user.getTitle());
                userQuery.addProperty("year", user.getYear());
                userQuery.addProperty("director", user.getDirector());
                userQuery.addProperty("star", user.getStar());

                JsonArray jsonArray = new JsonArray();
                jsonArray.add(userQuery);
                jsonOutput.println(jsonArray);
                jsonOutput.close();
        }
}


