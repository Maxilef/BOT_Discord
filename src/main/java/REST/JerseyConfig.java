package REST;

import org.glassfish.jersey.server.ResourceConfig;
import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        // Remplacez "com.example" par le package où se trouvent vos classes REST
        packages("REST");
    }
}
