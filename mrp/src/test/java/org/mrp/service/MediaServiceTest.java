package org.mrp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mrp.exception.*;
import org.mrp.model.Media;
import org.mrp.repository.MediaRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private MediaRepository mediaRepository;

    @InjectMocks
    private MediaService mediaService;

    private UUID creatorId;
    private UUID otherUserId;
    private Media media;

    @BeforeEach
    void setup() {
        creatorId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();

        media = new Media();
        media.setId(1);
        media.setTitle("Test Media");
        media.setCreatorId(creatorId);
    }


    @Test
    void createMedia_success() throws Exception {
        when(mediaRepository.insert(media)).thenReturn(media);

        Media result = mediaService.createMedia(media);

        assertEquals(media, result);
        verify(mediaRepository).insert(media);
    }

    @Test
    void createMedia_repositoryFails_throwsInternalServerException() throws Exception {
        when(mediaRepository.insert(media)).thenThrow(RuntimeException.class);

        assertThrows(InternalServerException.class, () -> mediaService.createMedia(media));
    }


    @Test
    void getMediaById_success() throws Exception {
        when(mediaRepository.findById(1)).thenReturn(Optional.of(media));

        Media result = mediaService.getMediaById(1);

        assertEquals(media, result);
    }

    @Test
    void getMediaById_notFound_throwsBadRequest() throws Exception {
        when(mediaRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> mediaService.getMediaById(1));
    }

    @Test
    void getAllMedia_success() throws Exception {
        when(mediaRepository.findAll()).thenReturn(List.of(media));

        List<Media> result = mediaService.getAllMedia();

        assertEquals(1, result.size());
    }


    @Test
    void updateMedia_creatorCanUpdate() throws Exception {
        when(mediaRepository.findById(1)).thenReturn(Optional.of(media));
        when(mediaRepository.update(eq(1), any(Media.class))).thenReturn(true);

        boolean updated = mediaService.updateMedia(1, media, creatorId);

        assertTrue(updated);
        verify(mediaRepository).update(1, media);
    }

    @Test
    void updateMedia_notCreator_throwsForbidden() throws Exception {
        when(mediaRepository.findById(1)).thenReturn(Optional.of(media));

        assertThrows(ForbiddenAccessExceptionMedia.class, () -> mediaService.updateMedia(1, media, otherUserId));

        verify(mediaRepository, never()).update(anyInt(), any());
    }

    @Test
    void deleteMedia_creatorCanDelete() throws Exception {
        when(mediaRepository.findById(1)).thenReturn(Optional.of(media));
        when(mediaRepository.delete(1)).thenReturn(true);

        boolean deleted = mediaService.deleteMedia(1, creatorId);

        assertTrue(deleted);
        verify(mediaRepository).delete(1);
    }

    @Test
    void deleteMedia_notCreator_throwsForbidden() throws Exception {
        when(mediaRepository.findById(1)).thenReturn(Optional.of(media));

        assertThrows(ForbiddenAccessExceptionMedia.class,
                () -> mediaService.deleteMedia(1, otherUserId));

        verify(mediaRepository, never()).delete(anyInt());
    }

    @Test
    void deleteMedia_repositoryFails_throwsInternalServerException() throws Exception{
        when(mediaRepository.findById(1)).thenReturn(Optional.of(media));
        when(mediaRepository.delete(1)).thenThrow(RuntimeException.class);

        assertThrows(InternalServerException.class, () -> mediaService.deleteMedia(1, creatorId));
    }
}
