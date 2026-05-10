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

    public String getId() {return id;}
    public String getTitle() {return title;}
    public int getYear() {return year;}
    public String getDirector() {return director;}
    public double getPrice() {return price;}

    public void setId(String id) {this.id = id;}
    public void setTitle(String title) {this.title = title;}
    public void setYear(int year) {this.year = year;}
    public void setDirector(String director) {this.director = director;}
    public void setPrice(double price) {this.price = price;}


}
