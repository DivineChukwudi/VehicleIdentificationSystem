package com.vis.controller;

import com.vis.db.InsuranceDAO;
import com.vis.model.Insurance;
import com.vis.util.UIUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class InsuranceAddController implements Initializable {

    @FXML private TextField txtVehicleID;
    @FXML private TextField txtProvider;
    @FXML private TextField txtPolicyNumber;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private ComboBox<String> cbStatus;
    @FXML private Label lblStatus;

    private final InsuranceDAO insuranceDAO = new InsuranceDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbStatus.getItems().addAll("Active", "Expired", "Cancelled", "Pending");
        cbStatus.setValue("Active");

        // Apply Validation
        UIUtils.applyDigitValidation(txtVehicleID);
        UIUtils.applyLetterValidation(txtProvider);
        UIUtils.applyDigitValidation(txtPolicyNumber);
    }

    @FXML
    private void handleSave() {
        String vIdStr = txtVehicleID.getText().trim();
        String provider = txtProvider.getText().trim();
        String policyNum = txtPolicyNumber.getText().trim();
        LocalDate start = dpStartDate.getValue();
        LocalDate end = dpEndDate.getValue();
        String status = cbStatus.getValue();

        if (vIdStr.isEmpty() || provider.isEmpty() || policyNum.isEmpty() || start == null || end == null) {
            showError("Please fill in all fields.");
            return;
        }

        try {
            int vehicleId = Integer.parseInt(vIdStr);
            Insurance insurance = new Insurance(0, vehicleId, provider, policyNum, start, end, status);
            boolean success = insuranceDAO.addInsurance(insurance);

            if (success) {
                showSuccess("Policy saved successfully!");
                handleClear();
            } else {
                showError("Failed to save policy. Check Vehicle ID or Policy Number.");
            }
        } catch (NumberFormatException e) {
            showError("Vehicle ID must be a number.");
        }
    }

    @FXML
    private void handleClear() {
        txtVehicleID.clear();
        txtProvider.clear();
        txtPolicyNumber.clear();
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
