package MongoMigration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import java.sql.*;
// wrap ratings into movies, keep a list of genres and stars in movies separately

public class MoviesMigrator {
    private static final String movies_query = """
            SELECT
                m.id, m.title, m.year, m.director, m.price, r.rating, r.vote_count
            FROM movies m
            LEFT JOIN ratings r on m.id = r.movie_id
            """;
    private static final String stars_query = """
            SELECT s.id, s.name
            FROM stars s JOIN stars_in_movies sm ON s.id = sm.star_id JOIN movies m on m.id = sm.movie_id
            WHERE m.id = ?
            ORDER BY s.name
            """;

    private static final String genres_query = """
            SELECT g.id, g.name
            FROM genres g JOIN genres_in_movies gm on m.id = gm.genre_id JOIN movies m on m.id = gm.movie_id
            WHERE m.id = ?
            ORDER BY g.name
            """;


    public void migrate(){
        String loginUrl = System.getenv("DB_URL");
        String loginUser = System.getenv("DB_USER");
        String loginPassword = System.getenv("DB_PASSWORD");
        String uri = System.getenv("MONGO_URI");

        try(Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPassword);
                PreparedStatement statement = connection.prepareStatement(movies_query);
                ResultSet resultSet = statement.executeQuery();
                MongoClient mongoClient = MongoClients.create(uri)) {

            MongoDatabase database = mongoClient.getDatabase("movieDB");
            MongoCollection<Document> moviesCollection = database.getCollection("movies");

            while (resultSet.next()) {
                String movieId = resultSet.getString("id");
                String title = resultSet.getString("title");
                int year = resultSet.getInt("year");
                String director = resultSet.getString("director");
                double price = resultSet.getDouble("price");

                Double rating = null;
                double ratingValue = resultSet.getDouble("rating");
                if(!resultSet.wasNull()){
                    rating = ratingValue;
                }
                Integer voteCount = null;
                int voteCountValue = resultSet.getInt("vote_count");
                if(!resultSet.wasNull()){
                    voteCount = voteCountValue;
                }
                Document ratingDocument = new Document("rating", rating)
                        .append("vote_count", voteCount);

                Document movie = new Document("_id", movieId)
                        .append("title", title)
                        .append("year", year)
                        .append("director", director)
                        .append("price", price)
                        .append("rating", ratingDocument);

                moviesCollection.replaceOne(
                        new Document("_id", movieId),
                        movie,
                        new ReplaceOptions().upsert(true)
                );
            }

            System.out.println("Movies migration complete.");

        } catch (Exception e) {
            System.out.println("Movie migration failed.");
            e.printStackTrace();
        }
    }
}
