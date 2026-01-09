package org.mrp.repository;

import org.mrp.model.Media;

import java.util.List;
import java.util.UUID;

public interface FavoriteRepository {

    boolean favoriteExists(UUID userId, int mediaId);

    void addFavorite(UUID userId, int mediaId);

    void removeFavorite(UUID userId, int mediaId);

    List<Media> findFavoritesByUser(String username);

}
