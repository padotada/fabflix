package Parsing;

public class ActorMoviePair {
    private String actorName;
    private String movieId;

    public ActorMoviePair(String actorName, String movieId) {
        this.actorName = actorName;
        this.movieId = movieId;
    }
    public ActorMoviePair() {}

    public String getActorName() {return actorName;}
    public String getMovieId() {return movieId;}

    public void setActorName(String actorName) {this.actorName = actorName;}
    public void setMovieId(String movieId) {this.movieId = movieId;}
}
