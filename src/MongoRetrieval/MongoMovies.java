package MongoRetrieval;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.bson.Document;
import org.bson.conversions.Bson;


public class MongoMovies {


    private String uri = System.getenv("MONGO_URI");
    Bson filter = Filters.and();


    public MongoMovies() {}

    public void addTitleFilter(String title){
        filter = Filters.and(filter, Filters.regex("title", title, "i"));
    }



    public List<MoviePojo> retrieveMovies() {
        List<MoviePojo> movies = new ArrayList<MoviePojo>();

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase movieDB = mongoClient.getDatabase("movieDB");

            MongoCollection<Document> movieCollection = movieDB.getCollection("movies");
            for(Document movie : movieCollection.find(filter)) {
                RatingPojo ratingPojo = new RatingPojo();
                Document rating = (Document) movie.get("rating");


                MoviePojo m = MoviePojo.builder()
                        .setMovieId(movie.getString("_id"))
                        .setMovieTitle(movie.getString("title"))
                        .setMovieYear(movie.getInteger("year"))
                        .setMovieDirector(movie.getString("director"));
            }


        }

        return null;
    }
}
