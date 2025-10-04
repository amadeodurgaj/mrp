package org.mrp.service;

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
import java.util.concurrent.ConcurrentHashMap;


public class UserService {

    public static final int CODE_USER_EXISTS = 0;
    public static final int CODE_LOGIN_SUCCESSFUL = 1;
    public final int CODE_INTERNAL_ERROR = 2;

    private static final Map<String, Integer> activeTokens = new ConcurrentHashMap<>();


    public int registerUser(String username, String password) {
        String checkSql = "SELECT id FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, password_hash, created_at) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return CODE_USER_EXISTS;
            }

            insertStmt.setString(1, username);
            insertStmt.setString(2, hashPassword(password));
            insertStmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            insertStmt.executeUpdate();

            return CODE_LOGIN_SUCCESSFUL;

        } catch (Exception e) {
            e.printStackTrace();
            return CODE_INTERNAL_ERROR;
        }
    }

    public String loginUser(String username, String password) {
        String sql = "SELECT id, password_hash FROM users WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                if (storedHash.equals(hashPassword(password))) {
                    String token = username + "-mrpToken";
                    int userId = rs.getInt("id");
                    activeTokens.put(token, userId);
                    return token;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByToken(String token) {
        Integer userId = activeTokens.get(token);
        if (userId == null) return null;

        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByUsername(String username) {
        String userSql = "SELECT * FROM users WHERE username = ?";
        String favoritesSql = "SELECT COUNT(*) AS total_favorites FROM favorites f JOIN users u ON f.user_id = u.id WHERE u.username = ?";
        String ratingsSql = "SELECT COUNT(*) AS total_ratings, COALESCE(AVG(stars),0) AS average_rating FROM ratings r JOIN users u ON r.user_id = u.id WHERE u.username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement userStmt = conn.prepareStatement(userSql);
             PreparedStatement favStmt = conn.prepareStatement(favoritesSql);
             PreparedStatement ratingStmt = conn.prepareStatement(ratingsSql)) {

            userStmt.setString(1, username);
            ResultSet rs = userStmt.executeQuery();
            if (!rs.next()) return null;

            User user = mapResultSetToUser(rs);

            favStmt.setString(1, username);
            ResultSet favRs = favStmt.executeQuery();
            if (favRs.next()) {
                user.setTotalFavorites(favRs.getInt("total_favorites"));
            }

            ratingStmt.setString(1, username);
            ResultSet ratingRs = ratingStmt.executeQuery();
            if (ratingRs.next()) {
                user.setTotalRatings(ratingRs.getInt("total_ratings"));
                user.setAverageRating(ratingRs.getDouble("average_rating"));
            }

            return user;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean updateUserProfile(int userId, String email, String favoriteGenre) {
        String sql = "UPDATE users SET email = ?, favorite_genre = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, favoriteGenre);
            stmt.setInt(3, userId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    private User mapResultSetToUser(ResultSet rs) throws Exception {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setFavoriteGenre(rs.getString("favorite_genre"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }

}
