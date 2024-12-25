package DAO;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DbManager {

    // BDD en local DOCKER
    private static EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("myPU");

    public static EntityManagerFactory getEntityManagerFactory() { return ENTITY_MANAGER_FACTORY; }

}
