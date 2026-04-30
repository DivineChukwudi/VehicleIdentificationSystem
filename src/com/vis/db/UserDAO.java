package com.vis.db;

import com.vis.db.DBConnection;
import com.vis.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Authenticates user
    public User loginUser(String username, String password) {
        String hashedPassword = PasswordUtil.hashPassword(password);
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Check active status
                    boolean isActive = true;
                    try {
                        isActive = rs.getBoolean("is_active");
                    } catch (SQLException ignored) {}

                    return new User(
                            rs.getInt("userid"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            isActive
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.loginUser() failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Get customer record ID
    public int getCustomerId(int userId) {
        String sql = "SELECT customer_id FROM Users WHERE userid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("customer_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Get unpaid fines summary
    public List<String> getUnpaidViolations(int customerId) {
        List<String> violations = new ArrayList<>();
        String sql = "SELECT v.violation_type, v.fine_amount, v.violation_date " +
                "FROM Violation v " +
                "JOIN Vehicle vh ON v.vehicle_id = vh.vehicle_id " +
                "WHERE vh.owner_id = ? AND UPPER(v.status) = 'UNPAID'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    violations.add(rs.getString("violation_type") +
                            " — M" + String.format("%.2f", rs.getDouble("fine_amount")) +
                            " on " + rs.getString("violation_date"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return violations;
    }

    // Registers user and customer profile
    public boolean registerUser(String username, String password, String role,
                                String firstName, String lastName, String email) {
        String hashedPassword = PasswordUtil.hashPassword(password);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Create account
            String userSql = "INSERT INTO users (username, password, role, first_name, last_name, email) " +
                    "VALUES (?, ?, ?, ?, ?, ?) RETURNING userid";
            int newUserId = -1;

            try (PreparedStatement stmt = conn.prepareStatement(userSql)) {
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, role);
                stmt.setString(4, firstName.isEmpty() ? null : firstName);
                stmt.setString(5, lastName.isEmpty()  ? null : lastName);
                stmt.setString(6, email.isEmpty()     ? null : email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) newUserId = rs.getInt("userid");
                }
            }

            // 2. Link customer profile
            if (role.equalsIgnoreCase("customer") && newUserId != -1) {
                String custSql = "INSERT INTO Customer(name, surname, email) VALUES(?, ?, ?) RETURNING customer_id";
                try (PreparedStatement stmt = conn.prepareStatement(custSql)) {
                    stmt.setString(1, firstName);
                    stmt.setString(2, lastName);
                    stmt.setString(3, email);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            int customerId = rs.getInt("customer_id");
                            String linkSql = "UPDATE users SET customer_id = ? WHERE userid = ?";
                            try (PreparedStatement link = conn.prepareStatement(linkSql)) {
                                link.setInt(1, customerId);
                                link.setInt(2, newUserId);
                                link.executeUpdate();
                            }
                        }
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("UserDAO.registerUser() failed: " + e.getMessage());
            return false;
        }
    }

    // List all users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY userid DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                boolean isActive = true;
                try { isActive = rs.getBoolean("is_active"); } catch (SQLException ignored) {}
                
                users.add(new User(
                        rs.getInt("userid"),
                        rs.getString("username"),
                        "********", 
                        rs.getString("role"),
                        isActive
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Enable/Disable user
    public boolean toggleUserStatus(int userId, boolean active) {
        String sql = "UPDATE users SET is_active = ? WHERE userid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, active);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Manual user creation
    public boolean createUserForCustomer(int customerId, String username, String password) {
        String hashedPassword = PasswordUtil.hashPassword(password);
        String sql = "INSERT INTO users (username, password, role, customer_id) VALUES (?, ?, 'customer', ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setInt(3, customerId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("UserDAO.createUserForCustomer() failed: " + e.getMessage());
            return false;
        }
    }

    // Check if account exists
    public boolean hasUserAccount(int customerId) {
        String sql = "SELECT 1 FROM users WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
