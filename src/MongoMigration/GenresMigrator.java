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
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private static final String genre_query = "SELECT * FROM genres";

    public void migrate() {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement select_genre_statement = connection.prepareStatement(genre_query);
            ResultSet resultSet = select_genre_statement.executeQuery()) {

            while(resultSet.next()) {
                int genre_id = resultSet.getInt("genre_id");
                String genre_name = resultSet.getString("name");
                Document genre = new Document("genre_id", genre_id).append("name", genre_name);
                writeToMongo(genre);
            }

        } catch (Exception e) {

        }
    }

    private void writeToMongo(Document genreDocument) {
        String uri = "mongodb://testuser:MyPassword6@localhost:27017";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            // This reuses the myNewDB database from the Mongo tutorial. You may want to create a better named database
            MongoDatabase myNewDB = mongoClient.getDatabase("movieDB");
            myNewDB.createCollection("genres");
            MongoCollection<Document> moviesCollection = myNewDB.getCollection("genres");
            moviesCollection.insertOne(genreDocument);
        }
    }

}
