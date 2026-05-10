package Parsing;

public class Actor {
    private String name;
    private int birth_year;

    public Actor(String name, int birth_year) {
        this.name = name;
        this.birth_year = birth_year;
    }
    public Actor(){}

    public String getName() {return name;}
    public int getBirth_year() {return birth_year;}

    public void setName(String name) {this.name = name;}
    public void setBirth_year(int birth_year) {this.birth_year = birth_year;}
}
