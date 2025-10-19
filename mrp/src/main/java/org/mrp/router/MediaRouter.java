package org.mrp.router;

import com.sun.net.httpserver.HttpServer;
import org.mrp.controller.MediaController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaRouter {

    private final MediaController mediaController;

    public MediaRouter(MediaController mediaController) {
        this.mediaController = mediaController;
    }

    public void register(HttpServer server) {
        server.createContext("/api/media", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            Matcher matcher = Pattern.compile("^/api/media/(\\d+)$").matcher(path);

            if (matcher.matches()) {
                int id = Integer.parseInt(matcher.group(1));
                switch (method.toUpperCase()) {
                    case "GET" -> mediaController.handleGetMediaById(exchange, id);
                    case "PUT" -> mediaController.handleUpdateMedia(exchange, id);
                    case "DELETE" -> mediaController.handleDeleteMedia(exchange, id);
                    default -> exchange.sendResponseHeaders(405, -1);
                }
                return;
            }

            switch (method.toUpperCase()) {
                case "GET" -> mediaController.handleGetAllMedia(exchange);
                case "POST" -> mediaController.handleCreateMedia(exchange);
                default -> exchange.sendResponseHeaders(405, -1);
            }
        });
    }
}
