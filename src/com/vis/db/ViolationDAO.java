package com.vis.db;

import com.vis.db.DBConnection;
import com.vis.model.Violation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViolationDAO {

    // Record violation
    public boolean addViolation(int vehicleId, String violationDate, String violationType, double fineAmount, String description) {
        String sql = "INSERT INTO Violation(vehicle_id, violation_date, violation_type, fine_amount, amount_paid, status, description) VALUES(?, CAST(? AS DATE), ?, ?, 0.0, 'UNPAID', ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            stmt.setString(2, violationDate);
            stmt.setString(3, violationType);
            stmt.setDouble(4, fineAmount);
            stmt.setString(5, description);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ViolationDAO.addViolation() failed: " + e.getMessage());
            return false;
        }
    }

    // Get violations by customer
    public List<Violation> getViolationsByCustomer(int customerId) {
        List<Violation> violations = new ArrayList<>();
        String sql = "SELECT v.* FROM Violation v " +
                "JOIN Vehicle vh ON v.vehicle_id = vh.vehicle_id " +
                "WHERE vh.owner_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    violations.add(extractViolationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return violations;
    }

    // List all violations
    public List<Violation> getAllViolations() {
        List<Violation> violations = new ArrayList<>();
        String sql = "SELECT * FROM Violation";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                violations.add(extractViolationFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("ViolationDAO.getAllViolations() failed: " + e.getMessage());
            e.printStackTrace();
        }
        return violations;
    }

    // Extract violation from result set
    private Violation extractViolationFromResultSet(ResultSet rs) throws SQLException {
        double amountPaid = 0;
        try { amountPaid = rs.getDouble("amount_paid"); } catch (SQLException ignored) {}
        
        return new Violation(
                rs.getInt("violation_id"),
                rs.getInt("vehicle_id"),
                rs.getString("violation_date"),
                rs.getString("violation_type"),
                rs.getDouble("fine_amount"),
                amountPaid,
                rs.getString("status"),
                rs.getString("description")
        );
    }

    // Record payment
    public boolean recordPayment(int violationId, double paymentAmount) {
        String selectSql = "SELECT fine_amount, amount_paid FROM Violation WHERE violation_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            
            selectStmt.setInt(1, violationId);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    double totalFine = rs.getDouble("fine_amount");
                    double alreadyPaid = rs.getDouble("amount_paid");
                    double newPaidTotal = alreadyPaid + paymentAmount;
                    
                    String newStatus = (newPaidTotal >= totalFine) ? "PAID" : "PARTIALLY PAID";
                    
                    String updateSql = "UPDATE Violation SET amount_paid = ?, status = ? WHERE violation_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setDouble(1, newPaidTotal);
                        updateStmt.setString(2, newStatus);
                        updateStmt.setInt(3, violationId);
                        return updateStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("ViolationDAO.recordPayment() failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Mark as paid
    public void markAsPaid(int violationId) {
        String sql = "UPDATE Violation SET amount_paid = fine_amount, status = 'PAID' WHERE violation_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, violationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ViolationDAO.markAsPaid() failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Delete violation
    public boolean deleteViolation(int violationId) {
        String sql = "DELETE FROM Violation WHERE violation_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, violationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ViolationDAO.deleteViolation() failed: " + e.getMessage());
            return false;
        }
    }
}
