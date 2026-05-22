package MongoMigration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import java.sql.*;

public class CustomersMigrator {
    private static final String customers_query = """
        SELECT c.id AS customer_id, c.first_name, c.last_name,
               c.credit_card_id, c.address, c.email, c.password, cc.expiration
        FROM customers c
        LEFT JOIN credit_cards cc ON c.credit_card_id = cc.id
""";
    public void migrate() {
        String loginUrl = System.getenv("DB_URL");
        String loginUser = System.getenv("DB_USER");
        String loginPassword = System.getenv("DB_PASSWORD");
        String uri = System.getenv("MONGO_URI");
        try(Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPassword);
            PreparedStatement select_genre_statement = connection.prepareStatement(customers_query);
            ResultSet resultSet = select_genre_statement.executeQuery();
            MongoClient mongoClient = MongoClients.create(uri))
        {

            MongoDatabase database = mongoClient.getDatabase("movieDB");
            MongoCollection<Document> customersCollection = database.getCollection("customers");

            while(resultSet.next()) {
                int customerId = resultSet.getInt("customer_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String creditCardId = resultSet.getString("credit_card_id");
                String address = resultSet.getString("address");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                Date expiration = resultSet.getDate("expiration");

                Document creditCard = new Document("id", creditCardId)
                        .append("expiration", expiration);
                Document customer = new Document("_id", customerId)
                        .append("first_name", firstName)
                        .append("last_name", lastName)
                        .append("address", address)
                        .append("email", email)
                        .append("password", password)
                        .append("credit_card", creditCard);
                customersCollection.replaceOne(
                        new Document("_id", customerId),
                        customer,
                        new ReplaceOptions().upsert(true));
            }
            System.out.println("Customers migration complete");
        } catch (Exception e) {
            System.out.println("uh oh");
            System.out.println(e);
        }
    }

//    private void writeToMongo(Document genreDocument) {
//        try (MongoClient mongoClient = MongoClients.create(uri)) {
//            // This reuses the myNewDB database from the Mongo tutorial. You may want to create a better named database
//            MongoDatabase myNewDB = mongoClient.getDatabase("movieDB");
//            myNewDB.createCollection("customers");
//            MongoCollection<Document> moviesCollection = myNewDB.getCollection("customers");
//            moviesCollection.insertOne(genreDocument);
//        }
//    }
}
