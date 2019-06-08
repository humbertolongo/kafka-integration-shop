package is.project3.data;

import javax.persistence.*;

public enum PersistenceManager {
    INSTANCE;
    private EntityManagerFactory emFactory;

    PersistenceManager() {
        emFactory = Persistence.createEntityManagerFactory("Project3BD");
        Cache cache = emFactory.getCache();
        cache.evictAll();
    }

    public EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }

    public void close() {
        emFactory.close();
    }
}
