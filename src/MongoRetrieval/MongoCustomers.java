package MongoRetrieval;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.regex.Pattern;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoCustomers {
    String uri = System.getenv("MONGO_URI");
    String email;

    public MongoCustomers(String email) {this.email = email;}

    public CustomerPojo getCustomer() {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase movieDB = mongoClient.getDatabase("movieDB");
            MongoCollection<Document> customerCollection = movieDB.getCollection("customers");

            Document customer = customerCollection.find(Filters.eq("email", email)).first();

            if (customer == null) {
                return null;
            }

            return documentToCustomerPojo(customer);
        }
    }

    private CustomerPojo documentToCustomerPojo(Document customer) {

        CustomerPojo customerPojo = CustomerPojo.builder()
                .setCustomerId(customer.getInteger("_id"))
                .setFirstName(customer.getString("first_name"))
                .setLastName(customer.getString("last_name"))
                .setAddress(customer.getString("address"))
                .setEmail(customer.getString("email"))
                .setPassword(customer.getString("password"));


        return customerPojo;
    }
}
