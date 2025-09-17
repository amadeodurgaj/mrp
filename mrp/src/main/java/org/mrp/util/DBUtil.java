package org.mrp.util;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DBUtil {

    private static final String url;
    private static final String username;
    private static final String password;

    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("application.yaml")) {
            if (in == null) {
                throw new RuntimeException("application.yaml not found in resources");
            }
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(in);

            Map<String, Object> dbConfig = (Map<String, Object>) config.get("database");
            url = (String) dbConfig.get("url");
            username = (String) dbConfig.get("username");
            password = (String) dbConfig.get("password");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load DB config", e);
        }
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(url, username, password);
    }
}
