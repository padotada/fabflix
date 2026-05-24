package MongoRetrieval;

import java.util.List;

public class MoviePojo {
    String id;
    String title;
    int year;
    String director;
    double price;
    RatingPojo rating;
    List<GenrePojo> genres;
    List<SingleStarPojo> stars;
    private MoviePojo(){

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
    public MoviePojo setMovieYear(int year){
        this.year = year;
        return this;
    }
    public MoviePojo setMovieDirector(String director){
        this.director = director;
        return this;
    }
    public MoviePojo setMoviePrice(double price){
        this.price = price;
        return this;
    }
    public MoviePojo setRating(RatingPojo rating){
        this.rating = rating;
        return this;
    }
    public MoviePojo setGenres(List<GenrePojo> genres){
        this.genres = genres;
        return this;
    }
    public MoviePojo setStars(List<SingleStarPojo> stars){
        this.stars = stars;
        return this;
    }
}
