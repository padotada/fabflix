package Classes;

public class User {

    private final String username;

    private boolean search_mode = true; // if true use stored search, if false use stored browse

    private String title = "";
    private String year = "";
    private String director = "";
    private String star = "";

    private char first = 'a';
    private String genre = "";

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {return username;}
    public String getTitle() {return title;}
    public String getYear() {return year;}
    public String getDirector() {return director;}
    public String getStar() {return star;}

    public boolean isSearch_mode() {return search_mode;}

    public char getFirst() {return first;}
    public String getGenre() {return genre;}

    public void setTitle(String title) {this.title = title;}
    public void setYear(String year) {this.year = year;}
    public void setDirector(String director) {this.director = director;}
    public void setStar(String star) {this.star = star;}

    public void setSearch_mode(boolean search_mode) {this.search_mode = search_mode;}

    public void setFirst(char first) {this.first = first;}
    public void setGenre(String genre) {this.genre = genre;}
}