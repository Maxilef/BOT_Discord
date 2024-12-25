package dao;

import DAO.APPUserDao;
import DAO.DbManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import modele.APPUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class APPUserDaoTest {

    private EntityManager entityManager;
    private EntityTransaction transaction;
    private APPUserDao appUserDao;

    @BeforeEach
    public void setUp() {
        // Obtenir un EntityManager à partir de la configuration de l'application principale
        entityManager = DbManager.getEntityManagerFactory().createEntityManager();
        transaction = entityManager.getTransaction();
        appUserDao = new APPUserDao();
        appUserDao.setEntityManagerFactory(DbManager.getEntityManagerFactory());

        // Démarrer une transaction
        transaction.begin();

        // Supprimer tous les utilisateurs pour avoir un état propre
        entityManager.createQuery("DELETE FROM APPUser").executeUpdate();
    }


    @AfterEach
    public void tearDown() {
        // Nettoyer la base de données en supprimant tous les utilisateurs
        //entityManager.createQuery("DELETE FROM APPUser").executeUpdate();

        // Faire un rollback pour annuler les changements dans la base de données
        if (transaction.isActive()) {
            transaction.rollback();
        }

        // Fermer l'EntityManager
        if (entityManager != null) {
            entityManager.close();
        }
    }


    @Test
    public void testSave() {
        // Créer un utilisateur fictif et l'enregistrer
        APPUser user = new APPUser();
        user.setNom("Test User");
        user.setEmail("testuser@example.com");

        appUserDao.save(user);

        // Vérifier que l'utilisateur a été enregistré correctement
        APPUser foundUser = appUserDao.findById(user.getId());
        Assertions.assertNotNull(foundUser);
        assertEquals("Test User", foundUser.getNom());
    }

    @Test
    public void testFindAllUsers() {
        // Créer quelques utilisateurs fictifs
        APPUser user1 = new APPUser();
        user1.setNom("User 1");
        user1.setEmail("user1@example.com");
        appUserDao.save(user1);

        APPUser user2 = new APPUser();
        user2.setNom("User 2");
        user2.setEmail("user2@example.com");
        appUserDao.save(user2);

        // Récupérer tous les utilisateurs
        List<APPUser> users = appUserDao.findAll();

        for (APPUser user : users) {
            System.out.println(user.getNom());
        }
    }
}

