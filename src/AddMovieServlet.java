import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;

@WebServlet("/api/add-movie")
public class AddMovieServlet extends HttpServlet {
    private DataSource dataSource;
    public void init(ServletConfig config){
        try{
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws IOException{
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String title = request.getParameter("title");
        String yearString = request.getParameter("year");
        String director = request.getParameter("director");
        String starName = request.getParameter("starName");
        String genreName = request.getParameter("genreName");

        JsonObject responseJson = new JsonObject();
        if(title == null || title.trim().isEmpty()
        || yearString == null || yearString.trim().isEmpty()
        || director == null || director.trim().isEmpty()
        || starName == null || starName.trim().isEmpty()
        || genreName == null || genreName.trim().isEmpty()) {
            responseJson.addProperty("status", "fail");
            responseJson.addProperty("message", "Title, year, director, star name, and genre name are required.");
            response.getWriter().write(responseJson.toString());
            return;
        }
        int year;
        try{
            year = Integer.parseInt(yearString.trim());
        }catch(NumberFormatException nfe){
            responseJson.addProperty("status", "fail");
            responseJson.addProperty("message", "Year must be a valid number");
            response.getWriter().write(responseJson.toString());
            return;
        }

        title = title.trim();
        director = director.trim();
        starName = starName.trim();
        genreName = genreName.trim();

        String procedureCall = "{CALL add_movie(?, ?, ?, ?, ?, ?)}";

        try(Connection connection = dataSource.getConnection();
        CallableStatement statement = connection.prepareCall(procedureCall)){
            statement.setString(1, title);
            statement.setInt(2, year);
            statement.setString(3, director);
            statement.setString(4, starName);
            statement.setString(5, genreName);
            statement.registerOutParameter(6, Types.VARCHAR);
            statement.execute();
            String message = statement.getString(6);
            if(message != null &&  message.toLowerCase().contains("already exists")){
                responseJson.addProperty("status", "fail");
            }else{
                responseJson.addProperty("status", "success");
            }
            responseJson.addProperty("message", message);
        }catch(Exception e){
            request.getServletContext().log("AddMovieServlet error", e);

            responseJson.addProperty("status", "fail");
            responseJson.addProperty("message", "Failed to add movie.");
        }
        response.getWriter().write(responseJson.toString());
    }
}
