package Parsing;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    private String id;
    private String title;
    private int year;
    private String director;
    private List<String> genres = new ArrayList<>();

    public Movie(){}

    public Movie(String id, String title, int year, String director) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
    }

    public String getId() {return id;}
    public String getTitle() {return title;}
    public int getYear() {return year;}
    public String getDirector() {return director;}

    public void setId(String id) {this.id = id;}
    public void setTitle(String title) {this.title = title;}
    public void setYear(int year) {this.year = year;}
    public void setDirector(String director) {this.director = director;}

    public List<String> getGenres() {return genres;}
    public void addGenre(String genre) {this.genres.add(genre);}
    public void setGenres(List<String> genres) {this.genres = genres;}
}
