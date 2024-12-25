package REST;

import DAO.APPUserDao;
import DAO.RoleDao;
import DAO.DbManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modele.Role;

import java.util.List;
import java.util.UUID;

@Path("/roles")
public class RoleResource {

    private RoleDao roleDao ;
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    public RoleResource() {
        entityManagerFactory = Persistence.createEntityManagerFactory("myPU");
        entityManager = entityManagerFactory.createEntityManager();
        roleDao = new RoleDao();
        roleDao.setEntityManagerFactory(DbManager.getEntityManagerFactory());
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Role> getAllRoles() {

        //System.out.println("GET /users called");
        List<Role> roles = roleDao.findAll();
        return roles;
    }



    // Obtenir un rôle par ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoleById(@PathParam("id") UUID id) {
        Role role = roleDao.findById(id);
        if (role == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(role).build();
    }

    // Créer un nouveau rôle
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRole(Role role) {
        try {
            roleDao.save(role);
            return Response.status(Response.Status.CREATED).entity(role).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erreur lors de la création du rôle").build();
        }
    }

    // Mettre à jour un rôle existant
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRole(@PathParam("id") UUID id, Role updatedRole) {
        Role existingRole = roleDao.findById(id);
        if (existingRole == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        existingRole.setNom(updatedRole.getNom());
        roleDao.save(existingRole);

        return Response.ok(existingRole).build();
    }

    // Supprimer un rôle
    @DELETE
    @Path("/{id}")
    public Response deleteRole(@PathParam("id") UUID id) {
        Role role = roleDao.findById(id);
        if (role == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        roleDao.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
