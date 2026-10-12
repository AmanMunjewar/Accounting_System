package com.vectorbank.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database configuration loaded dynamically from environment variables.
 * Eliminates hardcoded credentials and static shared connections.
 */
public class DatabaseConfig {
    private final String url;
    private final String username;
    private final String password;

    public DatabaseConfig() {
        String host = getEnvOrDefault("DB_HOST", "localhost");
        String port = getEnvOrDefault("DB_PORT", "3306");
        String dbName = getEnvOrDefault("DB_NAME", "banking");
        String customUrl = System.getenv("DB_URL");

        if (customUrl != null && !customUrl.trim().isEmpty()) {
            this.url = customUrl;
        } else {
            this.url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                    host, port, dbName);
        }

        this.username = getEnvOrDefault("DB_USER", "root");
        this.password = getEnvOrDefault("DB_PASSWORD", "root");
    }

    public DatabaseConfig(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Creates and returns a fresh JDBC Connection.
     * Callers are responsible for closing it via try-with-resources.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String val = System.getenv(key);
        return (val != null && !val.trim().isEmpty()) ? val.trim() : defaultValue;
    }
}
