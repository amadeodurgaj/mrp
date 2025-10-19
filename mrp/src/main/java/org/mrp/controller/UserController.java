package org.mrp.controller;
import org.mrp.exception.ApiException;
import org.mrp.exception.BadRequestException;
import org.mrp.exception.ForbiddenAccessException;
import org.mrp.model.User;
import org.mrp.service.UserService;
import org.mrp.util.HttpMethodValidatorUtil;
import org.mrp.util.JSONUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

public class UserController {
    private final UserService userService = new UserService();

    private final JSONUtil jsonUtil = new JSONUtil();
    private final HttpMethodValidatorUtil validatorUtil = new HttpMethodValidatorUtil();

    private record AuthContext(User authUser, String requestedUsername) {}

    public void handleRegister(HttpExchange exchange) throws IOException {
        if (validatorUtil.require(exchange, "POST")) return;

        Map<String, String> request = jsonUtil.fromJson(exchange.getRequestBody(), Map.class);
        String username = request.get("username");
        String password = request.get("password");

        try {
            UUID userId = userService.registerUser(username, password);
            jsonUtil.sendJson(exchange, 201, Map.of(
                    "message", "User '" + username + "' registered successfully",
                    "userId", userId.toString()
            ));
        } catch (ApiException e) {
            jsonUtil.sendJson(exchange, e.getStatusCode(), Map.of("error", e.getMessage()));
        }
    }


    public void handleLogin(HttpExchange exchange) throws IOException {
        if (validatorUtil.require(exchange, "POST")) return;

        try {
            Map<String, Object> request = jsonUtil.fromJson(exchange.getRequestBody(), Map.class);
            String username = (String) request.get("username");
            String password = (String) request.get("password");

            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                throw new BadRequestException("Missing username or password");
            }

            String token = userService.loginUser(username, password);
            jsonUtil.sendJson(exchange, 200, Map.of("token", token));

        } catch (ApiException e) {
            jsonUtil.sendJson(exchange, e.getStatusCode(), Map.of("error", e.getMessage()));
        }
    }


    public void handleGetProfile(HttpExchange exchange) throws IOException {
        if (validatorUtil.require(exchange, "GET")) return;

        try {
            AuthContext ctx = authorizeAndExtract(exchange);
            if (ctx == null) return;

            if (!ctx.requestedUsername().equals(ctx.authUser().getUsername())) {
                throw new ForbiddenAccessException();
            }

            User user = userService.getUserByUsername(ctx.requestedUsername());

            jsonUtil.sendJson(exchange, 200, Map.of(
                    "username", user.getUsername(),
                    "email", user.getEmail() == null ? "" : user.getEmail(),
                    "favoriteGenre", user.getFavoriteGenre() == null ? "" : user.getFavoriteGenre(),
                    "createdAt", user.getCreatedAt().toString(),
                    "totalFavorites", user.getTotalFavorites(),
                    "totalRatings", user.getTotalRatings(),
                    "averageRating", user.getAverageRating()
            ));

        } catch (ApiException e) {
            jsonUtil.sendJson(exchange, e.getStatusCode(), Map.of("error", e.getMessage()));
        }
    }


    public void handleUpdateProfile(HttpExchange exchange) throws IOException {
        if (validatorUtil.require(exchange, "PUT")) return;

        try {
            AuthContext ctx = authorizeAndExtract(exchange);
            if (ctx == null) return;

            if (!ctx.requestedUsername().equals(ctx.authUser().getUsername())) {
                throw new ForbiddenAccessException();
            }

            Map<String, Object> body = jsonUtil.fromJson(exchange.getRequestBody(), Map.class);
            String email = (String) body.get("email");
            String favoriteGenre = (String) body.get("favoriteGenre");

            boolean updated = userService.updateUserProfile(ctx.authUser().getId(), email, favoriteGenre);

            if (updated) {
                jsonUtil.sendJson(exchange, 200, Map.of("message", "Profile updated successfully"));
            } else {
                throw new BadRequestException("Failed to update profile");
            }

        } catch (ApiException e) {
            jsonUtil.sendJson(exchange, e.getStatusCode(), Map.of("error", e.getMessage()));
        }
    }


    private AuthContext authorizeAndExtract(HttpExchange exchange) throws IOException, ApiException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            jsonUtil.sendJson(exchange, 401, Map.of("error", "Missing or invalid Authorization header"));
            return null;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        User authUser = userService.getUserByToken(token);
        if (authUser == null) {
            jsonUtil.sendJson(exchange, 401, Map.of("error", "Invalid or expired token"));
            return null;
        }

        URI uri = exchange.getRequestURI();
        String[] pathParts = uri.getPath().split("/");
        if (pathParts.length < 4) {
            jsonUtil.sendJson(exchange, 400, Map.of("error", "Invalid request path"));
            return null;
        }

        String requestedUsername = pathParts[pathParts.length - 2];
        return new AuthContext(authUser, requestedUsername);
    }


}
