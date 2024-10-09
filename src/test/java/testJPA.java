import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import modele.APPUser;
import DAO.DbManager;

public class testJPA {
    public static void main(String[] args) {
        EntityManager em = DbManager.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            // Créer un nouvel utilisateur
            APPUser user = new APPUser();
            user.setNom("Jean Dupont");
            user.setEmail("jean.dupont@example.com");

            // Sauvegarder l'utilisateur
            em.persist(user);

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
