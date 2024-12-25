package dao;

import DAO.RoleDao;
import DAO.DbManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import modele.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class RoleDaoTest {

    private EntityManager entityManager;
    private EntityTransaction transaction;
    private RoleDao RoleDao;

    @BeforeEach
    public void setUp() {
        // Obtenir un EntityManager à partir de la configuration de l'application principale
        entityManager = DbManager.getEntityManagerFactory().createEntityManager();
        transaction = entityManager.getTransaction();
        RoleDao = new RoleDao();
        RoleDao.setEntityManagerFactory(DbManager.getEntityManagerFactory());

        // Démarrer une transaction
        transaction.begin();

        // Supprimer tous les rôles pour avoir un état propre
        entityManager.createQuery("DELETE FROM Role").executeUpdate();
    }

    @AfterEach
    public void tearDown() {
        // Nettoyer la base de données en supprimant tous les rôles
        // entityManager.createQuery("DELETE FROM Role").executeUpdate();

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
    @Order(1)
    public void testSave() {

        // Créer un rôle fictif et l'enregistrer
        Role role = new Role();
        role.setNom("Admin7891");

        RoleDao.save(role);

        // Vérifier que le rôle a été enregistré correctement
        Role foundRole = RoleDao.findById(role.getId());
        Assertions.assertNotNull(foundRole);
        assertEquals("Admin7891", foundRole.getNom());
    }

    @Test
    @Order(2)
    public void testFindAllRoles() {
        // Créer et sauvegarder des rôles fictifs
        Role role1 = new Role();
        role1.setNom("Admin24111");
        RoleDao.save(role1);

        Role role2 = new Role();
        role2.setNom("User24111");
        RoleDao.save(role2);

        // Log pour vérifier la sauvegarde
        System.out.println("Rôles sauvegardés :");
        System.out.println(role1.getId() + " - " + role1.getNom());
        System.out.println(role2.getId() + " - " + role2.getNom());

        // Récupérer tous les rôles
        List<Role> roles = RoleDao.findAll();

        // Log pour vérifier la récupération
        System.out.println("Rôles récupérés :");
        for (Role r : roles) {
            System.out.println(r.getId() + " - " + r.getNom());
        }

        // Vérifier que la liste contient les rôles attendus
        Assertions.assertNotNull(roles); // Vérifie que la liste n'est pas nulle
        Assertions.assertEquals(2, roles.size()); // Vérifie qu'il y a 2 rôles
        Assertions.assertTrue(roles.stream().anyMatch(r -> "Admin24111".equals(r.getNom())));
        Assertions.assertTrue(roles.stream().anyMatch(r -> "User24111".equals(r.getNom())));
    }

    @Test
    @Order(4)
    public void testRoleWithoutUser() {
        // Créer un rôle sans utilisateur
        Role role = new Role();
        role.setNom("Guest");
        RoleDao.save(role);

        // Vérifier que la table de jointure est vide
        List<Object[]> results = entityManager.createNativeQuery("SELECT * FROM user_roles").getResultList();
        Assertions.assertTrue(results.isEmpty(), "La table de jointure devrait être vide.");
    }

}
