package com.vis.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static HikariDataSource dataSource;

    static {
        try {
            String url = System.getenv("DB_URL");
            if (url == null) url = System.getenv("DATABASE_URL");
            
            String user = System.getenv("DB_USER");
            String pass = System.getenv("DB_PASS");

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

            if (url != null) {
                if (url.startsWith("postgres://")) {
                    url = "jdbc:postgresql://" + url.substring("postgres://".length());
                } else if (url.startsWith("postgresql://")) {
                    url = "jdbc:postgresql://" + url.substring("postgresql://".length());
                }
            }

            if (url == null) {
                throw new RuntimeException("Database configuration NOT FOUND!");
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            if (user != null) config.setUsername(user);
            if (pass != null) config.setPassword(pass);
            
            // Optimization for performance
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(300000);
            config.setConnectionTimeout(20000);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
            System.out.println("HikariCP Connection Pool initialized.");
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database pool", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
// URL -- TELLS CODE WHERE TO FIND DATABASE
// USER/PASSWORD -- BASICALLY CREDENTIALS TO ACCESS THE DATABASE
//getConnection() -- WHEN CALLED, OPENS A CONNECTION TO DATABASE
//throw SQLException -- IF CONNECTION FAILS, THROWS AN ERROR WE CAN CATCH

//IT'S MY FIRST TIME GETTING INTO SUCH DETAIL WITH THESE STUFF