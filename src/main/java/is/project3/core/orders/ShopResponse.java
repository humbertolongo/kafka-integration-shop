package is.project3.core.orders;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties
public class ShopResponse implements Message {

    private int quantity;
    private int price;
    private String product;
    private String userID;
    private Message.STATUS status;

    public ShopResponse() {
    }

    public ShopResponse(int quantity, int price, String product, String userID, Message.STATUS status) {
        this.quantity = quantity;
        this.price = price;
        this.product = product;
        this.userID = userID;
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public STATUS getStatus() {
        return status;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String getSender() {
        return "Shop";
    }

    public String getProduct() {
        return product;
    }

    public String getUserID() {
        return userID;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopResponse that = (ShopResponse) o;
        return quantity == that.quantity &&
                price == that.price &&
                Objects.equals(product, that.product) &&
                Objects.equals(userID, that.userID) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity, price, product, userID, status);
    }

    @Override
    public String toString() {
        return "ShopResponse{" +
                "quantity=" + quantity +
                ", price=" + price +
                ", product='" + product + '\'' +
                ", userID='" + userID + '\'' +
                ", status=" + status +
                '}';
    }
}
