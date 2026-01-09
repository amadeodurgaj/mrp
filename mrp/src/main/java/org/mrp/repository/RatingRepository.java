package org.mrp.repository;

import org.mrp.model.Rating;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RatingRepository {

    Rating insert(Rating rating);

    Optional<Rating> findById(int ratingId);

    List<Rating> findByMediaId(int mediaId);

    boolean update(int ratingId, Rating rating);

    boolean delete(int ratingId);

    boolean hasUserLiked(int ratingId, UUID userId);

    void like(int ratingId, UUID userId);

    void confirm(int ratingId);

    boolean existsByMediaAndUser(int mediaId, UUID userId);

    List<Rating> findRatingByUserId(String username);


}
