package Classes;

public class User {

    private final String username;

    private boolean search_mode = false; // if true use stored search, if false use stored browse

    private String title = "A";
    private String year = "";
    private String director = "";
    private String star = "";

    private String first = "a";
    private String genre = "";

    private int page = 1;
    private int records = 10;

    private String title_first = "false";
    private int title_sort = 0; // 0 for nothing, 1 for decreasing, -1 for increasing
    private int rating_sort = 1; // 0 for nothing, 1 for decreasing, -1 for increasing

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {return username;}
    public String getTitle() {return title;}
    public String getYear() {return year;}
    public String getDirector() {return director;}
    public String getStar() {return star;}

    public boolean isSearchMode() {return search_mode;}

    public String getFirst() {return first;}
    public String getGenre() {return genre;}

    public int getPage() {return page;}
    public int getNumRecords() {return records;}

    public String getTitleFirst() {return title_first;}
    public int getTitleSort() {return title_sort;}
    public int getRatingSort() {return rating_sort;}

    public void setTitle(String title) {this.title = title;}
    public void setYear(String year) {this.year = year;}
    public void setDirector(String director) {this.director = director;}
    public void setStar(String star) {this.star = star;}

    public void setSearchMode(boolean search_mode) {this.search_mode = search_mode;}

    public void setFirst(String first) {this.first = first;}
    public void setGenre(String genre) {this.genre = genre;}

    public void setPage(int page) {this.page = page;}
    public void setNumRecords(int records) {this.records = records;}

    public void setTitleFirst(String title_first) {this.title_first = title_first;}
    public void setTitleSort(int title_sort) {this.title_sort = title_sort;}
    public void setRatingSort(int rating_sort) {this.rating_sort = rating_sort;}
}