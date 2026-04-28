package com.vis.db;

import com.vis.db.DBConnection;
import com.vis.model.Insurance;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InsuranceDAO {

    public List<Insurance> getAllInsurances() {
        List<Insurance> insurances = new ArrayList<>();
        String sql = "SELECT * FROM Insurance";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                insurances.add(extractInsuranceFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("InsuranceDAO.getAllInsurances() failed: " + e.getMessage());
        }
        return insurances;
    }

    public List<Insurance> getInsurancesByVehicle(int vehicleId) {
        List<Insurance> insurances = new ArrayList<>();
        String sql = "SELECT * FROM Insurance WHERE vehicle_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    insurances.add(extractInsuranceFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("InsuranceDAO.getInsurancesByVehicle() failed: " + e.getMessage());
        }
        return insurances;
    }

    public boolean addInsurance(Insurance insurance) {
        String sql = "INSERT INTO Insurance (vehicle_id, provider, policy_number, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, insurance.getVehicleId());
            stmt.setString(2, insurance.getProvider());
            stmt.setString(3, insurance.getPolicyNumber());
            stmt.setDate(4, Date.valueOf(insurance.getStartDate()));
            stmt.setDate(5, Date.valueOf(insurance.getEndDate()));
            stmt.setString(6, insurance.getStatus());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("InsuranceDAO.addInsurance() failed: " + e.getMessage());
            return false;
        }
    }

    private Insurance extractInsuranceFromResultSet(ResultSet rs) throws SQLException {
        return new Insurance(
                rs.getInt("policy_id"),
                rs.getInt("vehicle_id"),
                rs.getString("provider"),
                rs.getString("policy_number"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getString("status")
        );
    }
}
