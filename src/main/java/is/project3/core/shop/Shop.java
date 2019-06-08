package is.project3.core.shop;


class Shop {

    public static void main(String[] args) {

        ShopConsumerFromPurchasesTopic ShopCRT = new ShopConsumerFromPurchasesTopic();
        ShopConsumerFromShipmentsTopic ShopCST = new ShopConsumerFromShipmentsTopic();

        Thread t1 = new Thread(ShopCRT);
        Thread t2 = new Thread(ShopCST);

        t1.start();
        t2.start();

    }
}

