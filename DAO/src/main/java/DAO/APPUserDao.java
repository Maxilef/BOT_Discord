package DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.transaction.Transactional;
import modele.APPUser;
import modele.Role;

import java.util.List;
import java.util.UUID;

public class APPUserDao extends AbstractDAO<APPUser, UUID> {

    private EntityManagerFactory entityManagerFactory;

    // Méthode pour injecter l'EntityManagerFactory
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public APPUser findById(UUID id) { // Utilisation de UUID
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(APPUser.class, id); // Recherche par UUID
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

    @Override
    protected Class<APPUser> getEntityClass() {
        return null;
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
    public void delete(UUID id) { // Utilisation de UUID pour supprimer
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            APPUser user = entityManager.find(APPUser.class, id); // Recherche par UUID
            if (user != null) {
                entityManager.remove(user); // Suppression si trouvé
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

    @Transactional
    public void deleteUserAndRoles(UUID userId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        try {
            // Charger l'utilisateur
            APPUser user = em.find(APPUser.class, userId);
            if (user != null) {
                // Supprimer les relations avec les rôles
                user.getRoles().forEach(role -> role.getUsers().remove(user));
                user.getRoles().clear();

                // Supprimer l'utilisateur
                em.remove(user);
            } else {
                throw new IllegalArgumentException("Utilisateur introuvable avec l'ID : " + userId);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Transactional
    public void deleteRole(UUID roleId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        try {
            // Charger le rôle
            Role role = em.find(Role.class, roleId);
            if (role != null) {
                // Supprimer les relations avec les utilisateurs
                role.getUsers().forEach(user -> user.getRoles().remove(role));
                role.getUsers().clear();

                // Supprimer le rôle
                em.remove(role);
            } else {
                throw new IllegalArgumentException("Rôle introuvable avec l'ID : " + roleId);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
