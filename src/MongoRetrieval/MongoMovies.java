package MongoRetrieval;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.regex.Pattern;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.bson.Document;
import org.bson.conversions.Bson;


public class MongoMovies {

    List<Bson> filters = new ArrayList<>();

    private Bson sort = Sorts.orderBy(Sorts.descending("rating.rating"), Sorts.descending("title"));
//    Bson filter = Filters.and(); // I'm not sure if this properly creates an empty filter

    private int pageNumber = 1;
    private int numRecords = 10;

    public MongoMovies() {}

    // searching
    public void addTitleFilter(String title){
        // this is a basic substring match, "i" is to ignore case
        // filter = Filters.and(filter, Filters.regex("title", title, "i"));
        if(title != null && !title.trim().isEmpty()) {
            filters.add(Filters.regex("title", Pattern.compile(Pattern.quote(title), Pattern.CASE_INSENSITIVE)));
        }
    }

    public void addYearFilter(String year) {
//        filter = Filters.and(filter, Filters.eq("year", year));
        if (year != null && !year.trim().isEmpty()) {
            filters.add(Filters.eq("year", Integer.parseInt(year)));
        }
    }

    public void addDirectorFilter(String director) {
//        filter = Filters.and(filter, Filters.regex("director", director, "i"));
        if (director != null && !director.trim().isEmpty()) {
            filters.add(Filters.regex("director", Pattern.compile(Pattern.quote(director), Pattern.CASE_INSENSITIVE)));
        }
    }

    public void addStarFilter(String star) {
        // how to go into star layer?
        if (star != null && !star.trim().isEmpty()) {
            filters.add(Filters.regex("stars.name", Pattern.compile(Pattern.quote(star), Pattern.CASE_INSENSITIVE)));
        }
    }


    // browsing
    public void addFirstLetterFilter(String first) {
//        filter = Filters.and(filter, Filters.regex("title", "^" + first, "i"));
        if (first != null && !first.trim().isEmpty()) {
            filters.add(Filters.regex("title", Pattern.compile("^" + Pattern.quote(first), Pattern.CASE_INSENSITIVE)));
        }
    }

    public void addGenreFilter(String genre) {
        // same as star filter
        if (genre != null && !genre.trim().isEmpty()) {
            filters.add(Filters.eq("genres.name", genre));
        }
    }

    public void setPagination(int pageNumber, int numRecords){
        this.pageNumber = Math.max(pageNumber, 1);
        this.numRecords = Math.max(numRecords, 1);
    }

    public void setSort(String titleFirst, int titleSort, int ratingSort){
        Bson titleOrder = titleSort == 1
                ? Sorts.descending("title")
                : Sorts.ascending("title");

        Bson ratingOrder = ratingSort == 1
                ? Sorts.descending("rating.rating")
                : Sorts.ascending("rating.rating");

        if ("true".equals(titleFirst)) {
            sort = Sorts.orderBy(titleOrder, ratingOrder);
        } else {
            sort = Sorts.orderBy(ratingOrder, titleOrder);
        }
    }

    private Bson buildFilter() {
        if (filters.isEmpty()) {
            return new Document();
        }

        return Filters.and(filters);
    }


    public List<MoviePojo> retrieveMovies() {
        List<MoviePojo> movies = new ArrayList<>();
        int skip = numRecords * (pageNumber - 1);

        String uri = System.getenv("MONGO_URI");
        if (uri == null || uri.isEmpty()) {
            throw new IllegalStateException("MONGO_URI environment variable is not set for Tomcat.");
        }

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase movieDB = mongoClient.getDatabase("movieDB");
            MongoCollection<Document> movieCollection = movieDB.getCollection("movies");

//            for(Document movie : movieCollection.find(filter)) {
//                RatingPojo ratingPojo = new RatingPojo();
//                Document rating = (Document) movie.get("rating");
//
//
//                MoviePojo m = MoviePojo.builder()
//                        .setMovieId(movie.getString("_id"))
//                        .setMovieTitle(movie.getString("title"))
//                        .setMovieYear(movie.getInteger("year"))
//                        .setMovieDirector(movie.getString("director"));
//            }
            FindIterable<Document> results = movieCollection
                    .find(buildFilter())
                    .sort(sort)
                    .limit(numRecords + 1)
                    .skip(skip);
            for(Document movie : results){
                MoviePojo moviePojo = documentToMoviePojo(movie);
                movies.add(moviePojo);
            }
        }
        return movies;

    }
    private MoviePojo documentToMoviePojo(Document movie){
        Document ratingDocument = movie.get("rating", Document.class);

        Double rating = null;
        Integer voteCount = null;

        if (ratingDocument != null) {
            rating = ratingDocument.getDouble("rating");
            voteCount = ratingDocument.getInteger("vote_count");
        }
//        RatingPojo ratingPojo = new RatingPojo().setRating(rating).setVoteCount(voteCount);

        MoviePojo moviePojo = MoviePojo.builder()
                .setMovieId(movie.getString("_id"))
                .setMovieTitle(movie.getString("title"))
                .setMovieYear(movie.getInteger("year"))
                .setMovieDirector(movie.getString("director"))
                .setMoviePrice(movie.getDouble("price"))
                .setMovieRating(rating).setVoteCount(voteCount);

        List<Document> genres = movie.getList("genres", Document.class);
        if (genres != null) {
            int count = 0;
            for (Document genre : genres) {
                if (count >= 3) {
                    break;
                }

                moviePojo.addGenre(genre.getString("name"));
                count++;
            }
        }

        List<Document> stars = movie.getList("stars", Document.class);
        if (stars != null) {
            int count = 0;
            for (Document star : stars) {
                if (count >= 3) {
                    break;
                }

                moviePojo.addStar(
                        star.getString("id"),
                        star.getString("name")
                );
                count++;
            }
        }

        return moviePojo;
    }
}
