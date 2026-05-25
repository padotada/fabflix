import MongoRetrieval.CustomerPojo;
import MongoRetrieval.MongoCustomers;
import MongoWriter.MongoSalesAdder;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.sql.*;
import java.util.Date;

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

        try{
            MongoCustomers mongoCustomers = new MongoCustomers();
            LocalDate date = LocalDate.parse(expiration);
            Date mongoDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

            CustomerPojo customer = mongoCustomers.getCustomer(creditCardId, firstName, lastName, mongoDate);
            boolean validPayment = customer != null;
            if (!validPayment) {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Invalid payment information.");
                response.getWriter().write(responseJsonObject.toString());
                return;
            }
            Integer customerId = customer.getCustomerId();
//            if (customerId == null) {
//                responseJsonObject.addProperty("status", "fail");
//                responseJsonObject.addProperty("message", "Could not identify customer.");
//                response.getWriter().write(responseJsonObject.toString());
//                return;
//            }

            JsonObject confirmationData = insertSalesAndBuildConfirmation(customerId, cart);

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

    private JsonObject insertSalesAndBuildConfirmation(
            Integer customerId, HashMap<String, CartItem> cart)
            throws Exception {

        MongoSalesAdder mongoSalesAdder = new MongoSalesAdder();

        JsonObject confirmationData = new JsonObject();
        JsonArray saleIds = new JsonArray();
        JsonArray items = new JsonArray();
        double totalPrice = 0.0;

        for (CartItem item : cart.values()) {
            int saleId = mongoSalesAdder.addSales(customerId, item.getMovieId(), new java.sql.Date(new Date().getTime()), item.getQuantity());
            saleIds.add(saleId);

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


        confirmationData.add("sale_ids", saleIds);
        confirmationData.add("items", items);
        confirmationData.addProperty("total_price", totalPrice);

        return confirmationData;
    }
}
