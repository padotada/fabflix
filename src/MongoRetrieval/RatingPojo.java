package MongoRetrieval;

public class RatingPojo {
    Double rating;
    Integer voteCount;
    public RatingPojo(){}
    public RatingPojo(Double rating, Integer voteCount){
        this.rating = rating;
        this.voteCount = voteCount;
    }
    public RatingPojo setRating(Double rating){
        this.rating = rating;
        return this;
    }
    public RatingPojo setVoteCount(Integer voteCount){
        this.voteCount = voteCount;
        return this;
    }
}
