package MongoRetrieval;

import java.util.List;
import java.io.*;

import com.mongodb.client.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.bson.Document;

public class MongoMovies {
    private String uri = System.getenv("MONGO_URI");

    public MongoMovies() {}

    public List<MoviePojo> retrieveMovies() {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase movieDB = mongoClient.getDatabase("movieDB");


        }

        return null;
    }
}
