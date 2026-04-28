package com.vis.db;

import com.vis.db.DBConnection;
import com.vis.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {




    public boolean updateCustomer(Customer c) {
        return updateCustomer(c.getCustomerID(), c.getName(), c.getSurname(), c.getAddress(), c.getPhone(), c.getEmail());
    }

    public boolean updateCustomer(int id, String name, String surname,
                                  String address, String phone, String email) {
        String sql = "UPDATE Customer SET name=?, surname=?, address=?, phone=?, email=? WHERE customer_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, surname);
            stmt.setString(3, address);
            stmt.setString(4, phone);
            stmt.setString(5, email);
            stmt.setInt(6, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("CustomerDAO.updateCustomer() failed: " + e.getMessage());
            return false;
        }
    }

    public boolean addCustomer(String name, String surname, String address, String phone, String email) {
        String sql = "CALL add_customer(?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, surname);
            stmt.setString(3, address);
            stmt.setString(4, phone);
            stmt.setString(5, email);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("CustomerDAO.addCustomer() via procedure failed: " + e.getMessage());
            return false;
        }
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customer";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email")
                ));
            }

        } catch (SQLException e) {
            System.err.println("CustomerDAO.getAllCustomers() failed: " + e.getMessage());
            e.printStackTrace();
        }

        return customers;
    }

    public Customer getCustomerById(int id) {
        String sql = "SELECT * FROM Customer WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getInt("customer_id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("address"),
                            rs.getString("phone"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("CustomerDAO.getCustomerById() failed: " + e.getMessage());
        }
        return null;
    }
}
