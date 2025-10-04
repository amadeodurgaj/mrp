package org.mrp.model;

import java.time.LocalDateTime;

public class User {

    private int id;
    private String username;
    private String email;
    private String favoriteGenre;
    private LocalDateTime createdAt;

    private int totalFavorites;
    private int totalRatings;
    private double averageRating;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFavoriteGenre() { return favoriteGenre; }
    public void setFavoriteGenre(String favoriteGenre) { this.favoriteGenre = favoriteGenre; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getTotalFavorites() { return totalFavorites; }
    public void setTotalFavorites(int totalFavorites) { this.totalFavorites = totalFavorites; }

    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
}
