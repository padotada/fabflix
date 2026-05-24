package MongoRetrieval;

import java.util.Date;

public class CreditCardPojo {
    String id;
    Date expiration;
    public CreditCardPojo(){}
    public CreditCardPojo(String id, Date expiration){
        this.id = id;
        this.expiration = expiration;
    }
    public CreditCardPojo setId(String id) {
        this.id = id;
        return this;
    }
    public CreditCardPojo setExpiration(Date expiration) {
        this.expiration = expiration;
        return this;
    }
}
