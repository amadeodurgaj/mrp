package org.mrp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Media {
    private int id;
    private String title;
    private String description;
    private String mediaType;
    private int releaseYear;
    private int ageRestriction;
    private UUID creatorId;
    private LocalDateTime createdAt;
    private List<String> genres;
    private List<Rating> ratings = new ArrayList<>();
    private Double averageRating;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }

    public int getAgeRestriction() { return ageRestriction; }
    public void setAgeRestriction(int ageRestriction) { this.ageRestriction = ageRestriction; }

    public UUID getCreatorId() { return creatorId; }
    public void setCreatorId(UUID creatorId) { this.creatorId = creatorId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }

    public List<Rating> getRatings() { return ratings; }
    public void setRatings(List<Rating> ratings) { this.ratings = ratings; }

    public double getAverageScore() {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .filter(Rating::isConfirmed)
                .mapToInt(Rating::getStars)
                .average()
                .orElse(0.0);
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }


}
