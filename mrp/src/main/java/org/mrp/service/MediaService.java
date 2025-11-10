package org.mrp.service;

import org.mrp.model.Media;
import org.mrp.exception.*;
import org.mrp.repository.MediaRepository;
import org.mrp.repository.MediaRepositoryImpl;
import java.util.*;

public class MediaService {


        private final MediaRepository mediaRepository;

        public MediaService() {
            this(new MediaRepositoryImpl());
        }

        public MediaService(MediaRepository mediaRepository) {
            this.mediaRepository = mediaRepository;
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
                return mediaRepository.findById(id)
                        .orElseThrow(() -> new BadRequestException("Media not found"));
            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new InternalServerException(e);
            }
        }

        public List<Media> getAllMedia() throws ApiException {
            try {
                return mediaRepository.findAll();
            } catch (Exception e) {
                throw new InternalServerException(e);
            }
        }

        public boolean updateMedia(int id, Media media) throws ApiException {
            try {
                return mediaRepository.update(id, media);
            } catch (Exception e) {
                throw new InternalServerException(e);
            }
        }

        public boolean deleteMedia(int id) throws ApiException {
            try {
                return mediaRepository.delete(id);
            } catch (Exception e) {
                throw new InternalServerException(e);
            }
        }
}
