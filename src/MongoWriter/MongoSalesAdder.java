package MongoWriter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.InsertOneResult;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.sql.*;

public class MongoSalesAdder {
    String uri = System.getenv("MONGO_URI");

    Integer customerId;
    String movieId;
    Date sale_date;
    Integer quantity;

    public MongoSalesAdder(Integer customerId, String movieId, Date sale_date, Integer quantity) {
        this.customerId = customerId;
        this.movieId = movieId;
        this.sale_date = sale_date;
        this.quantity = quantity;
    }

    public ObjectId addSales()
    {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("movieDB");
            MongoCollection<Document> salesCollection = database.getCollection("sales");




            InsertOneResult result = salesCollection.insertOne(new Document());
            return result.getInsertedId().asObjectId().getValue();
        }
    }
}
