package org.mrp.repository;


import org.mrp.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findByUsername(String username) throws Exception;

    Optional<User> findById(UUID id) throws Exception;

    UUID insertUser(String username, String passwordHash) throws Exception;

    Optional<String> getPasswordHash(String username) throws Exception;

    boolean updateProfile(UUID userId, String email, String favoriteGenre) throws Exception;
}


