package MongoRetrieval;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.util.List;
public class MongoStars {
    private static final String uri = System.getenv("MONGO_URI");
    public StarPojo getStarById(String starId) {
        if (starId == null || starId.trim().isEmpty()) {
            return null;
        }
        if (uri == null || uri.isBlank()) {
            throw new IllegalStateException("MONGO_URI environment variable is not set for Tomcat.");
        }
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("movieDB");
            MongoCollection<Document> starsCollection = database.getCollection("stars");

            Document star = starsCollection.find(Filters.eq("_id", starId)).first();

            if (star == null) {
                return null;
            }
            return documentToStarPojo(star);
        }
    }
    private StarPojo documentToStarPojo(Document star){
        StarPojo starPojo = StarPojo.builder()
                .setStarId(star.getString("_id"))
                .setStarName(star.getString("name"))
                .setStarBirthYear(star.getInteger("birth_year"));

        List<Document> movies = star.getList("movies", Document.class);

        if (movies != null) {
            for (Document movie : movies) {
                System.out.println(movie.toString());
                starPojo.addMovie(
                        movie.getString("id"),
                        movie.getString("movie_title"),
                        movie.getInteger("movie_year")
                );
            }
        }

        return starPojo;
    }
}
