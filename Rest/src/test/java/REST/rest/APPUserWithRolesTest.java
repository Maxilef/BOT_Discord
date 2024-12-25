package REST.rest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.json.JSONObject;
import org.json.JSONArray;

@TestMethodOrder(org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
public class APPUserWithRolesTest {

    private static final String BASE_URL = "http://localhost:8087/users";
    private static final String ROLES_URL = "http://localhost:8087/roles";
    private static UUID createdUserId;
    private static UUID adminRoleId;
    private static UUID masterRoleId;

    @Test
    @Order(1)
    public void testCreateUserWithRoles() throws Exception {
        String jsonInputString = "{\n" +
                "    \"nom\": \"Daupont\",\n" +
                "    \"email\": \"daupont2@example.com\",\n" +
                "    \"roles\": [\n" +
                "        {\"nom\": \"Admin\"},\n" +
                "        {\"nom\": \"Master\"}\n" +
                "    ]\n" +
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

        // Lecture de la réponse JSON
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            String responseBody = response.toString();

            // Analyse du JSON
            JSONObject jsonResponse = new JSONObject(responseBody);
            createdUserId = UUID.fromString(jsonResponse.getString("id"));  // L'ID de l'utilisateur

            Assertions.assertNotNull(createdUserId, "L'UUID de l'utilisateur créé ne doit pas être nul");

            System.out.println("Utilisateur créé avec l'ID: " + createdUserId);
        }

        connection.disconnect();
    }

    @Test
    @Order(3)
    public void testDeleteUserRoles() throws Exception {
        // Vérifiez que l'utilisateur a bien été créé avant de tenter de supprimer ses rôles
        Assertions.assertNotNull(createdUserId, "L'utilisateur n'a pas été créé avec succès");

        // Affiche l'UUID de l'utilisateur pour vérifier qu'il est correct
        System.out.println("UUID de l'utilisateur créé : " + createdUserId);

        // Supprime les relations entre l'utilisateur et ses rôles
        deleteUserRoles(createdUserId);

        // Vérifie que la suppression des rôles a été effectuée avec succès
        int responseCode = checkRolesDeleted(createdUserId);
        Assertions.assertEquals(204, responseCode, "Les rôles de l'utilisateur n'ont pas été supprimés correctement.");
    }
    // Méthode pour supprimer les rôles d'un utilisateur
    public void deleteUserRoles(UUID userId) throws Exception {
        // Construction de l'URL pour supprimer les rôles
        URL url = new URL(BASE_URL + "/" + userId + "/roles");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setDoOutput(true);

        // Obtention du code de réponse
        int responseCode = connection.getResponseCode();
        System.out.println("Code de réponse de la suppression des rôles : " + responseCode);

        // Vérifiez si la suppression des rôles a réussi avec un code 204
        Assertions.assertEquals(204, responseCode, "Les relations utilisateur-rôle n'ont pas été correctement supprimées.");

        connection.disconnect();
    }
    // Méthode pour vérifier si les rôles ont bien été supprimés
    public int checkRolesDeleted(UUID userId) throws Exception {
        URL url = new URL(BASE_URL + "/" + userId + "/roles");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Code de réponse après avoir essayé d'obtenir les rôles de l'utilisateur
        int responseCode = connection.getResponseCode();
        System.out.println("Code de réponse lors de la vérification des rôles : " + responseCode);

        // Vérifier que les rôles ont été supprimés, donc la réponse devrait être 404
        return responseCode;
    }


    @Test
    @Order(2)
    public void testAssignRoleToUser() throws Exception {
        // Vérifiez que l'utilisateur a bien été créé avant d'ajouter un rôle
        Assertions.assertNotNull(createdUserId, "L'utilisateur n'a pas été créé avec succès");

        // Créez un rôle nommé "role_test"
        String roleId = createRole("role_test");

        // Assignez le rôle "role_test" à l'utilisateur
        assignRoleToUser(createdUserId, roleId);

        // Vérifiez que le rôle a bien été ajouté
        int responseCode = checkUserRoleExists(createdUserId, roleId);
        Assertions.assertEquals(200, responseCode, "Le rôle n'a pas été correctement ajouté à l'utilisateur.");

        System.out.println("Rôle ajouté avec succès à l'utilisateur.");
    }

    // Méthode pour créer un rôle
    public String createRole(String roleName) throws Exception {
        // Construction du corps de la requête
        String jsonInputString = "{ \"nom\": \"" + roleName + "\" }";

        // URL de l'API des rôles
        URL url = new URL("http://localhost:8087/roles");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        // Envoi de la requête avec le rôle
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Lecture de la réponse
        int responseCode = connection.getResponseCode();
        Assertions.assertEquals(201, responseCode, "La création du rôle a échoué.");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // Supposons que la réponse contient l'ID du rôle créé au format {"id":"<UUID>"}
            JSONObject jsonResponse = new JSONObject(response.toString());
            String roleId = jsonResponse.getString("id");
            Assertions.assertNotNull(roleId, "L'ID du rôle créé ne doit pas être nul.");
            System.out.println("Rôle créé avec succès. ID : " + roleId);
            return roleId;
        }
    }

    // Méthode pour assigner un rôle existant à un utilisateur
    public void assignRoleToUser(UUID userId, String roleId) throws Exception {
        // Construction de l'URL pour associer un rôle
        URL url = new URL(BASE_URL + "/" + userId + "/roles/" + roleId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        // Obtention du code de réponse
        int responseCode = connection.getResponseCode();
        System.out.println("Code de réponse pour l'ajout d'un rôle : " + responseCode);

        // Vérifiez si l'ajout du rôle a réussi avec un code 200 ou 201
        Assertions.assertEquals(201, responseCode, "L'ajout du rôle à l'utilisateur a échoué.");

        connection.disconnect();
    }

    // Méthode pour vérifier si un rôle a été attribué à l'utilisateur
    public int checkUserRoleExists(UUID userId, String roleId) throws Exception {
        URL url = new URL(BASE_URL + "/" + userId + "/roles/" + roleId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Vérification de la réponse
        int responseCode = connection.getResponseCode();
        System.out.println("Code de réponse lors de la vérification du rôle : " + responseCode);

        connection.disconnect();
        return responseCode; // La réponse attendue pour un rôle existant est 200
    }


    @Test
    @Order(4)
    public void testDeleteUser() throws Exception {
        // Vérifiez que l'utilisateur a bien été créé avant de tenter de le supprimer
        Assertions.assertNotNull(createdUserId, "L'utilisateur n'a pas été créé avec succès");

        // Appel de l'API pour supprimer l'utilisateur
        int responseCode = deleteUserApi(createdUserId);

        // Vérification du code de réponse
        Assertions.assertEquals(204, responseCode, "La suppression de l'utilisateur a échoué.");

        // Vérifiez que l'utilisateur est effectivement supprimé
        responseCode = checkUserDeleted(createdUserId);
        Assertions.assertEquals(404, responseCode, "L'utilisateur n'a pas été correctement supprimé.");

        System.out.println("Utilisateur supprimé avec succès.");
    }
    // Méthode pour vérifier si l'utilisateur a été supprimé
    public int checkUserDeleted(UUID userId) throws Exception {
        URL url = new URL(BASE_URL + "/" + userId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Vérification si l'utilisateur est toujours disponible
        int responseCode = connection.getResponseCode();
        System.out.println("Code de réponse lors de la vérification de l'utilisateur : " + responseCode);

        connection.disconnect();
        return responseCode; // La réponse attendue pour un utilisateur supprimé est 404
    }
    public int deleteUserApi(UUID userId) throws Exception {
        // Suppression de l'utilisateur via l'API
        URL url = new URL(BASE_URL + "/" + userId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setDoOutput(true);

        int responseCode = connection.getResponseCode();
        connection.disconnect();
        return responseCode; // Retourne le code de réponse pour vérification
    }


}
