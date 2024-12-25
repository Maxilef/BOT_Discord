package DAO;

import jakarta.persistence.EntityManager;
import modele.Channel;
import java.util.List;

public class ChanelDao extends AbstractDAO<Channel, Long> {

    @Override
    public Channel findById(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(Channel.class, id);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Channel> findAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.createQuery("SELECT c FROM Channel c", Channel.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    protected Class<Channel> getEntityClass() {
        return null;
    }

    @Override
    public void delete(Long id) {
        return ;
    }

    public Channel findByName(String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.createQuery("SELECT c FROM Channel c WHERE c.nom = :name", Channel.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    public List<Channel> findByType(String type) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.createQuery("SELECT c FROM Channel c WHERE c.type = :type", Channel.class)
                    .setParameter("type", type)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }
}
