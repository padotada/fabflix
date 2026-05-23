package MongoMigration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
// store all movies the actor acted for in a list too

public class StarsMigrator {
    private static final String stars_query = """
        SELECT
            s.id AS star_id, s.name, s.birth_year, m.id AS movie_id, m.title AS movie_title
        FROM stars s
        LEFT JOIN stars_in_movies sim on s.id = sim.star_id
        LEFT JOIN movies m ON sim.movie_id = m.id
        ORDER BY s.id
    """;
    public void migrate(){
        String loginUrl = System.getenv("DB_URL");
        String loginUser = System.getenv("DB_USER");
        String loginPassword = System.getenv("DB_PASSWORD");
        String uri = System.getenv("MONGO_URI");

        HashMap<String, Document> starsMap = new HashMap<>();
        try (
                Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPassword);
                PreparedStatement statement = connection.prepareStatement(stars_query);
                ResultSet resultSet = statement.executeQuery();
                MongoClient mongoClient = MongoClients.create(uri)
        ) {
            MongoDatabase database = mongoClient.getDatabase("movieDB");
            MongoCollection<Document> starsCollection = database.getCollection("stars");

            while (resultSet.next()) {
                String starId = resultSet.getString("star_id");
                String name = resultSet.getString("name");

                Integer birthYear = null;
                int birthYearValue = resultSet.getInt("birth_year");
                if (!resultSet.wasNull()) {
                    birthYear = birthYearValue;
                }

                String movieId = resultSet.getString("movie_id");

                Document star = starsMap.get(starId);

                if (star == null) {
                    star = new Document("_id", starId)
                            .append("name", name)
                            .append("birth_year", birthYear)
                            .append("movies", new ArrayList<Document>());

                    starsMap.put(starId, star);
                }

                if (movieId != null) {
                    String movieTitle = resultSet.getString("movie_title");
                    Document movie = new Document("id", movieId).append("title", movieTitle);
                    List<Document> movies = star.getList("movies", Document.class);
                    movies.add(movie);
                }
            }

            for (Document star : starsMap.values()) {
                String starId = star.getString("_id");

                starsCollection.replaceOne(
                        new Document("_id", starId),
                        star,
                        new ReplaceOptions().upsert(true)
                );
            }

            System.out.println("Stars migration complete.");

        } catch (Exception e) {
            System.out.println("Stars migration failed.");
            e.printStackTrace();
        }
    }
}
