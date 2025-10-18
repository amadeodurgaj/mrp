package org.mrp.util;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DBUtil {

    private final String url;
    private final String username;
    private final String password;

    public DBUtil() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.yaml")) {
            if (in == null) {
                throw new RuntimeException("application.yaml not found in resources");
            }
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(in);

            Map<String, Object> dbConfig = (Map<String, Object>) config.get("database");
            this.url = (String) dbConfig.get("url");
            this.username = (String) dbConfig.get("username");
            this.password = (String) dbConfig.get("password");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load DB config", e);
        }
    }

    public Connection getConnection() throws Exception {
        return DriverManager.getConnection(url, username, password);
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
