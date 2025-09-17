package org.mrp;


import org.mrp.util.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("✅ Connected to PostgreSQL!");

            ResultSet rs = stmt.executeQuery("SELECT * FROM test_connection");
            while (rs.next()) {
                System.out.println("Row: id=" + rs.getInt("id")
                        + ", message=" + rs.getString("message"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}