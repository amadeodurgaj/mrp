package org.mrp.service;

import org.mrp.exception.*;
import org.mrp.model.User;
import org.mrp.repository.*;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class UserService {

    private final UserRepository userRepository;
    private final Map<String, UUID> activeTokens = new ConcurrentHashMap<>();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserService() {
        this(new UserRepositoryImpl());
    }

    public UUID registerUser(String username, String password) throws ApiException {
        try {
            if (userRepository.findByUsername(username).isPresent()) {
                throw new UserAlreadyExistsException(username);
            }

            String hash = hashPassword(password);
            return userRepository.insertUser(username, hash);

        } catch (UserAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    public String loginUser(String username, String password) throws ApiException {
        try {
            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                throw new InvalidCredentialsException();
            }

            User user = userOpt.get();
            String storedHash = userRepository.getPasswordHash(username).orElseThrow(InvalidCredentialsException::new);

            if (!verifyPassword(hashPassword(password), storedHash)) {
                throw new InvalidCredentialsException();
            }

            String token = generateToken(user.getId(), username);
            activeTokens.put(token, user.getId());
            return token;

        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    public User getUserByToken(String token) throws ApiException {
        try {
            UUID userId = activeTokens.get(token);
            if (userId == null) return null;
            return userRepository.findById(userId).orElse(null);
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    public User getUserByUsername(String username) throws ApiException {
        try {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException(username));
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    public boolean updateUserProfile(UUID userId, String email, String favoriteGenre) throws ApiException {
        try {
            return userRepository.updateProfile(userId, email, favoriteGenre);
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    private String generateToken(UUID userId, String username) {
        return userId + ":" + username + "-mrpToken";
    }

    private String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    private boolean verifyPassword(String password, String storedHash) {
        return password.equals(storedHash);
    }

}
