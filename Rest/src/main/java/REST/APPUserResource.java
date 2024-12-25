package REST;

import DAO.APPUserDao;
import DAO.DbManager;
import DAO.RoleDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import modele.APPUser;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modele.Role;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Path("/users")
public class APPUserResource {

    // DAO user
    private APPUserDao appUserDao ;
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private RoleDao roleDao;


    public APPUserResource() {
        entityManagerFactory = Persistence.createEntityManagerFactory("myPU");
        entityManager = entityManagerFactory.createEntityManager();
        appUserDao = new APPUserDao();
        roleDao = new RoleDao();
        appUserDao.setEntityManagerFactory(DbManager.getEntityManagerFactory());
        roleDao.setEntityManagerFactory(DbManager.getEntityManagerFactory());
    }

    // Obtenir tous les utilisateurs
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<APPUser> getAllUsers() {

        //System.out.println("GET /users called");
        List<APPUser> users = appUserDao.findAll();
        return users;
    }


    // Obtenir un utilisateur par ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") UUID id) {
        APPUser user = appUserDao.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(user).build();
    }

    // Créer un nouvel utilisateur
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(APPUser user) {
        try {
            appUserDao.save(user); // Utilisation de save pour persister l'utilisateur
            return Response.status(Response.Status.CREATED).entity(user).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erreur lors de la création de l'utilisateur").build();
        }
    }

    // Mettre à jour un utilisateur existant
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") UUID id, APPUser updatedUser) {
        APPUser existingUser = appUserDao.findById(id);
        if (existingUser == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        existingUser.setNom(updatedUser.getNom());
        existingUser.setEmail(updatedUser.getEmail());
        appUserDao.save(existingUser); // Mise à jour de l'utilisateur existant

        return Response.ok(existingUser).build();
    }

    // Supprimer un utilisateur
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") UUID id) {
        APPUser user = appUserDao.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        appUserDao.delete(id);  // Supprimer l'utilisateur en utilisant son ID
        return Response.status(Response.Status.NO_CONTENT).build();
    }


    // Obtenir les rôles d'un utilisateur
    @GET
    @Path("/{id}/roles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserRoles(@PathParam("id") UUID userId) {
        // Trouver l'utilisateur par son ID
        APPUser user = appUserDao.findById(userId);

        // Vérifier si l'utilisateur existe
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Utilisateur non trouvé")
                    .build();
        }

        // Vérifier si l'utilisateur a des rôles
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT) // 204 No Content
                    .entity("Aucun rôle associé à cet utilisateur")
                    .build();
        }

        // Retourner les rôles de l'utilisateur sous forme de réponse JSON
        return Response.ok(user.getRoles()).build();
    }


    // Supprimer les rôles d'un utilisateur
    @DELETE
    @Path("/{id}/roles")
    public Response deleteUserRoles(@PathParam("id") UUID userId) {
        APPUser user = appUserDao.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Begin transaction
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            // Remove the roles associated with the user
            user.getRoles().clear(); // Assuming 'roles' is a collection in your APPUser entity

            // Persist the updated user without roles
            appUserDao.save(user);

            transaction.commit(); // Commit transaction

            return Response.status(Response.Status.NO_CONTENT).build(); // 204 No Content
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback(); // Rollback if error occurs
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la suppression des rôles de l'utilisateur")
                    .build();
        }
    }
    @GET
    @Path("/{id}/roles/{roleId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserRole(@PathParam("id") UUID userId, @PathParam("roleId") UUID roleId) {
        // Trouver l'utilisateur par son ID
        APPUser user = appUserDao.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Utilisateur non trouvé").build();
        }

        // Vérifier si l'utilisateur a des rôles
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).entity("Aucun rôle associé à cet utilisateur").build();
        }

        // Trouver le rôle par son ID dans la liste des rôles de l'utilisateur
        Role role = user.getRoles().stream()
                .filter(r -> r.getId().equals(roleId))  // Comparaison des IDs des rôles
                .findFirst()
                .orElse(null);

        if (role == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Rôle non trouvé pour cet utilisateur").build();
        }

        // Retourner le rôle de l'utilisateur sous forme de réponse JSON
        return Response.ok(role).build();
    }


    @POST
    @Path("/{id}/roles/{roleId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignRoleToUser(@PathParam("id") UUID userId, @PathParam("roleId") UUID roleId) {
        try {
            // Trouver l'utilisateur par son ID
            APPUser user = appUserDao.findById(userId);
            if (user == null) {
                System.out.println("Utilisateur non trouvé");
                return Response.status(Response.Status.NOT_FOUND).entity("Utilisateur non trouvé").build();
            }

            // Trouver le rôle par son ID
            Role role = roleDao.findById(roleId); // Assurez-vous d'avoir un DAO pour les rôles
            if (role == null) {
                System.out.println("Rôle non trouvé");
                return Response.status(Response.Status.NOT_FOUND).entity("Rôle non trouvé").build();
            }

            // Ajouter le rôle à l'utilisateur
            if (user.getRoles() == null) {
                user.setRoles(new HashSet<>()); // Crée une nouvelle collection de rôles si elle est vide
            }
            user.getRoles().add(role);

            // Sauvegarder les changements
            appUserDao.save(user);
            System.out.println("Rôle ajouté avec succès");

            return Response.status(Response.Status.CREATED).entity("Rôle ajouté avec succès").build();
        } catch (Exception e) {
            e.printStackTrace();  // Affiche l'exception dans la sortie standard
            System.out.println("Erreur interne lors de l'ajout du rôle: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erreur interne lors de l'ajout du rôle à l'utilisateur").build();
        }
    }



    // Supprimer un rôle d'un utilisateur
    @DELETE
    @Path("/{id}/roles/{roleId}")
    public Response removeRoleFromUser(@PathParam("id") UUID userId, @PathParam("roleId") UUID roleId) {
        // Trouver l'utilisateur par son ID
        APPUser user = appUserDao.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Utilisateur non trouvé").build();
        }

        // Trouver le rôle par son ID
        Role role = roleDao.findById(roleId); // Assurez-vous d'avoir un DAO pour les rôles
        if (role == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Rôle non trouvé").build();
        }

        // Supprimer le rôle de l'utilisateur
        if (user.getRoles() != null && user.getRoles().contains(role)) {
            user.getRoles().remove(role);

            // Sauvegarder les changements
            appUserDao.save(user);

            return Response.status(Response.Status.NO_CONTENT).build(); // 204 No Content
        }

        return Response.status(Response.Status.NOT_FOUND).entity("Rôle non associé à cet utilisateur").build();
    }



}
