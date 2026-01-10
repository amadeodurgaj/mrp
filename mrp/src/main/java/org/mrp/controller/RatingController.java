package org.mrp.controller;

import com.sun.net.httpserver.HttpExchange;
import org.mrp.model.Rating;
import org.mrp.service.RatingService;
import org.mrp.util.JSONUtil;
import org.mrp.util.HttpMethodValidatorUtil;
import org.mrp.exception.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class RatingController {

    private final RatingService ratingService;
    private final JSONUtil jsonUtil;
    private final HttpMethodValidatorUtil validatorUtil;

    public RatingController(
            RatingService ratingService,
            JSONUtil jsonUtil,
            HttpMethodValidatorUtil validatorUtil
    ) {
        this.ratingService = ratingService;
        this.jsonUtil = jsonUtil;
        this.validatorUtil = validatorUtil;
    }

    public void handleCreateRatingForMedia(HttpExchange exchange, int mediaId, UUID userId) throws IOException {

        if (validatorUtil.require(exchange, "POST")) return;

        Rating rating = jsonUtil.fromJson(exchange.getRequestBody(), Rating.class);

        Rating created = null;
        try {
            created = ratingService.createRating(
                    mediaId,
                    userId,
                    rating
            );
            jsonUtil.sendJson(exchange, 201, Map.of(
                    "message", "Rating created",
                    "ratingId", created.getId()
            ));
        } catch (ApiException e) {
            jsonUtil.sendJson(exchange, e.getStatusCode(), Map.of("error", e.getMessage()));
        }

    }

    public void handleUpdateRating(HttpExchange exchange, int ratingId, UUID userId) throws IOException {
        if (validatorUtil.require(exchange, "PUT")) return;

        try {
            Rating updated = jsonUtil.fromJson(exchange.getRequestBody(), Rating.class);
            ratingService.updateRating(ratingId, userId, updated);

            jsonUtil.sendJson(exchange, 200, Map.of("message", "Rating updated"));
        } catch (ApiException e) {
            jsonUtil.sendJson(exchange, e.getStatusCode(), Map.of("error", e.getMessage()));
        }
    }

    public void handleDeleteRating(HttpExchange exchange, int ratingId, UUID userId) throws IOException {
        if (validatorUtil.require(exchange, "DELETE")) return;

        try {
            ratingService.deleteRating(ratingId, userId);
            jsonUtil.sendJson(exchange, 200, Map.of("message", "Rating deleted"));
        } catch (ApiException e) {
            jsonUtil.sendJson(exchange, e.getStatusCode(), Map.of("error", e.getMessage()));
        }
    }

    public void handleConfirmRating(HttpExchange exchange, int ratingId, UUID userId) throws IOException {
        if (validatorUtil.require(exchange, "POST")) return;

        try {
            ratingService.confirmRating(ratingId, userId);
            jsonUtil.sendJson(exchange, 200, Map.of("message", "Comment confirmed"));
        } catch (ApiException e) {
            jsonUtil.sendJson(exchange, e.getStatusCode(), Map.of("error", e.getMessage()));
        }
    }

    public void handleLikeRating(HttpExchange exchange, int ratingId, UUID userId) throws IOException {
        if (validatorUtil.require(exchange, "POST")) return;

        try {
            ratingService.likeRating(ratingId, userId);
            jsonUtil.sendJson(exchange, 200, Map.of("message", "Rating liked"));
        } catch (ApiException e) {
            jsonUtil.sendJson(exchange, e.getStatusCode(), Map.of("error", e.getMessage()));
        }
    }
}
