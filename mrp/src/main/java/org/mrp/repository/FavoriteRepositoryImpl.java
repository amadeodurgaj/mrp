package org.mrp.repository;

import org.mrp.model.Media;
import org.mrp.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FavoriteRepositoryImpl implements FavoriteRepository {

    private final DBUtil dbUtil = new DBUtil();

    @Override
    public boolean favoriteExists(UUID userId, int mediaId) {
        String sql = """
            SELECT 1 FROM favorites
            WHERE user_id = ? AND media_id = ?
        """;

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            stmt.setInt(2, mediaId);

            return stmt.executeQuery().next();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addFavorite(UUID userId, int mediaId) {
        String sql = """
            INSERT INTO favorites (user_id, media_id)
            VALUES (?, ?)
        """;

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            stmt.setInt(2, mediaId);
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeFavorite(UUID userId, int mediaId) {
        String sql = """
            DELETE FROM favorites
            WHERE user_id = ? AND media_id = ?
        """;

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            stmt.setInt(2, mediaId);
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Media> findFavoritesByUser(String username) {
        String sql = """
        SELECT m.*
        FROM favorites f
        JOIN media m ON m.id = f.media_id
        JOIN users u ON u.id = f.user_id
        WHERE u.username = ?
        ORDER BY f.added_at DESC
    """;

        List<Media> favorites = new ArrayList<>();

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                favorites.add(mapResultSetToMedia(rs));
            }

            return favorites;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load favorites", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Media mapResultSetToMedia(ResultSet rs) throws SQLException {
        Media m = new Media();
        m.setId(rs.getInt("id"));
        m.setTitle(rs.getString("title"));
        m.setDescription(rs.getString("description"));
        m.setMediaType(rs.getString("media_type"));
        m.setReleaseYear(rs.getInt("release_year"));
        m.setAgeRestriction(rs.getInt("age_restriction"));
        m.setCreatorId((UUID) rs.getObject("creator_id"));
        m.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return m;
    }

}
