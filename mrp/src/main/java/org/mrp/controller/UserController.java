package org.mrp.controller;
import org.mrp.model.User;
import org.mrp.service.UserService;
import org.mrp.util.JSONUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class UserController {
    private final UserService userService = new UserService();

    private record AuthContext(User authUser, String requestedUsername) {}

    public void handleRegister(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        Map<String, String> request = JSONUtil.fromJson(exchange.getRequestBody(), Map.class);
        String username = request.get("username");
        String password = request.get("password");

        int result = userService.registerUser(username, password);
        Map<String, String> response;

        if (result == UserService.CODE_USER_EXISTS) {
            response = Map.of("error", "User: " + username + " already exists");
            JSONUtil.sendJson(exchange, 409, response);
        } else if (result == UserService.CODE_LOGIN_SUCCESSFUL) {
            response = Map.of("message", "User: " + username + " registered successfully");
            JSONUtil.sendJson(exchange, 201, response);
        } else {
            response = Map.of("error", "Internal server error");
            JSONUtil.sendJson(exchange, 500, response);
        }
    }

    public void handleLogin(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                Map<String, Object> request = JSONUtil.fromJson(exchange.getRequestBody(), Map.class);
                String username = (String) request.get("username");
                String password = (String) request.get("password");

                if (username.isEmpty() || password.isEmpty()) {
                    JSONUtil.sendJson(exchange, 400, Map.of("error", "Missing username or password"));
                    return;
                }

                String token = userService.loginUser(username, password);

                if (token != null) {
                    JSONUtil.sendJson(exchange, 200, Map.of("token", token));
                } else {
                    JSONUtil.sendJson(exchange, 401, Map.of("error", "Invalid username or password"));
                }

            } catch (Exception e) {
                e.printStackTrace();
                JSONUtil.sendJson(exchange, 500, Map.of("error", "Internal server error"));
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    public void handleGetProfile(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            AuthContext ctx = authorizeAndExtract(exchange);
            if (ctx == null) return;

            if (!ctx.requestedUsername().equals(ctx.authUser().getUsername())) {
                JSONUtil.sendJson(exchange, 403, Map.of("error", "Forbidden: cannot access another user’s profile"));
                return;
            }

            User user = userService.getUserByUsername(ctx.requestedUsername());
            if (user != null) {
                JSONUtil.sendJson(exchange, 200, Map.of(
                        "username", user.getUsername(),
                        "email", user.getEmail() == null ? "" : user.getEmail(),
                        "favoriteGenre", user.getFavoriteGenre() == null ? "" : user.getFavoriteGenre(),
                        "createdAt", user.getCreatedAt().toString(),
                        "totalFavorites", user.getTotalFavorites(),
                        "totalRatings", user.getTotalRatings(),
                        "averageRating", user.getAverageRating()
                ));
            } else {
                JSONUtil.sendJson(exchange, 404, Map.of("error", "User not found"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            JSONUtil.sendJson(exchange, 500, Map.of("error", "Internal server error"));
        }
    }

    public void handleUpdateProfile(HttpExchange exchange) throws IOException {
        if (!"PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            AuthContext ctx = authorizeAndExtract(exchange);
            if (ctx == null) return;

            if (!ctx.requestedUsername().equals(ctx.authUser().getUsername())) {
                JSONUtil.sendJson(exchange, 403, Map.of("error", "Forbidden: cannot edit another user’s profile"));
                return;
            }

            Map<String, Object> body = JSONUtil.fromJson(exchange.getRequestBody(), Map.class);
            String email = (String) body.get("email");
            String favoriteGenre = (String) body.get("favoriteGenre");

            boolean updated = userService.updateUserProfile(ctx.authUser().getId(), email, favoriteGenre);

            if (updated) {
                JSONUtil.sendJson(exchange, 200, Map.of("message", "Profile updated successfully"));
            } else {
                JSONUtil.sendJson(exchange, 400, Map.of("error", "Failed to update profile"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            JSONUtil.sendJson(exchange, 500, Map.of("error", "Internal server error"));
        }
    }

    private AuthContext authorizeAndExtract(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            JSONUtil.sendJson(exchange, 401, Map.of("error", "Missing or invalid Authorization header"));
            return null;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        User authUser = userService.getUserByToken(token);
        if (authUser == null) {
            JSONUtil.sendJson(exchange, 401, Map.of("error", "Invalid or expired token"));
            return null;
        }

        URI uri = exchange.getRequestURI();
        String[] pathParts = uri.getPath().split("/");
        if (pathParts.length < 4) {
            JSONUtil.sendJson(exchange, 400, Map.of("error", "Invalid request path"));
            return null;
        }

        String requestedUsername = pathParts[pathParts.length - 2];
        return new AuthContext(authUser, requestedUsername);
    }


}
