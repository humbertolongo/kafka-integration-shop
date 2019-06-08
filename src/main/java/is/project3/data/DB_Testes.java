package is.project3.data;

import javax.persistence.*;

class DB_Testes {

    //private static final String PERSISTENCE_UNIT_NAME = "Project3BD";
    //private static EntityManagerFactory factory;

    //@PersistenceContext(unitName ="Project3BD")
    //static EntityManager em;

    public static void main(String[] args) throws Exception {
        //factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        //EntityManager em = factory.createEntityManager();


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
