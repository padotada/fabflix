package MongoRetrieval;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class MoviePojo {
    String id;
    String title;
    Integer year;
    String director;
    Double price;
    Double rating;
    Integer voteCount;
    List<String> genres;
    List<StarPojo> stars;
    private MoviePojo(){
        this.genres = new ArrayList<>();
        this.stars = new ArrayList<>();
    }
    static MoviePojo builder(){
        return new MoviePojo();
    }
    public MoviePojo setMovieId(String id){
        this.id = id;
        return this;
    }
    public MoviePojo setMovieTitle(String title){
        this.title = title;
        return this;
    }
    public MoviePojo setMovieYear(Integer year){
        this.year = year;
        return this;
    }
    public MoviePojo setMovieDirector(String director){
        this.director = director;
        return this;
    }
    public MoviePojo setMoviePrice(Double price){
        this.price = price;
        return this;
    }
    public MoviePojo setMovieRating(Double rating){
        this.rating = rating;
        return this;
    }
    public MoviePojo setVoteCount(Integer voteCount){
        this.voteCount = voteCount;
        return this;
    }
    public MoviePojo setGenres(List<String> genres){
        this.genres = genres;
        return this;
    }
    public MoviePojo setStars(List<StarPojo> stars){
        this.stars = stars;
        return this;
    }
    public void addGenre(String genreName) {
        this.genres.add(genreName);
    }
    public void addStar(String starId, String starName) {
        StarPojo star = StarPojo.builder()
                .setStarId(starId)
                .setStarName(starName);

        this.stars.add(star);
    }
    public JsonObject toJson() {
        JsonObject movieObject = new JsonObject();

        movieObject.addProperty("movie_id", id);
        movieObject.addProperty("movie_title", title);
        movieObject.addProperty("movie_year", year);
        movieObject.addProperty("movie_director", director);
        movieObject.addProperty("movie_price", price);
        if (rating != null) {
            movieObject.addProperty("movie_rating", rating);
        }else{
            movieObject.add("movie_rating", null);
        }

        JsonArray genresArray = new JsonArray();
        for (String genre : genres) {
            genresArray.add(genre);
        }
        movieObject.add("genres", genresArray);

        JsonArray starsArray = new JsonArray();
        for (StarPojo star : stars) {
            starsArray.add(star.toJson());
        }
        movieObject.add("stars", starsArray);

        return movieObject;
    }
}
