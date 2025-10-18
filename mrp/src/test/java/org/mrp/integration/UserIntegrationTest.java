package org.mrp.integration;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserIntegrationTest {

    private static final String BASE_URL = "http://localhost:8080/api/users";
    private static HttpClient client;

    @BeforeAll
    static void setup() {
        client = HttpClient.newHttpClient();
    }

    @Test
    @Order(1)
    void testRegisterUser() throws IOException, InterruptedException {
        String requestBody = """
            {
                "username": "testuser",
                "password": "testpass"
            }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Expected user registration to succeed");
        assertTrue(response.body().contains("registered successfully"));
    }

    @Test
    @Order(2)
    void testRegisterDuplicateUser() throws IOException, InterruptedException {
        String requestBody = """
            {
                "username": "testuser",
                "password": "anotherpass"
            }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(409, response.statusCode(), "Expected conflict for duplicate username");
    }

    @Test
    @Order(3)
    void testLoginUser() throws IOException, InterruptedException {
        String requestBody = """
            {
                "username": "testuser",
                "password": "testpass"
            }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Expected successful login");
        assertTrue(response.body().contains("token"));
    }

    @Test
    @Order(4)
    void testLoginInvalidCredentials() throws IOException, InterruptedException {
        String requestBody = """
            {
                "username": "testuser",
                "password": "wrongpass"
            }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(401, response.statusCode(), "Expected 401 for invalid login");
    }

    @Test
    @Order(5)
    void testGetProfile() throws IOException, InterruptedException {
        // First, login to get token
        String loginBody = """
            {
                "username": "testuser",
                "password": "testpass"
            }
        """;

        HttpRequest loginReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginBody))
                .build();

        HttpResponse<String> loginRes = client.send(loginReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, loginRes.statusCode());

        String token = loginRes.body().split("\"token\":\"")[1].split("\"")[0]; // crude token extract

        HttpRequest profileReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/testuser/profile"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> profileRes = client.send(profileReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, profileRes.statusCode(), "Profile should be accessible");
        assertTrue(profileRes.body().contains("testuser"));
    }

    @Test
    @Order(6)
    void testUpdateProfile() throws IOException, InterruptedException {
        String loginBody = """
            {
                "username": "testuser",
                "password": "testpass"
            }
        """;

        HttpRequest loginReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginBody))
                .build();

        HttpResponse<String> loginRes = client.send(loginReq, HttpResponse.BodyHandlers.ofString());
        String token = loginRes.body().split("\"token\":\"")[1].split("\"")[0];

        String updateBody = """
            {
                "email": "testuser@example.com",
                "favoriteGenre": "sci-fi"
            }
        """;

        HttpRequest updateReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/testuser/profile"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(updateBody))
                .build();

        HttpResponse<String> updateRes = client.send(updateReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, updateRes.statusCode());
        assertTrue(updateRes.body().contains("Profile updated successfully"));
    }
}
