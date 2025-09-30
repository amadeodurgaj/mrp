package org.mrp;


import com.sun.net.httpserver.HttpServer;
import org.mrp.controller.UserController;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/api/users/register", new UserController()::handleRegister);
            server.createContext("/api/users/login", new UserController()::handleLogin);


            server.setExecutor(null);
            server.start();

            System.out.println("Server running on http://localhost:8080");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}