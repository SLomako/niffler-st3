package guru.qa.niffler.db.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class JpaService {

    protected final EntityManager em;

    protected JpaService(EntityManager em) {
        this.em = em;
    }

    protected <T> void persist(T entity) {
        tx(em -> em.persist(entity));
    }

    protected <T> void remove(T entity) {
        tx(em -> em.remove(entity));
    }

    protected void removeById(Class<?> entityClass, Object primaryKey) {
        tx(em -> {
            Object entity = em.find(entityClass, primaryKey);
            if (entity != null) {
                em.remove(entity);
            } else {
                throw new IllegalArgumentException();
            }
        });
    }

    protected <T> T merge(T entity) {
        return txWithResult(em -> em.merge(entity));
    }

    protected <T> T find(Class<T> entityClass, Object primaryKey) {
        return txWithResult(em -> em.find(entityClass, primaryKey));
    }

    protected void findAndMerge(Class<?> entityClass, Object primaryKey, Consumer<Object> updateFunc) {
        tx(em -> {
            Object entity = em.find(entityClass, primaryKey);
            em.detach(entity);
            updateFunc.accept(entity);
            em.merge(entity);
        });
    }

    private void tx(Consumer<EntityManager> action) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            action.accept(em);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    private <T> T txWithResult(Function<EntityManager, T> action) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            T result = action.apply(em);
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}
