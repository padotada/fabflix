package MongoRetrieval;

public class CustomerPojo {
    int id;
    String firstName;
    String lastName;
    String creditCardId;
    String address;
    String email;
    String password;
    CreditCardPojo creditCard;
    public CustomerPojo() {}

    public static CustomerPojo builder() {
        return new CustomerPojo();
    }
    public CustomerPojo setCustomerId(int id) {
        this.id = id;
        return this;
    }
    public CustomerPojo setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    public CustomerPojo setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    public CustomerPojo setAddress(String address) {
        this.address = address;
        return this;
    }
    public CustomerPojo setEmail(String email) {
        this.email = email;
        return this;
    }
    public CustomerPojo setPassword(String password) {
        this.password = password;
        return this;
    }
    public CustomerPojo setCreditCard(CreditCardPojo creditCard) {
        this.creditCard = creditCard;
        return this;
    }
}
