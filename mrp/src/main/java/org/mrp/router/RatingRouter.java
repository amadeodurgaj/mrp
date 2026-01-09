package org.mrp.router;

import com.sun.net.httpserver.HttpServer;
import org.mrp.controller.RatingController;
import org.mrp.model.User;
import org.mrp.util.AuthUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RatingRouter {

    private final RatingController ratingController;
    private final AuthUtil authUtil;

    public RatingRouter(RatingController ratingController, AuthUtil authUtil) {
        this.ratingController = ratingController;
        this.authUtil = authUtil;
    }

    public void register(HttpServer server) {

        server.createContext("/api/ratings", exchange -> {
            String rawPath = exchange.getRequestURI().getPath();
            String path = rawPath.trim();
            String method = exchange.getRequestMethod();

            try {
                User user = authUtil.requireUser(exchange);

                // POST /api/ratings/media/{mediaId}
                Matcher createMatcher = Pattern
                        .compile("^/api/ratings/media/(\\d+)/?$")
                        .matcher(path);

                if (createMatcher.matches()) {
                    if (!"POST".equalsIgnoreCase(method)) {
                        exchange.sendResponseHeaders(405, -1);
                        return;
                    }

                    int mediaId = Integer.parseInt(createMatcher.group(1));
                    ratingController.handleCreateRatingForMedia(exchange, mediaId, user.getId()
                    );
                    return;
                }

                // /api/ratings/{id}
                Matcher ratingById = Pattern.compile("^/api/ratings/(\\d+)/?$").matcher(path);

                if (ratingById.matches()) {
                    int ratingId = Integer.parseInt(ratingById.group(1));

                    switch (method.toUpperCase()) {
                        case "PUT" ->
                                ratingController.handleUpdateRating(exchange, ratingId, user.getId());
                        case "DELETE" ->
                                ratingController.handleDeleteRating(exchange, ratingId, user.getId());
                        default ->
                                exchange.sendResponseHeaders(405, -1);
                    }
                    return;
                }

                // /api/ratings/{id}/confirm
                if (path.matches("^/api/ratings/\\d+/confirm/?$")) {
                    int ratingId = Integer.parseInt(path.split("/")[3]);
                    ratingController.handleConfirmRating(exchange, ratingId, user.getId());
                    return;
                }

                // /api/ratings/{id}/like
                if (path.matches("^/api/ratings/\\d+/like/?$")) {
                    int ratingId = Integer.parseInt(path.split("/")[3]);
                    ratingController.handleLikeRating(exchange, ratingId, user.getId());
                    return;
                }

                exchange.sendResponseHeaders(404, -1);

            } catch (Exception e) {
                exchange.sendResponseHeaders(400, -1);
            }
        });
    }

}

