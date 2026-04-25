public class CartItem {
    private String movieId;
    private String title;
    private int quantity;
    private double price;

    public CartItem(String movieId, String title, int quantity, double price){
        this.movieId = movieId;
        this.title = title;
        this.quantity = quantity;
        this.price = price;
    }

    public String getMovieId(){
        return movieId;
    }

    public String getTitle(){
        return title;
    }

    public int getQuantity(){
        return quantity;
    }

    public double getPrice(){
        return price;
    }

    public void increaseQuantity(){
        quantity++;
    }

    public void decreaseQuantity(){
        quantity--;
    }
}
