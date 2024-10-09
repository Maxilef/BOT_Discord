package DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import lombok.extern.slf4j.Slf4j;
import modele.Identifiable;

import java.util.List;

@Slf4j
public abstract class AbstractDAO<T extends Identifiable<ID>, ID> {

    protected EntityManagerFactory entityManagerFactory = DbManager.getEntityManagerFactory();

    public T create(T entity) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(entity);
            transaction.commit();
            entityManager.detach(entity);
            return entity;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Erreur lors de la création de l'entité", e);
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public T update(T entity) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            T updatedEntity = entityManager.merge(entity);
            transaction.commit();
            return updatedEntity;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Erreur lors de la mise à jour de l'entité", e);
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public void delete(T entity) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            T e = (T) entityManager.find(entity.getClass(), entity.getId());
            entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Erreur lors de la suppression de l'entité", e);
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public abstract T findById(ID id);

    public abstract List<T> findAll();

    public void close() {
        entityManagerFactory.close();
    }
}
