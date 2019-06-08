package is.project3.data;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


public class ItemDAO implements DAO<Item> {

    private EntityManager em;


    public ItemDAO(EntityManager em) {
        this.em = em;
    }

    public Optional<Item> getByName(String name) {
        TypedQuery<Item> q = em.createQuery("SELECT i FROM Item i WHERE i.name = :parameter", Item.class).setParameter("parameter", name);

        List<Item> results = q.getResultList();
        if (results.isEmpty())
            return Optional.empty();
        else
            return Optional.of(results.get(0));
    }

    @Override
    public Optional<Item> get(int id) {
        return Optional.ofNullable(em.find(Item.class, id));
    }

    @Override
    public List<Item> getAll() {
        TypedQuery<Item> q = em.createQuery("SELECT i FROM Item i", Item.class);
        return q.getResultList();
    }

    @Override
    public void save(Item item) {
        executeAsTransaction(entityManager -> entityManager.persist(item));
    }

    @Override
    public void update(Item itemToUpdate) {

        Optional<Item> itemOptional = get(itemToUpdate.getId());
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();

            item.setStock(itemToUpdate.getStock());
            item.setPrice(itemToUpdate.getPrice());
            executeAsTransaction(entityManager -> entityManager.merge(item));
        }
    }

    @Override
    public void delete(Item item) {
        executeAsTransaction(entityManager -> entityManager.remove(item));
    }

    private void executeAsTransaction(Consumer<EntityManager> consumer) {
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            consumer.accept(em);
            et.commit();
        } catch (RuntimeException e) {
            et.rollback();
            throw e;
        }
    }
}
