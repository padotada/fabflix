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
    Bson filter = Filters.and(); // I'm not sure if this properly creates an empty filter


    public MongoMovies() {}

    // searching
    public void addTitleFilter(String title){
        // this is a basic substring match, "i" is to ignore case
        filter = Filters.and(filter, Filters.regex("title", title, "i"));
    }

    public void addYearFilter(int year) {
        filter = Filters.and(filter, Filters.eq("year", year));
    }

    public void addDirectorFilter(String director) {
        filter = Filters.and(filter, Filters.regex("director", director, "i"));
    }

    public void addStarFilter(String star) {
        // how to go into star layer?

    }


    // browsing
    public void addFirstLetterFilter(String first) {
        filter = Filters.and(filter, Filters.regex("title", "^" + first, "i"));
    }

    public void addGenreFilter(int genreId) {
        // same as star filter
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
