package is.project3.core.orders;

public class MessageFactory {

    public static CustomerOrder createCustomerOrder(int quantity, int price, String product, String userID) {
        return new CustomerOrder(quantity, price, product, userID);
    }

    public static ShopOrder createShopOrder(int quantity, String product) {
        return new ShopOrder(quantity, product);
    }

    public static ShopResponse createShopResponse(int quantity, int price, String product, String userID, Message.STATUS status) {
        return new ShopResponse(quantity, price, product, userID, status);
    }

    public static SupplierResponse createSupplierResponse(String product, int quantity, int unitPrice) {
        return new SupplierResponse(product, quantity, unitPrice);
    }

    public static OwnerOrder createOwnerOrder(String product, int quantity) {
        return new OwnerOrder(quantity, product);
    }

/*    public static <T extends Message> T emptyMessageOf (Class<T> className) {
    }*/
}
