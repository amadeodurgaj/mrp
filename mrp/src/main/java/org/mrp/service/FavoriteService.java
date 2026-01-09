package org.mrp.service;

import org.mrp.exception.ApiException;
import org.mrp.exception.BadRequestException;
import org.mrp.exception.InternalServerException;
import org.mrp.model.Media;
import org.mrp.repository.FavoriteRepository;
import org.mrp.repository.MediaRepository;

import java.util.List;
import java.util.UUID;

public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MediaRepository mediaRepository;

    public FavoriteService(
            FavoriteRepository favoriteRepository,
            MediaRepository mediaRepository
    ) {
        this.favoriteRepository = favoriteRepository;
        this.mediaRepository = mediaRepository;
    }

    public void favorite(UUID userId, int mediaId) throws ApiException {

        try {
            mediaRepository.findById(mediaId).orElseThrow(() -> new BadRequestException("Media not found"));

            if (favoriteRepository.favoriteExists(userId, mediaId)) {
                throw new BadRequestException("Media already favorited");
            }

            favoriteRepository.addFavorite(userId, mediaId);

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    public void unfavorite(UUID userId, int mediaId) throws ApiException {

        if (!favoriteRepository.favoriteExists(userId, mediaId)) {
            throw new BadRequestException("Media not favorited");
        }

        favoriteRepository.removeFavorite(userId, mediaId);
    }

    public List<Media> getFavorites(String username) throws ApiException {
        try {
            return favoriteRepository.findFavoritesByUser(username);
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

}
