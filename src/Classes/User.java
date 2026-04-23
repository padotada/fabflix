package Classes;

public class User {

    private final String username;
    private String title = "";
    private String year = "";
    private String director = "";
    private String star = "";

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {return username;}
    public String getTitle() {return title;}
    public String getYear() {return year;}
    public String getDirector() {return director;}
    public String getStar() {return star;}

    public void setTitle(String title) {this.title = title;}
    public void setYear(String year) {this.year = year;}
    public void setDirector(String director) {this.director = director;}
    public void setStar(String star) {this.star = star;}
}