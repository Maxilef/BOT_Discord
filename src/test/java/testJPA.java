import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import modele.APPUser;
import modele.Role;
import DAO.DbManager;

public class testJPA {
    public static void main(String[] args) {
        EntityManager em = DbManager.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            // Créer un rôle administrateur
            Role adminRole = new Role();
            adminRole.setNom("ADMIN");

            // Persist le rôle administrateur
            em.persist(adminRole);

            // Créer un nouvel utilisateur
            APPUser user = new APPUser();
            user.setNom("Jean Dupont admin");
            user.setEmail("jean.dupontadmin@example.com");

            // Associer le rôle administrateur à l'utilisateur
            user.getRoles().add(adminRole);

            // Sauvegarder l'utilisateur (les rôles seront aussi persistés)
            em.persist(user);

            transaction.commit();

            // Affichage des résultats pour vérifier la persistance
            System.out.println("Utilisateur et rôle enregistrés avec succès.");
            System.out.println("Utilisateur : " + user.getNom() + ", Email : " + user.getEmail());
            System.out.println("Rôle : " + adminRole.getNom());

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
