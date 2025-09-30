package org.mrp.controller;
import org.mrp.service.UserService;
import org.mrp.util.JSONUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

public class UserController {
    private final UserService userService = new UserService();

    public void handleRegister(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        Map<String, String> request = JSONUtil.fromJson(exchange.getRequestBody(), Map.class);
        String username = request.get("username");
        String password = request.get("password");

        userService.register(username, password);

        Map<String, String> response = Map.of("message", "User " + username + " registered successfully");
        JSONUtil.sendJson(exchange, 201, response);
    }

}
