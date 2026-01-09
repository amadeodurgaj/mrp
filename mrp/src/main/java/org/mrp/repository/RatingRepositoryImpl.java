package org.mrp.repository;

import org.mrp.model.Rating;
import org.mrp.util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class RatingRepositoryImpl implements RatingRepository {

    private final DBUtil dbUtil = new DBUtil();

    @Override
    public Rating insert(Rating rating) {
        String sql = """
            INSERT INTO ratings (media_id, user_id, stars, comment, confirmed, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
        """;

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, rating.getMediaId());
            stmt.setObject(2, rating.getUserId());
            stmt.setInt(3, rating.getStars());
            stmt.setString(4, rating.getComment());
            stmt.setBoolean(5, rating.isConfirmed());
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rating.setId(rs.getInt("id"));
            }
            return rating;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert rating", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Rating> findById(int ratingId) {
        String sql = "SELECT * FROM ratings WHERE id = ?";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ratingId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                return Optional.empty();
            }

            Rating rating = mapRating(rs);
            rating.setLikedByUsers(loadLikes(conn, ratingId));
            return Optional.of(rating);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch rating by id", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Rating> findByMediaId(int mediaId) {
        String sql = "SELECT * FROM ratings WHERE media_id = ?";

        List<Rating> ratings = new ArrayList<>();

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mediaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Rating rating = mapRating(rs);
                rating.setLikedByUsers(loadLikes(conn, rating.getId()));
                ratings.add(rating);
            }

            return ratings;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch ratings by mediaId", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(int ratingId, Rating rating) {
        String sql = """
            UPDATE ratings
            SET stars = ?, comment = ?, confirmed = false
            WHERE id = ?
        """;

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, rating.getStars());
            stmt.setString(2, rating.getComment());
            stmt.setInt(3, ratingId);

            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update rating", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(int ratingId) {
        String sql = "DELETE FROM ratings WHERE id = ?";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ratingId);
            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete rating", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasUserLiked(int ratingId, UUID userId) {
        String sql = """
            SELECT 1
            FROM rating_likes
            WHERE rating_id = ? AND user_id = ?
        """;

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ratingId);
            stmt.setObject(2, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to check rating like", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void like(int ratingId, UUID userId) {
        String sql = """
            INSERT INTO rating_likes (rating_id, user_id)
            VALUES (?, ?)
        """;

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ratingId);
            stmt.setObject(2, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to like rating", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void confirm(int ratingId) {
        String sql = "UPDATE ratings SET confirmed = true WHERE id = ?";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ratingId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to confirm rating", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean existsByMediaAndUser(int mediaId, UUID userId) {
        String sql = """
        SELECT 1
        FROM ratings
        WHERE media_id = ? AND user_id = ?
    """;

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mediaId);
            stmt.setObject(2, userId);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to check existing rating", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Rating> findRatingByUserId(String username) {
        String sql = """
            SELECT r.* FROM ratings r
            JOIN users u on u.id = r.user_id
            WHERE u.username = ?
            ORDER BY r.created_at DESC;
    """;

        List<Rating> ratings = new ArrayList<>();

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ratings.add(mapRating(rs));
            }

            return ratings;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load rating history", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Rating mapRating(ResultSet rs) throws SQLException {
        Rating rating = new Rating();
        rating.setId(rs.getInt("id"));
        rating.setMediaId(rs.getInt("media_id"));
        rating.setUserId((UUID) rs.getObject("user_id"));
        rating.setStars(rs.getInt("stars"));
        rating.setComment(rs.getString("comment"));
        rating.setConfirmed(rs.getBoolean("confirmed"));
        rating.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return rating;
    }

    private List<UUID> loadLikes(Connection conn, int ratingId) throws SQLException {
        String sql = "SELECT user_id FROM rating_likes WHERE rating_id = ?";
        List<UUID> likes = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ratingId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                likes.add((UUID) rs.getObject("user_id"));
            }
        }
        return likes;
    }

}
