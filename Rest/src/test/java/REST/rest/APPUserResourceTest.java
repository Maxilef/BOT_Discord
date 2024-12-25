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
public class APPUserResourceTest {

    private static final String BASE_URL = "http://localhost:8087/users";
    private static UUID createdUserId; // Stocke l'UUID de l'utilisateur créé

    @Test
    @Order(1)
    public void testCreateUser() throws IOException {
        String jsonInputString = "{\n" +
                "    \"nom\": \"Daupont\",\n" +
                "    \"email\": \"daupont@example.com\"\n" +
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
        Assertions.assertEquals(201, responseCode);

        // Lecture de l'UUID généré depuis la réponse
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            // Supposons que l'UUID est renvoyé dans la réponse JSON sous la forme {"id":"<UUID>"}
            String responseBody = response.toString();
            createdUserId = UUID.fromString(responseBody.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1"));
            Assertions.assertNotNull(createdUserId, "L'UUID de l'utilisateur créé ne doit pas être nul");
        }

        connection.disconnect();
    }

    @Test
    @Order(2)
    public void testGetAllUsers() throws IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        connection.disconnect();

        Assertions.assertEquals(200, responseCode);
    }

    @Test
    @Order(3)
    public void testGetUserById() throws IOException {
        // Utilisation de l'UUID stocké
        URL url = new URL(BASE_URL + "/" + createdUserId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        connection.disconnect();

        Assertions.assertEquals(200, responseCode);
    }

    @Test
    @Order(4)
    public void testUpdateUser() throws IOException {
        String jsonInputString = "{\n" +
                "    \"nom\": \"DupontUpdated\",\n" +
                "    \"email\": \"dupontupdated@example.com\"\n" +
                "}";

        // Utilisation de l'UUID stocké
        URL url = new URL(BASE_URL + "/" + createdUserId);
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
    public void testDeleteUser() throws IOException {
        // Utilisation de l'UUID stocké
        URL url = new URL(BASE_URL + "/" + createdUserId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        int responseCode = connection.getResponseCode();
        connection.disconnect();

        // Vérifie que le code de statut est 204 (NO CONTENT)
        Assertions.assertEquals(204, responseCode);
    }
}
