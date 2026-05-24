package MongoRetrieval;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class StarPojo {
    private String starId;
    private String starName;
    private Integer starBirthYear;
    private List<MovieInfo> movies;

    private static class MovieInfo{
        private String movieId;
        private String movieTitle;
        private Integer movieYear;

        public MovieInfo(String movieId, String movieTitle, Integer movieYear) {
            this.movieId = movieId;
            this.movieTitle = movieTitle;
            this.movieYear = movieYear;
        }
    }
    private StarPojo() {
        this.movies = new ArrayList<>();
    }

    public static StarPojo builder() {
        return new StarPojo();
    }

    public StarPojo setStarId(String starId) {
        this.starId = starId;
        return this;
    }

    public StarPojo setStarName(String starName) {
        this.starName = starName;
        return this;
    }

    public StarPojo setStarBirthYear(Integer starBirthYear) {
        this.starBirthYear = starBirthYear;
        return this;
    }
    public void addMovie(String movieId, String movieTitle, Integer movieYear){
        this.movies.add(new MovieInfo(movieId, movieTitle, movieYear));
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("star_id", starId);
        jsonObject.addProperty("star_name", starName);

        if (starBirthYear == null) {
            jsonObject.addProperty("birth_year", "N/A");
        } else {
            jsonObject.addProperty("birth_year", starBirthYear);
        }
        if(movies != null){
            JsonArray movieArray = new JsonArray();

            for (MovieInfo movie : this.movies) {
                JsonObject movieObject = new JsonObject();
                movieObject.addProperty("movie_id", movie.movieId);
                movieObject.addProperty("movie_title", movie.movieTitle);
                movieObject.addProperty("movie_year", movie.movieYear);
                movieArray.add(movieObject);
            }
            jsonObject.add("movies", movieArray);
        }
        return jsonObject;
    }
}
