package org.mrp;


import com.sun.net.httpserver.HttpServer;
import org.mrp.router.RouteRegister;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) {
        try {

            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            RouteRegister router = new RouteRegister();
            router.registerRoutes(server);

            server.setExecutor(null);
            server.start();

            System.out.println("Server running on http://localhost:8080");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}