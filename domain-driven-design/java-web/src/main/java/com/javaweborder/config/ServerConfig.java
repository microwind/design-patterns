package com.javaweborder.config;

import com.javaweborder.infrastructure.configuration.DatabaseConfig;
import com.javaweborder.infrastructure.configuration.LoggingConfig;
import com.javaweborder.infrastructure.configuration.JWTConfig;
import com.javaweborder.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerConfig {
    private int port;
    private String env;
    private String appName;
    private final DatabaseConfig database;
    private final LoggingConfig logging;
    private final JWTConfig jwt;

    public ServerConfig() {
        this.database = new DatabaseConfig();
        this.logging = new LoggingConfig();
        this.jwt = new JWTConfig();

        loadConfig();
    }

    private void loadConfig() {
        Properties properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("无法找到 application.properties");
                return;
            }

            properties.load(input);

            // 读取基本配置
            this.port = Integer.parseInt(properties.getProperty("server.port", "8080"));
            this.env = properties.getProperty("env", "development");
            this.appName = properties.getProperty("appName", "DDD Java App");

            // 读取数据库配置
            this.database.setHost(properties.getProperty("database.host", "localhost"));
            this.database.setPort(Integer.parseInt(properties.getProperty("database.port", "5432")));
            this.database.setUser(properties.getProperty("database.user", "postgres"));
            this.database.setPassword(properties.getProperty("database.password", "password"));
            this.database.setName(properties.getProperty("database.name", "order_db"));

            // 读取日志配置
            this.logging.setLevel(properties.getProperty("logging.level", "info"));
            this.logging.setFile(properties.getProperty("logging.file", "app.log"));

            // 读取 JWT 配置
            this.jwt.setSecret(properties.getProperty("jwt.secret", "your_jwt_secret"));
            this.jwt.setExpiresIn(properties.getProperty("jwt.expiresIn", "1h"));

        } catch (IOException e) {
            LogUtils.logError("loadConfig error:", e);
        }
    }

    public int getPort() {
        return port;
    }

    public String getEnv() {
        return env;
    }

    public String getAppName() {
        return appName;
    }

    public DatabaseConfig getDatabase() {
        return database;
    }

    public LoggingConfig getLogging() {
        return logging;
    }

    public JWTConfig getJwt() {
        return jwt;
    }

    public static void main(String[] args) {
        ServerConfig config = new ServerConfig();

        System.out.println("Server Port: " + config.getPort());
        System.out.println("App Name: " + config.getAppName());
        System.out.println("Database Host: " + config.getDatabase().getHost());
        System.out.println("JWT Secret: " + config.getJwt().getSecret());
    }
}
