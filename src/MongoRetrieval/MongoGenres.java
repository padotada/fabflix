package MongoRetrieval;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoGenres {
    public List<GenrePojo> retrieveGenres(){
        List<GenrePojo> genres = new ArrayList<>();
        String uri = System.getenv("MONGO_URI");
        if (uri == null || uri.isBlank()) {
            throw new IllegalStateException("MONGO_URI environment variable is not set for Tomcat.");
        }

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("movieDB");
            MongoCollection<Document> genresCollection = database.getCollection("genres");

            for (Document genre : genresCollection.find().sort(Sorts.ascending("name"))) {
                GenrePojo genrePojo = GenrePojo.builder()
                        .setGenreId(genre.getInteger("_id"))
                        .setGenreName(genre.getString("name"));

                genres.add(genrePojo);
            }
        }

        return genres;
    }
}
