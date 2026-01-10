package org.mrp.service;

import org.mrp.model.Media;
import org.mrp.exception.*;
import org.mrp.repository.MediaRepository;
import org.mrp.repository.RatingRepository;

import java.util.*;

public class MediaService {


        private final MediaRepository mediaRepository;
        private final RatingRepository ratingRepository;

        public MediaService(MediaRepository mediaRepository, RatingRepository ratingRepository) {
            this.mediaRepository = mediaRepository;
            this.ratingRepository = ratingRepository;
        }

        public Media createMedia(Media media) throws ApiException {
            try {
                return mediaRepository.insert(media);
            } catch (Exception e) {
                throw new InternalServerException(e);
            }
        }

        public Media getMediaById(int id) throws ApiException {
            try {

                Media media = mediaRepository.findById(id).orElseThrow(() -> new BadRequestException("Media not found"));

                double avg = ratingRepository.getAverageRatingForMedia(id).orElse(0.0);

                media.setAverageRating(avg);
                return media;

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new InternalServerException(e);
            }
        }

        public List<Media> getAllMedia() throws ApiException {
            try {
                List<Media> mediaList = mediaRepository.findAll();

                for (Media media : mediaList) {
                    double avg = ratingRepository.getAverageRatingForMedia(media.getId()).orElse(0.0);
                    media.setAverageRating(avg);
                }

                return mediaList;

            } catch (Exception e) {
                throw new InternalServerException(e);
            }
        }

        public boolean updateMedia(int id, Media media, UUID requestingUserId) throws ApiException {

            findExistingMedia(id, requestingUserId);

            try {
                return mediaRepository.update(id, media);
            } catch (Exception e) {
                throw new InternalServerException(e);
            }
        }

        public boolean deleteMedia(int id, UUID requestingUserId) throws ApiException {

            findExistingMedia(id, requestingUserId);

            try {
                return mediaRepository.delete(id);
            } catch (Exception e) {
                throw new InternalServerException(e);
            }
        }

        private void findExistingMedia(int id, UUID requestingUserId) throws ApiException {

            Media existing = null;

            try {
                existing = mediaRepository.findById(id).orElseThrow(() -> new BadRequestException("Media not found"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (!existing.getCreatorId().equals(requestingUserId)) {
                throw new ForbiddenAccessExceptionMedia();
            }
        }
}
