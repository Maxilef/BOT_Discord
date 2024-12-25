package REST.rest;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RoleResourceTest {

    private static final String BASE_URL = "http://localhost:8087/roles";
    private static UUID createdRoleId; // Stocke l'UUID du rôle créé

    @Test
    @Order(1)
    public void testCreateRole() throws IOException {
        String jsonInputString = "{\n" +
                "    \"nom\": \"Adminnnnnnn\"\n" +
                "}";

        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        // Envoi des données JSON dans la requête POST
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);  // Affichage du code de réponse

        // Capture du corps de la réponse
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getErrorStream() != null ?
                        connection.getErrorStream() : connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            // Supposons que l'UUID est renvoyé dans la réponse JSON sous la forme {"id":"<UUID>"}
            String responseBody = response.toString();
            createdRoleId = UUID.fromString(responseBody.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1"));
            Assertions.assertNotNull(createdRoleId, "L'UUID de l'utilisateur créé ne doit pas être nul");
            System.out.println("Response Body: " + response.toString());  // Affichage du corps de la réponse
        }

        connection.disconnect();
        Assertions.assertEquals(201, responseCode);
    }


    @Test
    @Order(2)
    public void testGetAllRoles() throws IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        connection.disconnect();

        // Vérifie que le code de statut est 200 (OK)
        Assertions.assertEquals(200, responseCode);
    }

    @Test
    @Order(3)
    public void testGetRoleById() throws IOException {
        // Utilisation de l'UUID stocké
        URL url = new URL(BASE_URL + "/" + createdRoleId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        connection.disconnect();

        // Vérifie que le code de statut est 200 (OK)
        Assertions.assertEquals(200, responseCode);
    }

    @Test
    @Order(4)
    public void testUpdateRole() throws IOException {
        String jsonInputString = "{\n" +
                "    \"nom\": \"AdminUpdated\"\n" +
                "}";

        // Utilisation de l'UUID stocké
        URL url = new URL(BASE_URL + "/" + createdRoleId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        // Envoi des données JSON dans la requête PUT
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        connection.disconnect();

        Assertions.assertEquals(200, responseCode);
    }

    @Test
    @Order(5)
    public void testDeleteRole() throws IOException {
        // Utilisation de l'UUID stocké
        URL url = new URL(BASE_URL + "/" + createdRoleId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        int responseCode = connection.getResponseCode();
        connection.disconnect();

        // Vérifie que le code de statut est 204 (NO CONTENT)
        Assertions.assertEquals(204, responseCode);
    }
}
