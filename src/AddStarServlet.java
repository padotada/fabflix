import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.*;

@WebServlet("/api/add-star")
public class AddStarServlet extends HttpServlet{
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletResponse response, HttpServletRequest request) throws IOException{
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String name = request.getParameter("name");
        String birthYearString = request.getParameter("birthYear");
        JsonObject responseJson = new JsonObject();
        if(name == null || name.trim().isEmpty()){
            responseJson.addProperty("status", "fail");
            responseJson.addProperty("message", "Star name is required");
            response.getWriter().write(responseJson.toString());
            return;
        }
        name = name.trim();
        Integer birthYear = null;
        if(birthYearString != null && !birthYearString.trim().isEmpty()){
            try{
                birthYear = Integer.parseInt(birthYearString.trim());
            }catch(NumberFormatException nfe){
                responseJson.addProperty("status", "fail");
                responseJson.addProperty("message", "Birth year must be a valid number");
                response.getWriter().write(responseJson.toString());
                return;
            }
        }

        String maxIdQuery = """
                SELECT MAX(id) AS max_id FROM stars WHERE id LIKE 'nm%'
                """;
        String insertQuery = """
                INSERT INTO stars(id, name, birth_year) VALUES(?, ?, ?)
                """;

        try(Connection connection = dataSource.getConnection()){
            connection.setAutoCommit(false);
            String newStarId;
            try(PreparedStatement maxIdStatement = connection.prepareStatement(maxIdQuery);
            ResultSet resultSet = maxIdStatement.executeQuery()){
                int nextIdNumber = -1;
                if(resultSet.next()){
                    String maxId = resultSet.getString("max_id");
                    if(maxId != null && maxId.length() > 2){
                        String numericPart = maxId.substring(2);
                        nextIdNumber = Integer.parseInt(numericPart) + 1;
                    }
                }
                newStarId = "nm" + String.format("%07d", nextIdNumber);
            }
            try(PreparedStatement insertStatement = connection.prepareStatement(insertQuery)){
                insertStatement.setString(1, newStarId);
                insertStatement.setString(2, name);
                if(birthYear == null){
                    insertStatement.setNull(3, Types.INTEGER);
                }else{
                    insertStatement.setInt(3, birthYear);
                }
                insertStatement.executeUpdate();
            }
            connection.commit();

            responseJson.addProperty("status", "success");
            responseJson.addProperty("message", "Star added successfully. New star ID: " + newStarId);
            responseJson.addProperty("starId", newStarId);
        }catch(Exception e){
            request.getServletContext().log("AddStarServlet error", e);

            responseJson.addProperty("status", "fail");
            responseJson.addProperty("message", "Failed to add star.");
        }
        response.getWriter().write(responseJson.toString());
    }
}
