package org.mrp.service;

import org.mrp.exception.*;
import org.mrp.model.User;
import org.mrp.util.DBUtil;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class UserService {

    private final DBUtil dbUtil = new DBUtil();
    private Map<String, UUID> activeTokens = new ConcurrentHashMap<>();

    public UUID registerUser(String username, String password) throws ApiException {
        String checkSql = "SELECT id FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, password_hash, created_at) VALUES (?, ?, ?) RETURNING id";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                throw new UserAlreadyExistsException(username);
            }

            insertStmt.setString(1, username);
            insertStmt.setString(2, hashPassword(password));
            insertStmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            ResultSet inserted = insertStmt.executeQuery();
            if (inserted.next()) {
                return (UUID) inserted.getObject("id");
            }

            throw new InternalServerException(new RuntimeException("Failed to insert user"));

        } catch (UserAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    public String loginUser(String username, String password) throws ApiException {
        String sql = "SELECT id, password_hash FROM users WHERE username = ?";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new InvalidCredentialsException();
            }

            String storedHash = rs.getString("password_hash");
            if (!verifyPassword(hashPassword(password), storedHash)) {
                throw new InvalidCredentialsException();
            }

            UUID userId = (UUID) rs.getObject("id");
            String token = generateToken(userId, username);

            activeTokens.put(token, userId);

            return token;

        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    private String generateToken(UUID userId, String username) {
        return userId.toString() + ":" + username + "-mrpToken";
    }


    public User getUserByToken(String token) throws ApiException {
        UUID userId = activeTokens.get(token);
        if (userId == null) return null;

        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (Exception e) {
            throw new InternalServerException(e);
        }
        return null;
    }

    public User getUserByUsername(String username) throws ApiException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new UserNotFoundException(username);
            }

            return mapResultSetToUser(rs);

        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }



    public boolean updateUserProfile(UUID userId, String email, String favoriteGenre) throws ApiException {
        String sql = "UPDATE users SET email = ?, favorite_genre = ? WHERE id = ?";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, favoriteGenre);
            stmt.setObject(3, userId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }



    private String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    private User mapResultSetToUser(ResultSet rs) throws Exception {
        User user = new User();
        user.setId((UUID)rs.getObject("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setFavoriteGenre(rs.getString("favorite_genre"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }

    private boolean verifyPassword(String password, String storedHash) {
        return password.equals(storedHash);
    }

}
