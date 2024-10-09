package DAO;

import jakarta.persistence.EntityManager;
import modele.Role;
import java.util.List;

public class RoleDao extends AbstractDAO<Role, Long> {

    @Override
    public Role findById(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(Role.class, id);
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

    public Role findByName(String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.createQuery("SELECT r FROM Role r WHERE r.nom = :name", Role.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } finally {
            entityManager.close();
        }
    }
}
