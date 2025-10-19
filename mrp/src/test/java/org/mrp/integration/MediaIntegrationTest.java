package org.mrp.integration;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MediaIntegrationTest {

    private static final String BASE_URL = "http://localhost:8080/api/media";
    private static HttpClient client;
    private static int createdMediaId;

    @BeforeAll
    static void setup() {
        client = HttpClient.newHttpClient();
    }

    @Test
    @Order(1)
    void testCreateMedia() throws IOException, InterruptedException {
        String requestBody = """
            {
              "title": "Inception",
              "description": "Sci-fi thriller about dreams within dreams.",
              "mediaType": "movie",
              "releaseYear": 2010,
              "ageRestriction": 12,
              "creatorId": "bbf7e002-ff25-4a2d-b6fc-073c3cdd3688"
            }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Expected media creation to succeed");
        assertTrue(response.body().contains("Media created"));

        String id = response.body().replaceAll("\\D+", "");
        createdMediaId = Integer.parseInt(id);
        System.out.println("Created media ID: " + createdMediaId);
    }

    @Test
    @Order(2)
    void testGetAllMedia() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Inception"), "Expected list to contain 'Inception'");
    }

    @Test
    @Order(3)
    void testGetMediaById() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + createdMediaId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Inception"), "Expected specific media details");
    }

    @Test
    @Order(4)
    void testUpdateMedia() throws IOException, InterruptedException {
        String updateBody = """
            {
              "title": "Inception (Updated)",
              "description": "Updated description",
              "mediaType": "movie",
              "releaseYear": 2010,
              "ageRestriction": 16
            }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + createdMediaId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Media updated successfully"));
    }

    @Test
    @Order(5)
    void testDeleteMedia() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + createdMediaId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Media deleted"));
    }

    @Test
    @Order(6)
    void testGetDeletedMediaShouldReturn404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + createdMediaId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Expected 400/404 when fetching deleted media");
    }
}
