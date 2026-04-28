package com.vis.db;

import com.vis.db.DBConnection;
import com.vis.model.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    public boolean addService(int vehicleId, String serviceDate, String serviceType, String description, double cost) {
        String sql = "INSERT INTO ServiceRecord(vehicle_id, service_date, service_type, description, cost) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            stmt.setString(2, serviceDate);
            stmt.setString(3, serviceType);
            stmt.setString(4, description);
            stmt.setDouble(5, cost);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ServiceDAO.addService() failed: " + e.getMessage());
            return false;
        }
    }

    public List<Service> getServicesByCustomer(int customerId) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT sr.* FROM ServiceRecord sr " +
                "JOIN Vehicle v ON sr.vehicle_id = v.vehicle_id " +
                "WHERE v.owner_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    services.add(new Service(
                            rs.getInt("service_id"),
                            rs.getInt("vehicle_id"),
                            rs.getString("service_date"),
                            rs.getString("service_type"),
                            rs.getString("description"),
                            rs.getDouble("cost")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM ServiceRecord";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                services.add(new Service(
                        rs.getInt("service_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("service_date"),
                        rs.getString("service_type"),
                        rs.getString("description"),
                        rs.getDouble("cost")
                ));
            }

        } catch (SQLException e) {
            System.err.println("ServiceDAO.getAllServices() failed: " + e.getMessage());
            e.printStackTrace();
        }
        return services;
    }
}
