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

    }
}
