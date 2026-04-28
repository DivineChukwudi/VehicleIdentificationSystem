package com.vis.db;

import com.vis.db.DBConnection;
import com.vis.model.PoliceReport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PoliceReportDAO {

    public boolean addReport(int vehicleId, String reportDate, String reportType, String description, String officerName) {
        String sql = "INSERT INTO PoliceReport(vehicle_id, report_date, report_type, description, officer_name) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            stmt.setString(2, reportDate);
            stmt.setString(3, reportType);
            stmt.setString(4, description);
            stmt.setString(5, officerName);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("PoliceReportDAO.addReport() failed: " + e.getMessage());
            return false;
        }
    }

    public List<PoliceReport> getAllReports() {
        List<PoliceReport> reports = new ArrayList<>();
        String sql = "SELECT * FROM PoliceReport";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reports.add(new PoliceReport(
                        rs.getInt("report_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("report_date"),
                        rs.getString("report_type"),
                        rs.getString("description"),
                        rs.getString("officer_name")
                ));
            }

        } catch (SQLException e) {
            System.err.println("PoliceReportDAO.getAllReports() failed: " + e.getMessage());
            e.printStackTrace();
        }
        return reports;
    }
}
