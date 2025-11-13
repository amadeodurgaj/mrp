package org.mrp.router;

import com.sun.net.httpserver.HttpServer;
import org.mrp.controller.UserController;

public class UserRouter {
    private final UserController userController;

    public UserRouter(UserController userController) {
        this.userController = userController;
    }

    public void register(HttpServer server) {
        server.createContext("/api/users/register", userController::handleRegister);
        server.createContext("/api/users/login", userController::handleLogin);
        server.createContext("/api/users", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.matches("^/api/users/[^/]+/profile$")) {
                switch (exchange.getRequestMethod().toUpperCase()) {
                    case "GET" -> userController.handleGetProfile(exchange);
                    case "PUT" -> userController.handleUpdateProfile(exchange);
                    default -> exchange.sendResponseHeaders(405, -1);
                }
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        });
    }
}
