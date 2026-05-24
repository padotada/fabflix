package MongoRetrieval;

import java.util.Date;

public class SalesPojo {
    int saleId;
    int customerId;
    String movieId;
    Date saleDate;
    int quantity;

    public SalesPojo(int saleId, int customerId, String movieId, Date saleDate, int quantity){
        this.saleId = saleId;
        this.customerId = customerId;
        this.movieId = movieId;
        this.saleDate = saleDate;
        this.quantity = quantity;
    }
    public SalesPojo setSaleId(int saleId){
        this.saleId = saleId;
        return this;
    }
    public SalesPojo setCustomerId(int customerId){
        this.customerId = customerId;
        return this;
    }
    public SalesPojo setMovieId(String movieId){
        this.movieId = movieId;
        return this;
    }
    public SalesPojo setSaleDate(Date saleDate){
        this.saleDate = saleDate;
        return this;
    }
    public SalesPojo setQuantity(int quantity){
        this.quantity = quantity;
        return this;
    }

}
