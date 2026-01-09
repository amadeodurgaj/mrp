package org.mrp.controller;

import com.sun.net.httpserver.HttpExchange;
import org.mrp.model.Media;
import org.mrp.model.User;
import org.mrp.service.FavoriteService;
import org.mrp.service.MediaService;
import org.mrp.util.AuthUtil;
import org.mrp.util.HttpMethodValidatorUtil;
import org.mrp.util.JSONUtil;
import org.mrp.exception.*;

import java.io.IOException;
import java.util.*;

public class MediaController {

    private final MediaService mediaService;
    private final FavoriteService favoriteService;
    private final AuthUtil authUtil;
    private final JSONUtil jsonUtil;
    private final HttpMethodValidatorUtil validatorUtil;

    public MediaController(MediaService mediaService, FavoriteService favoriteService, AuthUtil authUtil ,JSONUtil jsonUtil, HttpMethodValidatorUtil validatorUtil) {
        this.mediaService = mediaService;
        this.favoriteService = favoriteService;
        this.jsonUtil = jsonUtil;
        this.authUtil = authUtil;
        this.validatorUtil = validatorUtil;
    }

    public void handleCreateMedia(HttpExchange exchange) throws IOException, ApiException {
        if (validatorUtil.require(exchange, "POST")) return;

        if (authUtil.requireUser(exchange) == null) return;

        try {
            Media media = jsonUtil.fromJson(exchange.getRequestBody(), Media.class);
            Media created = mediaService.createMedia(media);
            jsonUtil.sendJson(exchange, 201, Map.of("message", "Media created", "id", created.getId()));
        } catch (ApiException e) {
            jsonUtil.sendJson(exchange, e.getStatusCode(), Map.of("error", e.getMessage()));
        }
    }

    public void handleGetAllMedia(HttpExchange exchange) throws IOException, ApiException {
        if (validatorUtil.require(exchange, "GET")) return;

        if (authUtil.requireUser(exchange) == null) return;


        try {
            List<Media> list = mediaService.getAllMedia();
            jsonUtil.sendJson(exchange, 200, list);
        } catch (ApiException e) {
            jsonUtil.sendJson(exchange, e.getStatusCode(), Map.of("error", e.getMessage()));
        }
    }

    public void handleGetMediaById(HttpExchange exchange, int id) throws IOException, ApiException {
        if (validatorUtil.require(exchange, "GET")) return;

        if (authUtil.requireUser(exchange) == null) return;


        try {
            Media media = mediaService.getMediaById(id);
            jsonUtil.sendJson(exchange, 200, media);
        } catch (ApiException e) {
            jsonUtil.sendJson(exchange, e.getStatusCode(), Map.of("error", e.getMessage()));
        }
    }

    public void handleUpdateMedia(HttpExchange exchange, int id) throws IOException, ApiException {
        if (validatorUtil.require(exchange, "PUT")) return;

        if (authUtil.requireUser(exchange) == null) return;

        try {
            Media media = jsonUtil.fromJson(exchange.getRequestBody(), Media.class);
            boolean updated = mediaService.updateMedia(id, media);
            jsonUtil.sendJson(exchange, updated ? 200 : 404, Map.of(
                    updated ? "message" : "error",
                    updated ? "Media updated successfully" : "Media not found"
            ));
        } catch (ApiException e) {
            jsonUtil.sendJson(exchange, e.getStatusCode(), Map.of("error", e.getMessage()));
        }
    }

    public void handleDeleteMedia(HttpExchange exchange, int id) throws IOException, ApiException {
        if (validatorUtil.require(exchange, "DELETE")) return;

        if (authUtil.requireUser(exchange) == null) return;

        try {
            boolean deleted = mediaService.deleteMedia(id);
            jsonUtil.sendJson(exchange, deleted ? 200 : 404, Map.of(
                    deleted ? "message" : "error",
                    deleted ? "Media deleted" : "Media not found"
            ));
        } catch (ApiException e) {
            jsonUtil.sendJson(exchange, e.getStatusCode(), Map.of("error", e.getMessage()));
        }
    }

    public void handleFavoriteMedia(HttpExchange exchange, int mediaId) throws IOException, ApiException {

        User user = authUtil.requireUser(exchange);

        if (validatorUtil.require(exchange, "POST")) return;


        try {
            favoriteService.favorite(user.getId(), mediaId);
            jsonUtil.sendJson(exchange, 200, Map.of(
                    "message", "Media favorited"
            ));
        } catch (ApiException e) {
            jsonUtil.sendJson(exchange, e.getStatusCode(), Map.of("error", e.getMessage()));
        }


    }

    public void handleUnfavoriteMedia(HttpExchange exchange, int mediaId) throws IOException, ApiException {

        User user = authUtil.requireUser(exchange);

        if (validatorUtil.require(exchange, "DELETE")) return;

        try {
            favoriteService.unfavorite(user.getId(), mediaId);
            jsonUtil.sendJson(exchange, 200, Map.of(
                    "message", "Favorite removed!"
            ));
        } catch (ApiException e) {
            jsonUtil.sendJson(exchange, e.getStatusCode(), Map.of("error", e.getMessage()));
        }
    }

}
