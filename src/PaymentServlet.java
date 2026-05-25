import MongoRetrieval.CustomerPojo;
import MongoRetrieval.MongoCustomers;
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

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/place-order")
public class PaymentServlet extends HttpServlet{
    private DataSource dataSource;

    public void init(ServletConfig config){
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        JsonObject responseJsonObject = new JsonObject();

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String creditCardId = request.getParameter("creditCardId");
        String expiration = request.getParameter("expiration");


        System.out.println("firstName = " + firstName);
        System.out.println("lastName = " + lastName);
        System.out.println("creditCardId = " + creditCardId);
        System.out.println("expiration = " + expiration);
        HttpSession session = request.getSession();

        HashMap<String, CartItem> cart = (HashMap<String, CartItem>) session.getAttribute("cart");
        if(cart == null || cart.isEmpty()){
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Your cart is empty.");
            response.getWriter().write(responseJsonObject.toString());
            return;
        }

        MongoCustomers mongoCustomers = new MongoCustomers();
        CustomerPojo customer = mongoCustomers.getCustomer(creditCardId, firstName, lastName, expiration);

        try (Connection connection = dataSource.getConnection()) {
            boolean validPayment = isValidPayment(
                    connection,
                    creditCardId,
                    firstName,
                    lastName,
                    expiration
            );

            if (!validPayment) {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Invalid payment information.");
                response.getWriter().write(responseJsonObject.toString());
                return;
            }
            Integer customerId = getCustomerId(session, creditCardId, connection);

            if (customerId == null) {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Could not identify customer.");
                response.getWriter().write(responseJsonObject.toString());
                return;
            }

            JsonObject confirmationData = insertSalesAndBuildConfirmation(connection, customerId, cart);

            session.setAttribute("confirmationData", confirmationData);
            session.setAttribute("lastOrder", confirmationData);
            session.removeAttribute("cart");

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "Order placed successfully.");
            response.getWriter().write(responseJsonObject.toString());

        } catch (Exception e) {
            e.printStackTrace();

            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Server error while placing order.");
            response.getWriter().write(responseJsonObject.toString());
        }
    }
    private boolean isValidPayment(
            Connection connection,
            String creditCardId,
            String firstName,
            String lastName,
            String expiration
    ) throws Exception {
        String query = """
            SELECT id
            FROM credit_cards
            WHERE id = ?
              AND first_name = ?
              AND last_name = ?
              AND expiration = ?
        """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, creditCardId);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, expiration);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private Integer getCustomerId(
            HttpSession session,
            String creditCardId,
            Connection connection
    ) throws Exception {
        String query = """
            SELECT id
            FROM customers
            WHERE credit_card_id = ?
            LIMIT 1
        """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, creditCardId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }

        return null;
    }

    private JsonObject insertSalesAndBuildConfirmation(
            Connection connection, Integer customerId, HashMap<String, CartItem> cart)
            throws Exception {
        String query = """
            INSERT INTO sales (customer_id, movie_id, sale_date, quantity)
            VALUES (?, ?, CURDATE(), ?)
        """;
        JsonObject confirmationData = new JsonObject();
        JsonArray saleIds = new JsonArray();
        JsonArray items = new JsonArray();
        double totalPrice = 0.0;
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            for (CartItem item : cart.values()) {
                statement.setInt(1, customerId);
                statement.setString(2, item.getMovieId());
                statement.setInt(3, item.getQuantity());
                statement.executeUpdate();
                int saleId = -1;
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        saleId = generatedKeys.getInt(1);
                        saleIds.add(saleId);
                    }
                }

                double price = item.getPrice();
                int quantity = item.getQuantity();
                double subtotal = price * quantity;

                JsonObject itemObject = new JsonObject();
                itemObject.addProperty("sale_id", saleId);
                itemObject.addProperty("movie_id", item.getMovieId());
                itemObject.addProperty("movie_title", item.getTitle());
                itemObject.addProperty("quantity", quantity);
                itemObject.addProperty("price", price);
                itemObject.addProperty("subtotal", subtotal);

                items.add(itemObject);

                totalPrice += subtotal;
            }
        }

        confirmationData.add("sale_ids", saleIds);
        confirmationData.add("items", items);
        confirmationData.addProperty("total_price", totalPrice);

        return confirmationData;
    }
}
