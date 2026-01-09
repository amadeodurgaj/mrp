package org.mrp.router;

import com.sun.net.httpserver.HttpServer;
import org.mrp.controller.UserController;
import org.mrp.exception.ApiException;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            if (path.matches("^/api/users/[^/]+/ratings$")) {

                try {
                    userController.handleGetRatingHistory(exchange);
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
                return;
            }

            if (path.matches("^/api/users/[^/]+/favorites$")) {
                try {
                    userController.handleGetFavorites(exchange);
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
                return;
            }

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
