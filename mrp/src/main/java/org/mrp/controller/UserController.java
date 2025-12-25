package org.mrp.controller;
import org.mrp.exception.ApiException;
import org.mrp.exception.BadRequestException;
import org.mrp.exception.ForbiddenAccessException;
import org.mrp.model.User;
import org.mrp.service.UserService;
import org.mrp.util.AuthUtil;
import org.mrp.util.HttpMethodValidatorUtil;
import org.mrp.util.JSONUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class UserController {
    private final UserService userService;

    private final JSONUtil jsonUtil;
    private final AuthUtil authUtil;
    private final HttpMethodValidatorUtil validatorUtil;

    public UserController(UserService userService, AuthUtil authUtil , JSONUtil jsonUtil, HttpMethodValidatorUtil validatorUtil) {
        this.userService = userService;
        this.jsonUtil = jsonUtil;
        this.authUtil = authUtil;
        this.validatorUtil = validatorUtil;
    }

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
            User authUser = authUtil.requireUser(exchange);
            if (authUser == null) return;

            String requestedUsername = extractUsernameFromPath(exchange);

            if (!requestedUsername.equals(authUser.getUsername())) {
                throw new ForbiddenAccessException();
            }

            User user = userService.getUserByUsername(requestedUsername);


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
            User authUser = authUtil.requireUser(exchange);
            if (authUser == null) return;

            String requestedUsername = extractUsernameFromPath(exchange);

            if (!requestedUsername.equals(authUser.getUsername())) {
                throw new ForbiddenAccessException();
            }

            Map<String, Object> body = jsonUtil.fromJson(exchange.getRequestBody(), Map.class);
            String email = (String) body.get("email");
            String favoriteGenre = (String) body.get("favoriteGenre");

            boolean updated = userService.updateUserProfile(
                    authUser.getId(),
                    email,
                    favoriteGenre
            );

            if (updated) {
                jsonUtil.sendJson(exchange, 200, Map.of(
                        "message", "Profile updated successfully"
                ));
            } else {
                throw new BadRequestException("Failed to update profile");
            }

        } catch (ApiException e) {
            jsonUtil.sendJson(exchange, e.getStatusCode(), Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    private String extractUsernameFromPath(HttpExchange exchange) throws ApiException {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        if (parts.length < 2) {
            throw new BadRequestException("Invalid request path");
        }
        return parts[parts.length - 2];
    }



}
