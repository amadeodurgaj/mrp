package org.mrp;


import com.sun.net.httpserver.HttpServer;
import org.mrp.controller.UserController;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) {
        try {

            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            UserController userController = new UserController();

            server.createContext("/api/users/register", userController::handleRegister);
            server.createContext("/api/users/login", userController::handleLogin);

            server.createContext("/api/users", exchange -> {
                String path = exchange.getRequestURI().getPath();

                if (path.matches("^/api/users/[^/]+/profile$")) {
                    if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                        userController.handleGetProfile(exchange);
                    } else if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
                        userController.handleUpdateProfile(exchange);
                    } else {
                        exchange.sendResponseHeaders(405, -1);
                    }
                } else {
                    exchange.sendResponseHeaders(404, -1);
                }
            });

            server.setExecutor(null);
            server.start();

            System.out.println("Server running on http://localhost:8080");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}