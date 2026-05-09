package Parsing;

public class Movie {
    private String id;
    private String title;
    private int year;
    private String director;
    private double price;

    public Movie(){}

    public Movie(String id, String title, int year, String director, double price) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.price = price;
    }


}
