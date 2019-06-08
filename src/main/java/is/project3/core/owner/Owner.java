package is.project3.core.owner;

import is.project3.core.orders.MessageFactory;

class Owner {

    public static void main(String[] args) {
        OwnerProducerToReorderTopic OwnerPRT = new OwnerProducerToReorderTopic(MessageFactory.createOwnerOrder(args[0], Integer.parseInt(args[1])));
        Thread t1 = new Thread(OwnerPRT);
        t1.start();
    }

}




