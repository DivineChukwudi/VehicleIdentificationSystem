package com.vis.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DBConnection {
    private static String URL;
    private static String USER;
    private static String PASS;
    static {
        try {
            // 1. Try Environment Variables first (for GitHub/Render)
            URL = System.getenv("DB_URL");
            USER = System.getenv("DB_USER");
            PASS = System.getenv("DB_PASS");

            // 2. Fallback to config.properties if env vars are missing
            if (URL == null || USER == null || PASS == null) {
                Properties props = new Properties();
                InputStream input = DBConnection.class.getResourceAsStream("/config.properties");

                if (input != null) {
                    props.load(input);
                    URL = props.getProperty("DB_URL");
                    USER = props.getProperty("DB_USER");
                    PASS = props.getProperty("DB_PASS");
                } else if (URL == null) {
                    // Only throw error if we don't have URL from anywhere
                    throw new RuntimeException("Database configuration NOT FOUND (Environment variables or config.properties missing)!");
                }
            }

            // Ensure URL starts with the JDBC prefix if it's a raw URL from Render
            if (URL != null && URL.startsWith("postgresql://")) {
                URL = "jdbc:" + URL;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL Driver not found!", e);
        }

        return DriverManager.getConnection(URL, USER, PASS);
    }
}
// URL -- TELLS CODE WHERE TO FIND DATABASE
// USER/PASSWORD -- BASICALLY CREDENTIALS TO ACCESS THE DATABASE
//getConnection() -- WHEN CALLED, OPENS A CONNECTION TO DATABASE
//throw SQLException -- IF CONNECTION FAILS, THROWS AN ERROR WE CAN CATCH

//IT'S MY FIRST TIME GETTING INTO SUCH DETAIL WITH THESE STUFF