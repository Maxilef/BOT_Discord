package DAO;

import jakarta.persistence.EntityManager;
import modele.APPUser;
import java.util.List;

public class APPUserDao extends AbstractDAO<APPUser, Long> {

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
        try {
            return entityManager.createQuery("SELECT u FROM APPUser u", APPUser.class).getResultList();
        } finally {
            entityManager.close();
        }
    }
}
