package is.project3.core.supplier;

class Supplier {
    public static void main(String[] args) {

        SupplierConsumerFromReorderTopic SupplierCRT = new SupplierConsumerFromReorderTopic();
        Thread t1 = new Thread(SupplierCRT);
        t1.start();

    }
}


