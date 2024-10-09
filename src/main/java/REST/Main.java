package REST;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class Main {

    public static final String BASE_URI = "http://localhost:8080/api/";

    public static HttpServer startServer() {
        // Crée une instance de ResourceConfig qui enregistre les classes de ressource (votre API)
        final ResourceConfig rc = new JerseyConfig();

        // Démarre le serveur Grizzly
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) {
        final HttpServer server = startServer();
        System.out.println("Jersey app started at " + BASE_URI);
        try {
            System.in.read(); // Appuyez sur n'importe quelle touche pour arrêter le serveur
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.shutdown();
        }
    }
}
