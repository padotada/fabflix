package MongoRetrieval;

import Parsing.Movie;

import java.util.List;

public class MoviePojo {
    String id;
    String title;
    int year;
    String director;
    float price;

    List<GenrePojo> genres;

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
    public MoviePojo setMoviePrice(float price){
        this.price = price;
        return this;
    }
    
}
