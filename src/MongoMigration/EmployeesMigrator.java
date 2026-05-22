package MongoMigration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.sql.*;

public class EmployeesMigrator {
    private static final String employees_query = "SELECT * FROM employees";
    public void migrate(){

    }
}
