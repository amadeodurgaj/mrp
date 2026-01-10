package org.mrp.controller;

import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mrp.exception.ApiException;
import org.mrp.exception.ForbiddenAccessException;
import org.mrp.model.Rating;
import org.mrp.service.RatingService;
import org.mrp.util.HttpMethodValidatorUtil;
import org.mrp.util.JSONUtil;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingControllerTest {

    @Mock
    private RatingService ratingService;

    @Mock
    private JSONUtil jsonUtil;

    @Mock
    private HttpMethodValidatorUtil validatorUtil;

    @Mock
    private HttpExchange exchange;

    private RatingController controller;
    private UUID userId;

    @BeforeEach
    void setup() {
        controller = new RatingController(ratingService, jsonUtil, validatorUtil);
        userId = UUID.randomUUID();
    }


    @Test
    void handleCreateRating_success() throws IOException, ApiException {
        Rating rating = new Rating();
        rating.setId(10);

        when(validatorUtil.require(exchange, "POST")).thenReturn(false);
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream("{}".getBytes()));
        when(jsonUtil.fromJson(any(), eq(Rating.class))).thenReturn(rating);
        when(ratingService.createRating(1, userId, rating)).thenReturn(rating);

        controller.handleCreateRatingForMedia(exchange, 1, userId);

        verify(ratingService).createRating(1, userId, rating);
        verify(jsonUtil).sendJson(eq(exchange), eq(201), any());
    }

    @Test
    void handleCreateRating_serviceThrowsApiException() throws IOException, ApiException {
        Rating rating = new Rating();

        when(validatorUtil.require(exchange, "POST")).thenReturn(false);
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream("{}".getBytes()));
        when(jsonUtil.fromJson(any(), eq(Rating.class))).thenReturn(rating);
        when(ratingService.createRating(anyInt(), any(), any())).thenThrow(new ForbiddenAccessException());

        controller.handleCreateRatingForMedia(exchange, 1, userId);

        verify(jsonUtil).sendJson(eq(exchange), eq(403), any());
    }


    @Test
    void handleUpdateRating_success() throws IOException, ApiException {
        Rating updated = new Rating();

        when(validatorUtil.require(exchange, "PUT")).thenReturn(false);
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream("{}".getBytes()));
        when(jsonUtil.fromJson(any(), eq(Rating.class))).thenReturn(updated);

        controller.handleUpdateRating(exchange, 5, userId);

        verify(ratingService).updateRating(5, userId, updated);
        verify(jsonUtil).sendJson(eq(exchange), eq(200), any());
    }

    @Test
    void handleUpdateRating_forbidden() throws IOException, ApiException {
        Rating updated = new Rating();

        when(validatorUtil.require(exchange, "PUT")).thenReturn(false);
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream("{}".getBytes()));
        when(jsonUtil.fromJson(any(), eq(Rating.class))).thenReturn(updated);

        doThrow(new ForbiddenAccessException()).when(ratingService).updateRating(anyInt(), any(), any());

        controller.handleUpdateRating(exchange, 5, userId);

        verify(jsonUtil).sendJson(eq(exchange), eq(403), any());
    }


    @Test
    void handleDeleteRating_success() throws IOException, ApiException {
        when(validatorUtil.require(exchange, "DELETE")).thenReturn(false);

        controller.handleDeleteRating(exchange, 3, userId);

        verify(ratingService).deleteRating(3, userId);
        verify(jsonUtil).sendJson(eq(exchange), eq(200), any());
    }


    @Test
    void handleConfirmRating_success() throws IOException, ApiException {
        when(validatorUtil.require(exchange, "POST")).thenReturn(false);

        controller.handleConfirmRating(exchange, 7, userId);

        verify(ratingService).confirmRating(7, userId);
        verify(jsonUtil).sendJson(eq(exchange), eq(200), any());
    }


    @Test
    void handleLikeRating_success() throws IOException, ApiException {
        when(validatorUtil.require(exchange, "POST")).thenReturn(false);

        controller.handleLikeRating(exchange, 9, userId);

        verify(ratingService).likeRating(9, userId);
        verify(jsonUtil).sendJson(eq(exchange), eq(200), any());
    }
}
