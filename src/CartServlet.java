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
import javax.sql.DataSource;
import java.io.IOException;
import java.util.*;
import java.sql.*;
import java.util.Date;

@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    private CartItem getMovieFromDatabase(String movieId){
        String moviePriceQuery = """
            SELECT id, title, price
            FROM movies
            WHERE id = ?
        """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement moviePriceStatement = connection.prepareStatement(moviePriceQuery);) {
            moviePriceStatement.setString(1, movieId);
            ResultSet resultSet = moviePriceStatement.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getString("id");
                String title = resultSet.getString("title");
                double price = resultSet.getDouble("price");

                return new CartItem(id, title, 1, price);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        HashMap<String, CartItem> cart = getCartFromSession(session);

        JsonObject responseJsonObject = new JsonObject();
        JsonArray cartJsonArray = new JsonArray();

        double total = 0.0;
        for (CartItem item : cart.values()) {
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

        response.setContentType("application/json");
        response.getWriter().write(responseJsonObject.toString());
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
                cart.put(movieId, item);
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
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("status", "success");

        response.setContentType("application/json");
        response.getWriter().write(responseJsonObject.toString());
    }
}
