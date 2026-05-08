import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;

@WebServlet("/api/metadata")
public class MetadataServlet extends HttpServlet {
    private DataSource dataSource;
    public void init(ServletConfig config){
        try{
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonObject responseJson = new JsonObject();
        JsonArray tablesArray = new JsonArray();

        try(Connection connection = dataSource.getConnection()){
            DatabaseMetaData metadata = connection.getMetaData();
            String databaseName = connection.getCatalog();
            try(ResultSet tables = metadata.getTables(databaseName, null, "%",
                    new String[]{"TABLE"})){
                while(tables.next()){
                    String tableName = tables.getString("TABLE_NAME");
                    JsonObject tableJson = new JsonObject();
                    tableJson.addProperty("tableName", tableName);
                    JsonArray columnsArray = new JsonArray();
                    try(ResultSet columns = metadata.getColumns(databaseName, null,
                            tableName, "%")){
                        while(columns.next()){
                            JsonObject columnJson = new JsonObject();
                            columnJson.addProperty("columnName", columns.getString("COLUMN_NAME"));
                            columnJson.addProperty("columnType", columns.getString("TYPE_NAME"));
                            columnJson.addProperty("columnSize", columns.getInt("COLUMN_SIZE"));
                            columnsArray.add(columnJson);
                        }
                    }
                    tableJson.add("columns", columnsArray);
                    tablesArray.add(tableJson);
                }
            }
        }catch(Exception e){
            request.getServletContext().log("MetadataServlet error", e);
            responseJson.addProperty("status", "fail");
            responseJson.addProperty("message", "Failed to load metadata.");
        }
        response.getWriter().write(responseJson.toString());
    }
}
