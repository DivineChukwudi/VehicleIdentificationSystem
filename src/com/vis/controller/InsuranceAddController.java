package com.vis.controller;

import com.vis.db.InsuranceDAO;
import com.vis.db.VehicleDAO;
import com.vis.model.Insurance;
import com.vis.model.Vehicle;
import com.vis.util.UIUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class InsuranceAddController implements Initializable {

    // FXML UI elements for adding an insurance policy
    @FXML private ComboBox<Vehicle> cbVehicles;
    @FXML private TextField txtProvider;
    @FXML private TextField txtPolicyNumber;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private ComboBox<String> cbStatus;
    @FXML private Label lblStatus; // For success/error messages

    private final InsuranceDAO insuranceDAO = new InsuranceDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup status options
        cbStatus.getItems().addAll("Active", "Expired", "Cancelled", "Pending");
        cbStatus.setValue("Active");

        loadVehicles();          // Load car list into the dropdown
        generatePolicyNumber();  // Create a random 8-digit policy number

        // Prevent users from typing numbers in the provider name
        UIUtils.applyLetterValidation(txtProvider);
    }

    /**
     * Loads all vehicles into the dropdown with readable labels.
     */
    private void loadVehicles() {
        List<Vehicle> vehicles = VehicleDAO.getAllVehicles();
        cbVehicles.getItems().setAll(vehicles);
        
        // Custom display for the dropdown: "REG-123 (Toyota Corolla)"
        cbVehicles.setConverter(new StringConverter<Vehicle>() {
            @Override
            public String toString(Vehicle v) {
                return v == null ? "" : v.getRegistrationNumber() + " (" + v.getMake() + " " + v.getModel() + ")";
            }
            @Override
            public Vehicle fromString(String string) { return null; }
        });
    }

    /**
     * Generates a unique 8-digit policy number automatically.
     */
    private void generatePolicyNumber() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(rand.nextInt(10));
        }
        txtPolicyNumber.setText(sb.toString());
        txtPolicyNumber.setEditable(false); // Make it read-only so users can't change it
    }

    /**
     * Saves the new insurance policy to the database.
     */
    @FXML
    private void handleSave() {
        Vehicle selectedVehicle = cbVehicles.getValue();
        String provider = txtProvider.getText().trim();
        String policyNum = txtPolicyNumber.getText().trim();
        LocalDate start = dpStartDate.getValue();
        LocalDate end = dpEndDate.getValue();
        String status = cbStatus.getValue();

        // Ensure no fields are empty
        if (selectedVehicle == null || provider.isEmpty() || policyNum.isEmpty() || start == null || end == null) {
            showError("Please fill in all fields.");
            return;
        }

        // Create model and send to database
        Insurance insurance = new Insurance(0, selectedVehicle.getVehicleID(), provider, policyNum, start, end, status);
        boolean success = insuranceDAO.addInsurance(insurance);

        if (success) {
            showSuccess("Policy saved successfully!");
            handleClear(); // Reset the form
        } else {
            showError("Failed to save policy. This Policy Number may already exist.");
        }
    }

    /**
     * Resets the form to its original state.
     */
    @FXML
    private void handleClear() {
        cbVehicles.setValue(null);
        txtProvider.clear();
        generatePolicyNumber(); // Generate a fresh policy number
        dpStartDate.setValue(null);
        dpEndDate.setValue(null);
        cbStatus.setValue("Active");
    }

    private void showError(String msg) {
        lblStatus.setTextFill(Color.web("#e74c3c"));
        lblStatus.setText(msg);
    }

    private void showSuccess(String msg) {
        lblStatus.setTextFill(Color.web("#27ae60"));
        lblStatus.setText(msg);
    }
}
