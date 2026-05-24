package MongoRetrieval;

public class GenrePojo {
    int id;
    String name;
    public GenrePojo(){}
    public GenrePojo(int id, String name){
        this.id = id;
        this.name = name;
    }
    public GenrePojo setGenreId(int id){
        this.id = id;
        return this;
    }
    public GenrePojo setGenreName(String name){
        this.name = name;
        return this;
    }
}
