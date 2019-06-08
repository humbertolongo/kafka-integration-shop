package is.project3.core.orders;

public interface Message {

    String getSender();

    String getProduct();

    int getPrice();

    int getQuantity();

    STATUS getStatus();


    enum STATUS {
        ON_HOLD,
        REJECTED,
        REQUESTED_REORDER,
        SUCCEDEED,
        UNKNOWN,
        UNKNOWN_PRODUCT
    }
}
