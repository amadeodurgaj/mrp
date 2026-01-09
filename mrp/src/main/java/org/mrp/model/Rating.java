package org.mrp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Rating {

    private int id;
    private int mediaId;
    private UUID userId;
    private int stars; // 1–5
    private String comment;
    private boolean confirmed;
    private LocalDateTime createdAt;
    private List<UUID> likedByUsers = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Stars must be between 1 and 5");
        }
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<UUID> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(List<UUID> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    // ===== Convenience Methods =====

    public int getLikeCount() {
        return likedByUsers.size();
    }

    public void like(UUID userId) {
        if (!likedByUsers.contains(userId)) {
            likedByUsers.add(userId);
        }
    }
}
