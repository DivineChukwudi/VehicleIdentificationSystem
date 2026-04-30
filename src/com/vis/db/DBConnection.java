package com.vis.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBConnection {
    // Database connection pool
    private static HikariDataSource dataSource;

    static {
        try {
            // Get credentials from environment
            String url = System.getenv("DB_URL");
            if (url == null) url = System.getenv("DATABASE_URL");
            
            String user = System.getenv("DB_USER");
            String pass = System.getenv("DB_PASS");

            // Fallback to config file
            if (url == null || user == null || pass == null) {
                Properties props = new Properties();
                try (InputStream input = DBConnection.class.getResourceAsStream("/config.properties")) {
                    if (input != null) {
                        props.load(input);
                        if (url == null) url = props.getProperty("DB_URL");
                        if (user == null) user = props.getProperty("DB_USER");
                        if (pass == null) pass = props.getProperty("DB_PASS");
                    }
                }
            }

            // Convert postgres URL format
            if (url != null) {
                if (url.startsWith("postgres://")) {
                    url = "jdbc:postgresql://" + url.substring("postgres://".length());
                } else if (url.startsWith("postgresql://")) {
                    url = "jdbc:postgresql://" + url.substring("postgresql://".length());
                }
            }

            // Error if config missing
            if (url == null) {
                throw new RuntimeException("Database configuration NOT FOUND!");
            }

            // Pool configuration
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            if (user != null) config.setUsername(user);
            if (pass != null) config.setPassword(pass);
            
            config.setMaximumPoolSize(20);
            config.setMinimumIdle(5);
            config.setIdleTimeout(300000);
            config.setConnectionTimeout(20000);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            // Start pool
            dataSource = new HikariDataSource(config);
            System.out.println("HikariCP Connection Pool initialized.");
            
            // Check schema
            ensureSchemaUpToDate();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database pool", e);
        }
    }

    // Fixes missing columns/tables
    private static void ensureSchemaUpToDate() {
        System.out.println("Checking database schema...");
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Add is_active column
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE");
            } catch (SQLException e) {
                System.out.println("Note: Could not add is_active column: " + e.getMessage());
            }

            // Add amount_paid column
            try {
                stmt.execute("ALTER TABLE Violation ADD COLUMN IF NOT EXISTS amount_paid NUMERIC DEFAULT 0.0");
            } catch (SQLException e) {
                System.out.println("Note: Could not add amount_paid column: " + e.getMessage());
            }

            // Create insurance table
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS Insurance (" +
                        "policy_id SERIAL PRIMARY KEY, " +
                        "vehicle_id INT NOT NULL, " +
                        "provider VARCHAR(100) NOT NULL, " +
                        "policy_number VARCHAR(50) UNIQUE NOT NULL, " +
                        "start_date DATE NOT NULL, " +
                        "end_date DATE NOT NULL, " +
                        "status VARCHAR(20) DEFAULT 'Active', " +
                        "FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id) ON DELETE CASCADE" +
                        ")");
                System.out.println("Insurance table verified/created.");
            } catch (SQLException e) {
                System.err.println("Error creating Insurance table: " + e.getMessage());
            }

            System.out.println("Database schema check complete.");
        } catch (SQLException e) {
            System.err.println("Error during schema migration: " + e.getMessage());
        }
    }

    // Get connection
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}

// URL -- TELLS CODE WHERE TO FIND DATABASE
// USER/PASSWORD -- BASICALLY CREDENTIALS TO ACCESS THE DATABASE
//getConnection() -- WHEN CALLED, OPENS A CONNECTION TO DATABASE
//throw SQLException -- IF CONNECTION FAILS, THROWS AN ERROR WE CAN CATCH

//IT'S MY FIRST TIME GETTING INTO SUCH DETAIL WITH THESE STUFF