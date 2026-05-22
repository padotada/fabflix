package MongoMigration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import java.sql.*;
public class SalesMigrator {
    private static final String sales_query = """
            SELECT sales.id, sales.customer_id, sales.movie_id, sales.sale_date, sales.quantity
            FROM sales
            """;
    public void migrate(){
        String loginUrl = System.getenv("DB_URL");
        String loginUser = System.getenv("DB_USER");
        String loginPassword = System.getenv("DB_PASSWORD");
        String uri = System.getenv("MONGO_URI");

        try(
                Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPassword);
                PreparedStatement statement = connection.prepareStatement(sales_query);
                ResultSet resultSet = statement.executeQuery();
                MongoClient mongoClient = MongoClients.create(uri)
        ){
            MongoDatabase database = mongoClient.getDatabase("movieDB");
            MongoCollection<Document> salesCollection = database.getCollection("sales");

            while (resultSet.next()) {
                Integer saleId = resultSet.getInt("id");
                Integer customerId = resultSet.getInt("customer_id");
                String movieId = resultSet.getString("movie_id");
                Date sale_date = resultSet.getDate("sale_date");
                Integer quantity = resultSet.getInt("quantity");

                Document sale = new Document("_id", saleId)
                        .append("customer_id", customerId)
                        .append("movie_id", movieId)
                        .append("sale_date", sale_date)
                        .append("quantity", quantity);

                salesCollection.replaceOne(
                        new Document("_id", saleId),
                        sale,
                        new ReplaceOptions().upsert(true)
                );
            }

            System.out.println("Sales migration complete.");
        }catch(Exception e) {
            System.out.println("Sales migration failed.");
            e.printStackTrace();
        }
    }
}
