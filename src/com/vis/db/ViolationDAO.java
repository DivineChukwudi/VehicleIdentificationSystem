package com.vis.db;

import com.vis.db.DBConnection;
import com.vis.model.Violation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViolationDAO {

    public boolean addViolation(int vehicleId, String violationDate, String violationType, double fineAmount, String description) {
        String sql = "INSERT INTO Violation(vehicle_id, violation_date, violation_type, fine_amount, status, description) VALUES(?, ?, ?, ?, 'UNPAID', ?)";
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
                    violations.add(new Violation(
                            rs.getInt("violation_id"),
                            rs.getInt("vehicle_id"),
                            rs.getString("violation_date"),
                            rs.getString("violation_type"),
                            rs.getDouble("fine_amount"),
                            rs.getString("status"),
                            rs.getString("description")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return violations;
    }

    public List<Violation> getAllViolations() {
        List<Violation> violations = new ArrayList<>();
        String sql = "SELECT * FROM Violation";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                violations.add(new Violation(
                        rs.getInt("violation_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("violation_date"),
                        rs.getString("violation_type"),
                        rs.getDouble("fine_amount"),
                        rs.getString("status"),
                        rs.getString("description")
                ));
            }

        } catch (SQLException e) {
            System.err.println("ViolationDAO.getAllViolations() failed: " + e.getMessage());
            e.printStackTrace();
        }
        return violations;
    }

    /**
     * Calls the PostgreSQL PROCEDURE mark_violation_paid (stored_procedures.sql).
     * @param violationId the ID of the violation to mark as paid
     */
    public void markAsPaid(int violationId) {
        String sql = "CALL mark_violation_paid(?)";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, violationId);
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("ViolationDAO.markAsPaid() failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

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
