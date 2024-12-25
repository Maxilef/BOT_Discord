package REST;

import org.glassfish.jersey.server.ResourceConfig;
import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        packages("REST");
        register(TestResource.class);
        register(APPUserResource.class);
        register(RoleResource.class);
    }
}

