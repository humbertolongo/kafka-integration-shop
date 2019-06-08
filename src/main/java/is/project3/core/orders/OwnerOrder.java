package is.project3.core.orders;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties
public class OwnerOrder implements Message {

    private int quantity;
    private String product;

    public OwnerOrder() {
    }

    public OwnerOrder(int quantity, String product) {
        this.quantity = quantity;
        this.product = product;
    }

    @Override
    public String getSender() {
        return null;
    }

    @Override
    public int getPrice() {
        return 0;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public STATUS getStatus() {
        return null;
    }

    public String getProduct() {
        return product;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OwnerOrder shopOrder = (OwnerOrder) o;
        return quantity == shopOrder.quantity &&
                Objects.equals(product, shopOrder.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity, product);
    }

    @Override
    public String toString() {
        return "OwnerOrder{" +
                "quantity=" + quantity +
                ", product='" + product + '\'' +
                '}';
    }
}
