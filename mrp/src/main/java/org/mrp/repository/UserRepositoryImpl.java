package org.mrp.repository;
import org.mrp.model.User;
import org.mrp.util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryImpl implements UserRepository {


    private final DBUtil dbUtil = new DBUtil();

    @Override
    public Optional<User> findByUsername(String username) throws Exception {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findById(UUID id) throws Exception {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            return Optional.empty();
        }
    }

    @Override
    public UUID insertUser(String username, String passwordHash) throws Exception {
        String sql = "INSERT INTO users (username, password_hash, created_at) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return (UUID) rs.getObject("id");
            } else {
                throw new SQLException("User insert failed — no ID returned.");
            }
        }
    }

    @Override
    public Optional<String> getPasswordHash(String username) throws Exception {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getString("password_hash"));
            }
            return Optional.empty();
        }
    }

    @Override
    public boolean updateProfile(UUID userId, String email, String favoriteGenre) throws Exception {
        String sql = "UPDATE users SET email = ?, favorite_genre = ? WHERE id = ?";
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, favoriteGenre);
            stmt.setObject(3, userId);

            return stmt.executeUpdate() > 0;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws Exception {
        User user = new User();
        user.setId((UUID) rs.getObject("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setFavoriteGenre(rs.getString("favorite_genre"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }

}
