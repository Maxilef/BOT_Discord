package REST;

import DAO.APPUserDao;
import DAO.DbManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import modele.APPUser;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/users")
public class APPUserResource {

    // DAO user
    private APPUserDao appUserDao ;
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;



    public APPUserResource() {
        entityManagerFactory = Persistence.createEntityManagerFactory("myPU");
        entityManager = entityManagerFactory.createEntityManager();
        appUserDao = new APPUserDao();
        appUserDao.setEntityManagerFactory(DbManager.getEntityManagerFactory());
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
    public Response getUserById(@PathParam("id") Long id) {
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
    public Response updateUser(@PathParam("id") Long id, APPUser updatedUser) {
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
    public Response deleteUser(@PathParam("id") Long id) {
        APPUser user = appUserDao.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        appUserDao.delete(id);  // Supprimer l'utilisateur en utilisant son ID
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
