package REST;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")  // <-- Ajoutez cette annotation pour spécifier l'URL du resource
public class ApiRest {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        String users = "[{\"id\":1, \"name\":\"John Doe\"}, {\"id\":2, \"name\":\"Jane Doe\"}]";
        return Response.ok(users).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") int id) {
        String user = "{\"id\":" + id + ", \"name\":\"John Doe\"}";
        return Response.ok(user).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(String user) {
        // Logique de création d'utilisateur ici
        return Response.status(Response.Status.CREATED).entity(user).build();
    }
}
