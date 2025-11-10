package org.mrp.repository;

import org.mrp.model.Media;

import java.util.List;
import java.util.Optional;

public interface MediaRepository {

    Media insert(Media media) throws Exception;

    Optional<Media> findById(int id) throws Exception;

    List<Media> findAll() throws Exception;

    boolean update(int id, Media media) throws Exception;

    boolean delete(int id) throws Exception;
}

