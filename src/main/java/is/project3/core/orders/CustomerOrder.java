package is.project3.core.orders;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties
public class CustomerOrder implements Message {


    private int quantity;
    private int price;
    private String product;
    private String userID;

    public CustomerOrder() {
    }

    public CustomerOrder(int quantity, int price, String product, String userID) {
        this.quantity = quantity;
        this.price = price;
        this.product = product;
        this.userID = userID;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public String getProduct() {
        return product;
    }

    public String getUserID() {
        return userID;
    }

    @Override
    public STATUS getStatus() {
        return null;
    }

    @Override
    public String getSender() {
        return userID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerOrder customerOrder = (CustomerOrder) o;
        return quantity == customerOrder.quantity &&
                price == customerOrder.price &&
                Objects.equals(product, customerOrder.product) &&
                Objects.equals(userID, customerOrder.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity, price, product, userID);
    }

    @Override
    public String toString() {
        return "CustomerOrder{" +
                "quantity=" + quantity +
                ", price=" + price +
                ", product='" + product + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }
}
