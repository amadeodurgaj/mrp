package org.mrp.service;

import org.mrp.exception.*;
import org.mrp.model.Rating;
import org.mrp.repository.RatingRepository;

import java.util.List;
import java.util.UUID;

public class RatingService {

    private final RatingRepository repository;

    public RatingService(RatingRepository repository) {
        this.repository = repository;
    }

    public Rating createRating(int mediaId, UUID userId, Rating rating) throws ApiException {

        if (repository.existsByMediaAndUser(mediaId, userId)) {
            throw new BadRequestException("User has already rated this media");
        }

        rating.setMediaId(mediaId);
        rating.setUserId(userId);
        rating.setConfirmed(false);

        return repository.insert(rating);
    }

    public List<Rating> getRatingHistory(String username) throws ApiException {
        try {
            return repository.findRatingByUserId(username);
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }


    public void updateRating(int ratingId, UUID userId, Rating updated) throws ApiException {
        Rating existing = repository.findById(ratingId).orElseThrow(() -> new BadRequestException("Rating not found"));

        if (!existing.getUserId().equals(userId)) {
            throw new ForbiddenAccessException();
        }

        repository.update(ratingId, updated);
    }

    public void deleteRating(int ratingId, UUID userId) throws ApiException{
        Rating existing = repository.findById(ratingId).orElseThrow(() -> new BadRequestException("Rating not found"));

        if (!existing.getUserId().equals(userId)) {
            throw new ForbiddenAccessException();
        }

        repository.delete(ratingId);
    }

    public void confirmRating(int ratingId, UUID userId) throws ApiException {
        Rating existing = repository.findById(ratingId).orElseThrow(() -> new BadRequestException("Rating not found"));

        if (!existing.getUserId().equals(userId)) {
            throw new ForbiddenAccessException();
        }

        repository.confirm(ratingId);
    }

    public void likeRating(int ratingId, UUID userId) throws ApiException {

        if (repository.hasUserLiked(ratingId, userId)) {
            throw new ForbiddenAccessException();
        }

        repository.like(ratingId, userId);
    }
}
