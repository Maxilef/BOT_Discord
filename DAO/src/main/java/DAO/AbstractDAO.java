package DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import lombok.extern.slf4j.Slf4j;
import modele.Identifiable;

import java.util.List;
import java.util.UUID;

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
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public void delete(ID id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            T entity = entityManager.find(getEntityClass(), id);
            if (entity != null) {
                entityManager.remove(entity);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public T findById(ID id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(getEntityClass(), id);
        } finally {
            entityManager.close();
        }
    }

    public abstract List<T> findAll();

    protected abstract Class<T> getEntityClass();
}
