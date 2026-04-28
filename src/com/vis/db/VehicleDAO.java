package com.vis.db;

import com.vis.db.DBConnection;
import com.vis.model.Vehicle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {


    public static List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM Vehicle";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vehicles.add(new Vehicle(
                        rs.getInt("vehicle_id"),
                        rs.getString("registration_number"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getInt("owner_id")
                ));
            }

        } catch (SQLException e) {
            System.err.println("VehicleDAO.getAllVehicles() failed: " + e.getMessage());
            e.printStackTrace();
        }
        return vehicles;
    }

    public List<Vehicle> getVehiclesByCustomer(int customerId) {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM Vehicle WHERE owner_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(new Vehicle(
                            rs.getInt("vehicle_id"),
                            rs.getString("registration_number"),
                            rs.getString("make"),
                            rs.getString("model"),
                            rs.getInt("year"),
                            rs.getInt("owner_id")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }


    public List<Vehicle> getVehiclesFromView() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT vehicle_id, registration_number, make, model, year, owner_id " +
                     "FROM vehicle_owner_view";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vehicles.add(new Vehicle(
                        rs.getInt("vehicle_id"),
                        rs.getString("registration_number"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getInt("owner_id")
                ));
            }

        } catch (SQLException e) {
            System.err.println("VehicleDAO.getVehiclesFromView() failed — falling back to direct query.");
            return getAllVehicles();
        }
        return vehicles;
    }
    public boolean addVehicle(String registrationNumber, String make, String model, int year, int ownerID) {
        String sql = "CALL add_vehicle(?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, registrationNumber);
            stmt.setString(2, make);
            stmt.setString(3, model);
            stmt.setInt(4, year);
            stmt.setInt(5, ownerID);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("VehicleDAO.addVehicle() via procedure failed: " + e.getMessage());
            return false;
        }
    }


    public List<Vehicle> getVehiclesViaStoredProcedure() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM get_all_vehicles()";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vehicles.add(new Vehicle(
                        rs.getInt("vehicle_id"),
                        rs.getString("registration_number"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getInt("owner_id")
                ));
            }

        } catch (SQLException e) {
            System.err.println("VehicleDAO.getVehiclesViaStoredProcedure() failed — falling back.");
            return getAllVehicles();
        }
        return vehicles;
    }
}
