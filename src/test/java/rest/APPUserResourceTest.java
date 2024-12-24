import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class APPUserResourceTest {

    private static final String BASE_URL = "http://localhost:8087/users";

    @Test
    public void testGetAllUsers() throws IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        connection.disconnect();

        assertEquals(200, responseCode);
    }

    @Test
    public void testGetUserById() throws IOException {
        testCreateUser();
        URL url = new URL(BASE_URL + "/1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        connection.disconnect();

        assertEquals(200, responseCode);
    }

    @Test
    public void testCreateUser() throws IOException {
        String jsonInputString = "{\n" +
                "    \"nom\": \"Dupont\",\n" +
                "    \"email\": \"dupont@example.com\"\n" +
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
        connection.disconnect();
        assertEquals(201, responseCode);
    }

    @Test
    public void testUpdateUser() throws IOException {
        String jsonInputString = "{\n" +
                "    \"nom\": \"DupontUpdated\",\n" +
                "    \"email\": \"dupontupdated@example.com\"\n" +
                "}";

        URL url = new URL(BASE_URL + "/1");
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

        assertEquals(200, responseCode);
    }

    @Test
    public void testDeleteUser() throws IOException {
        URL url = new URL(BASE_URL + "/1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        int responseCode = connection.getResponseCode();
        connection.disconnect();

        // Vérifie que le code de statut est 204 (NO CONTENT)
        assertEquals(204, responseCode);
    }
}
