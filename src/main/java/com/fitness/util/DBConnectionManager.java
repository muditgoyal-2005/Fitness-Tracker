package com.fitness.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

// Singleton connection provider: Tomcat owns pooling in production; this compact manager opens JDBC connections per operation.
public final class DBConnectionManager {
    private static final Logger LOGGER = Logger.getLogger(DBConnectionManager.class.getName());
    private static final DBConnectionManager INSTANCE = new DBConnectionManager();
    private final Properties properties = new Properties();
    private DBConnectionManager() {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) throw new IllegalStateException("db.properties was not found on the classpath");
            properties.load(input);
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException exception) {
            LOGGER.log(Level.SEVERE, "Database configuration could not be loaded", exception);
            throw new IllegalStateException(exception);
        }
    }
    public static DBConnectionManager getInstance() { return INSTANCE; }
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(properties.getProperty("db.url"), properties.getProperty("db.username"), properties.getProperty("db.password"));
    }
}