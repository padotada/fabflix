import Classes.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.google.gson.JsonPrimitive;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/genres")
public class GenresServlet extends HttpServlet {
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
        JsonObject genreList = new JsonObject();
        PrintWriter jsonOutput = response.getWriter();
        JsonArray jsonArray = new JsonArray();

        String genreQuery = """
            SELECT name FROM genres ORDER BY name
            """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement movieStatement = connection.prepareStatement(genreQuery);
             ResultSet resultSet = movieStatement.executeQuery())
        {
            JsonArray genres = new JsonArray();
            while(resultSet.next())
            {
                genres.add(resultSet.getString("name"));
            }
            genreList.add("genres", genres);
            jsonArray.add(genreList);
        }
        catch (Exception e) {
        request.getServletContext().log("Error: ", e);
        response.setStatus(500);
        // error is stored in json object
        JsonObject error = new JsonObject();
        error.addProperty("errorMessage", e.getMessage());
        jsonOutput.write(error.toString());
    }
        jsonOutput.write(jsonArray.toString());
        jsonOutput.close();
    }
}


