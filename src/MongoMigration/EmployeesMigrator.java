package MongoMigration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.sql.*;

// we might not need this because we don't need to do the dashboard

public class EmployeesMigrator {
    private static final String employees_query = "SELECT * FROM employees";
    public void migrate(){
        String loginUrl = System.getenv("DB_URL");
        String loginUser = System.getenv("DB_USER");
        String loginPassword = System.getenv("DB_PASSWORD");
        String uri = System.getenv("MONGO_URI");

        try(Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPassword);
            PreparedStatement select_genre_statement = connection.prepareStatement(employees_query);
            ResultSet resultSet = select_genre_statement.executeQuery();
            MongoClient mongoClient = MongoClients.create(uri))
        {

            MongoDatabase database = mongoClient.getDatabase("movieDB");
            MongoCollection<Document> employeesCollection = database.getCollection("employees");
            employeesCollection.deleteMany(new Document());

            while(resultSet.next()) {
                String email = resultSet.getString("email");
                String fullName = resultSet.getString("fullname");
                String password = resultSet.getString("password");
                Document employee = new Document("email", email)
                        .append("email", email)
                        .append("password", password);

                employeesCollection.insertOne(employee);
            }
            System.out.println("Employees migration complete");
        } catch (Exception e) {
            System.out.println("uh oh");
            System.out.println(e);
        }
    }
}
