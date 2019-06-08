package is.project3.core.orders;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties
public class SupplierResponse implements Message {

    private String product;
    private int quantity;
    private int unitPrice;

    public SupplierResponse() {
    }

    public SupplierResponse(String product, int quantity, int unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }


    @Override
    public String getSender() {
        return "Supplier";
    }

    public String getProduct() {
        return product;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public int getPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public STATUS getStatus() {
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplierResponse that = (SupplierResponse) o;
        return quantity == that.quantity &&
                unitPrice == that.unitPrice &&
                Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity, unitPrice);
    }

    @Override
    public String toString() {
        return "SupplierResponse{" +
                "product='" + product + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
