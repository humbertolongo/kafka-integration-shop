package is.project3.data;

import javax.persistence.*;

class DB_Testes {

    public static void main(String[] args) throws Exception {

        Item i = new Item();
        i.setName("Product 1");
        i.setPrice(20);
        i.setStock(10);

        EntityManager em = PersistenceManager.INSTANCE.getEntityManager();
        em.getTransaction()
                .begin();
        em.persist(i);
        em.getTransaction()
                .commit();
        em.close();
        PersistenceManager.INSTANCE.close();

    }

}
