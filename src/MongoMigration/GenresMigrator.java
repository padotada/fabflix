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
        PreparedStatement genre_statement;
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement select_genre_statement = connection.prepareStatement(genre_query);


        } catch (Exception e) {

        }
    }

    private void writeToMongo(Document genreDocument) {

    }

}
