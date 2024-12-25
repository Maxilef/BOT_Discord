package DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import modele.Identifiable;
import modele.Role;

import java.util.List;
import java.util.UUID;

public class RoleDao extends AbstractDAO<Role, UUID> {

    private EntityManagerFactory entityManagerFactory;

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Role findById(UUID id) { // Utilisation de UUID
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(Role.class, id); // Recherche par UUID
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Role> findAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.createQuery("SELECT r FROM Role r", Role.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    protected Class<Role> getEntityClass() {
        return null;
    }

    public void save(Role role) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            if (role.getId() == null) {
                entityManager.persist(role);
            } else {
                entityManager.merge(role);
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
            Role role = entityManager.find(Role.class, id); // Recherche par UUID
            if (role != null) {
                entityManager.remove(role);
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

    public Role findByName(String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.createQuery("SELECT r FROM Role r WHERE r.nom = :name", Role.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;  // Aucun rôle avec ce nom trouvé
        } finally {
            entityManager.close();
        }
    }
}
