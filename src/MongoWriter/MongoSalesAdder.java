package MongoWriter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.InsertOneResult;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.sql.*;

public class MongoSalesAdder {
    String uri = System.getenv("MONGO_URI");

    public MongoSalesAdder() {}

    public Integer addSales(Integer customerId, String movieId, Date sale_date, Integer quantity)
    {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("movieDB");
            MongoCollection<Document> salesCollection = database.getCollection("sales");
            Document largestDoc = salesCollection.find()
                    .sort(Sorts.descending("_id"))
                    .limit(1)
                    .first();

            Integer id = largestDoc.getInteger("_id") + 1;

            Document sale = new Document("_id", id).append("customer_id", customerId)
                    .append("movie_id", movieId).append("sale_date", sale_date)
                    .append("quantity", quantity);

            salesCollection.insertOne(sale);
            return id;
        }
    }
}
