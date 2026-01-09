package org.mrp.router;

import com.sun.net.httpserver.HttpServer;
import org.mrp.controller.MediaController;
import org.mrp.exception.ApiException;

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

            Matcher favoriteMatcher = Pattern.compile("^/api/media/(\\d+)/favorite$").matcher(path);

            if (favoriteMatcher.matches()) {
                int mediaId = Integer.parseInt(favoriteMatcher.group(1));

                switch (method.toUpperCase()) {
                    case "POST" -> {
                        try {
                            mediaController.handleFavoriteMedia(exchange, mediaId);
                        } catch (ApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    case "DELETE" -> {
                        try {
                            mediaController.handleUnfavoriteMedia(exchange, mediaId);
                        } catch (ApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    default -> exchange.sendResponseHeaders(405, -1);
                }
                return;
            }

            if (matcher.matches()) {
                int id = Integer.parseInt(matcher.group(1));
                switch (method.toUpperCase()) {
                    case "GET" -> {
                        try {
                            mediaController.handleGetMediaById(exchange, id);
                        } catch (ApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    case "PUT" -> {
                        try {
                            mediaController.handleUpdateMedia(exchange, id);
                        } catch (ApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    case "DELETE" -> {
                        try {
                            mediaController.handleDeleteMedia(exchange, id);
                        } catch (ApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    default -> exchange.sendResponseHeaders(405, -1);
                }
                return;
            }

            switch (method.toUpperCase()) {
                case "GET" -> {
                    try {
                        mediaController.handleGetAllMedia(exchange);
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                }
                case "POST" -> {
                    try {
                        mediaController.handleCreateMedia(exchange);
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                }
                default -> exchange.sendResponseHeaders(405, -1);
            }
        });
    }

}
