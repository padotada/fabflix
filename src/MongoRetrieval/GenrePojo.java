package MongoRetrieval;

import com.google.gson.JsonObject;

public class GenrePojo {
    Integer id;
    String name;
    public GenrePojo(){}
    public static GenrePojo builder(){
        return new GenrePojo();
    }
    public GenrePojo setGenreId(Integer id){
        this.id = id;
        return this;
    }
    public GenrePojo setGenreName(String name){
        this.name = name;
        return this;
    }
    public Integer getGenreId(){
        return this.id;
    }
    public String getGenreName(){
        return this.name;
    }
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("genre_id", this.id);
        jsonObject.addProperty("genre_name", this.name);
        return jsonObject;
    }
}
