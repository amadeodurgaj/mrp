package org.mrp.service;

import org.mrp.exception.*;
import org.mrp.model.Rating;
import org.mrp.repository.RatingRepository;

import java.util.List;
import java.util.UUID;

public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Rating createRating(int mediaId, UUID userId, Rating rating) throws ApiException {

        if (ratingRepository.existsByMediaAndUser(mediaId, userId)) {
            throw new BadRequestException("User has already rated this media");
        }

        rating.setMediaId(mediaId);
        rating.setUserId(userId);
        rating.setConfirmed(false);

        return ratingRepository.insert(rating);
    }

    public List<Rating> getRatingHistory(String username) throws ApiException {
        try {
            return ratingRepository.findRatingByUserId(username);
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }


    public void updateRating(int ratingId, UUID userId, Rating updated) throws ApiException {
        Rating existing = ratingRepository.findById(ratingId).orElseThrow(() -> new BadRequestException("Rating not found"));

        if (!existing.getUserId().equals(userId)) {
            throw new ForbiddenAccessException();
        }

        ratingRepository.update(ratingId, updated);
    }

    public void deleteRating(int ratingId, UUID userId) throws ApiException{
        Rating existing = ratingRepository.findById(ratingId).orElseThrow(() -> new BadRequestException("Rating not found"));

        if (!existing.getUserId().equals(userId)) {
            throw new ForbiddenAccessException();
        }

        ratingRepository.delete(ratingId);
    }

    public void confirmRating(int ratingId, UUID userId) throws ApiException {
        Rating existing = ratingRepository.findById(ratingId).orElseThrow(() -> new BadRequestException("Rating not found"));

        if (!existing.getUserId().equals(userId)) {
            throw new ForbiddenAccessException();
        }

        ratingRepository.confirm(ratingId);
    }

    public void likeRating(int ratingId, UUID userId) throws ApiException {

        if (ratingRepository.hasUserLiked(ratingId, userId)) {
            throw new ForbiddenAccessException();
        }

        ratingRepository.like(ratingId, userId);
    }
}
