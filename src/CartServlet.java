import MongoRetrieval.MongoMovies;
import MongoRetrieval.MoviePojo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.*;
//import java.util.Date;

@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
    private CartItem getMovieFromDatabase(String movieId){
        MongoMovies mongoMovies = new MongoMovies();
        MoviePojo movie = mongoMovies.getMovieById(movieId);
        if(movie == null) return null;
        return new CartItem(movie.getMovieId(), movie.getMovieTitle(), 1, movie.getMoviePrice());
    }

    @SuppressWarnings("unchecked")
    private HashMap<String, CartItem> getCartFromSession(HttpSession session){
        HashMap<String, CartItem> cart = (HashMap<String, CartItem>) session.getAttribute("cart");
        if(cart == null){
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    private JsonObject buildCartJson(HashMap<String, CartItem> cart){
        JsonObject responseJsonObject = new JsonObject();
        JsonArray cartJsonArray = new JsonArray();
        double total = 0.0;
        for(CartItem item : cart.values()){
            if(item == null) continue;
            JsonObject itemJson = new JsonObject();
            double subtotal = item.getPrice() * item.getQuantity();
            total += subtotal;

            itemJson.addProperty("movieId", item.getMovieId());
            itemJson.addProperty("title", item.getTitle());
            itemJson.addProperty("quantity", item.getQuantity());
            itemJson.addProperty("price", item.getPrice());
            itemJson.addProperty("subtotal", subtotal);

            cartJsonArray.add(itemJson);
        }
        responseJsonObject.add("cart", cartJsonArray);
        responseJsonObject.addProperty("total", total);

        return responseJsonObject;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        HashMap<String, CartItem> cart = getCartFromSession(session);

        response.setContentType("application/json");
        response.getWriter().write(buildCartJson(cart).toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        String movieId = request.getParameter("movieId");
        HttpSession session = request.getSession();

        @SuppressWarnings("unchecked")
        HashMap<String, CartItem> cart = getCartFromSession(session);

        if("add".equals(action)){
            if(cart.containsKey(movieId)){
                cart.get(movieId).increaseQuantity();
            }else{
                CartItem item = getMovieFromDatabase(movieId);
                if (item != null) {
                    cart.put(movieId, item);
                }
            }
        }else if("increase".equals(action) && cart.containsKey(movieId)){
            cart.get(movieId).increaseQuantity();
        }else if("decrease".equals(action) && cart.containsKey(movieId)){
            CartItem item = cart.get(movieId);
            item.decreaseQuantity();
            if (item.getQuantity() <= 0) {
                cart.remove(movieId);
            }
        }else if("delete".equals(action)){
            cart.remove(movieId);
        }

        response.setContentType("application/json");
        response.getWriter().write(buildCartJson(cart).toString());
    }
}
