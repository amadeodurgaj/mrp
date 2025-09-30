package org.mrp.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UserController {

    private final ObjectMapper mapper = new ObjectMapper();

    public void handleRegister(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> request = mapper.readValue(exchange.getRequestBody(), Map.class);

            String username = request.get("username");
            String password = request.get("password");

            System.out.println("Registering user: " + username);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User " + username + " registered successfully");

            sendJson(exchange, 201, response);
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    public void handleLogin(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> request = mapper.readValue(exchange.getRequestBody(), Map.class);

            String username = request.get("username");
            String password = request.get("password");

            String token = username + "-mrpToken";

            Map<String, String> response = new HashMap<>();
            response.put("token", token);

            sendJson(exchange, 200, response);
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private void sendJson(HttpExchange exchange, int status, Object response) throws IOException {
        String json = mapper.writeValueAsString(response);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
