package DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import modele.APPUser;

import java.util.List;

public class APPUserDao extends AbstractDAO<APPUser, Long> {

    private EntityManagerFactory entityManagerFactory;

    // Méthode pour injecter l'EntityManagerFactory
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public APPUser findById(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(APPUser.class, id);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<APPUser> findAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        System.out.println("ici user");
        try {

            return entityManager.createQuery("SELECT u FROM APPUser u", APPUser.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

    // Implémentation de la méthode save
    public void save(APPUser user) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            if (user.getId() == null) {
                entityManager.persist(user);
            } else {
                entityManager.merge(user);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void delete(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            APPUser user = entityManager.find(APPUser.class, id);
            if (user != null) {
                entityManager.remove(user);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }
}
