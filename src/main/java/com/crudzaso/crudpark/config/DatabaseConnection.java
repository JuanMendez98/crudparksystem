package com.crudzaso.crudpark.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class to manage PostgreSQL database connection Ensures a single connection instance
 * throughout the application
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private final AppConfig config;

    private DatabaseConnection() {

        config = AppConfig.getInstance();
        try {
            this.connection = createConnection();
            System.out.println("Database connection established successfully");
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(config.getDbUrl(), config.getDbUser(),
                config.getDbPassword());
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = createConnection();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error testing connection: " + e.getMessage());
            return false;
        }
    }
}
