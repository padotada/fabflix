package MongoMigration;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import jakarta.servlet.ServletConfig;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenresMigrator {


    private static final String genre_query = "SELECT * FROM genres";

    public void migrate() {
        String loginUrl = System.getenv("DB_URL");
        String loginUser = System.getenv("DB_USER");
        String loginPassword = System.getenv("DB_PASSWORD");
        try(Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPassword);
            PreparedStatement select_genre_statement = connection.prepareStatement(genre_query);
            ResultSet resultSet = select_genre_statement.executeQuery()) {

            while(resultSet.next()) {
                int genre_id = resultSet.getInt("id");
                String genre_name = resultSet.getString("name");
                Document genre = new Document("_id", genre_id).append("name", genre_name);
                writeToMongo(genre);
            }
            System.out.println("Genres migration completed");
        } catch (Exception e) {
            System.out.println("Error with genre migration");
            System.out.println(e);
        }
    }

    private void writeToMongo(Document genreDocument) {
        String uri = System.getenv("MONGO_URI");
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase myNewDB = mongoClient.getDatabase("movieDB");
            myNewDB.createCollection("genres");
            MongoCollection<Document> moviesCollection = myNewDB.getCollection("genres");
            moviesCollection.insertOne(genreDocument);
        }
    }

}
