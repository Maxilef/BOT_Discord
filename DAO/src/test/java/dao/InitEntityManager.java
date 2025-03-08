package dao;

import DAO.DbManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class InitEntityManager {
    private static EntityManager entityManager;
    private static EntityTransaction transaction;

    @BeforeAll
    public static void init() {
        // Obtenir un EntityManager
        entityManager = DbManager.getEntityManagerFactory().createEntityManager();
        transaction = entityManager.getTransaction();

        // Démarrer une transaction
        transaction.begin();

        // Supprimer tous les utilisateurs pour avoir un état propre
        entityManager.createQuery("DELETE FROM APPUser").executeUpdate();
    }

    @Test
    public void testInit() {
        System.out.println("✅ EntityManager initialisé !");
    }

    @AfterAll
    public static void cleanUp() {
        if (transaction.isActive()) {
            transaction.rollback();
        }
        if (entityManager != null) {
            entityManager.close();
        }
    }
}
