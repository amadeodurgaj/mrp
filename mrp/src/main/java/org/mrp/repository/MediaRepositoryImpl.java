package org.mrp.repository;

import org.mrp.model.Media;
import org.mrp.util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;

public class MediaRepositoryImpl implements MediaRepository {

    private final DBUtil dbUtil = new DBUtil();

    @Override
    public Media insert(Media media) throws Exception {
        String sql = """
            INSERT INTO media (title, description, media_type, release_year, age_restriction, creator_id, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id
        """;

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getDescription());
            stmt.setString(3, media.getMediaType());
            stmt.setInt(4, media.getReleaseYear());
            stmt.setInt(5, media.getAgeRestriction());
            stmt.setObject(6, media.getCreatorId());
            stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                media.setId(rs.getInt("id"));
                return media;
            }

            throw new SQLException("Failed to insert media — no ID returned.");
        }
    }

    @Override
    public Optional<Media> findById(int id) throws Exception {
        String sql = "SELECT * FROM media WHERE id = ?";
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToMedia(rs));
            }
            return Optional.empty();
        }
    }

    @Override
    public List<Media> findAll() throws Exception {
        String sql = "SELECT * FROM media ORDER BY id DESC";
        List<Media> list = new ArrayList<>();

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToMedia(rs));
            }
            return list;
        }
    }

    @Override
    public boolean update(int id, Media media) throws Exception {
        String sql = """
            UPDATE media
            SET title=?, description=?, media_type=?, release_year=?, age_restriction=?
            WHERE id=?;
        """;

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getDescription());
            stmt.setString(3, media.getMediaType());
            stmt.setInt(4, media.getReleaseYear());
            stmt.setInt(5, media.getAgeRestriction());
            stmt.setInt(6, id);

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM media WHERE id = ?";
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
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
