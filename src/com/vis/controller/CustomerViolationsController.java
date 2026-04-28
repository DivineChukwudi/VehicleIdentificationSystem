package com.vis.controller;

import com.vis.db.ViolationDAO;
import com.vis.model.Violation;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerViolationsController implements Initializable {

    @FXML private TableView<Violation> violationsTable;
    @FXML private TableColumn<Violation, Integer> colViolationID;
    @FXML private TableColumn<Violation, Integer> colVehicleID;
    @FXML private TableColumn<Violation, String> colViolationDate;
    @FXML private TableColumn<Violation, String> colViolationType;
    @FXML private TableColumn<Violation, Double> colFineAmount;
    @FXML private TableColumn<Violation, String> colStatus;
    @FXML private Label lblSummary;

    private int customerId = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colViolationID.setCellValueFactory(new PropertyValueFactory<>("violationId"));
        colVehicleID.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        colViolationDate.setCellValueFactory(new PropertyValueFactory<>("violationDate"));
        colViolationType.setCellValueFactory(new PropertyValueFactory<>("violationType"));
        colFineAmount.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
        loadViolations();
    }

    private void loadViolations() {
        if (customerId == -1) return;
        List<Violation> all = new ViolationDAO().getViolationsByCustomer(customerId);
        violationsTable.setItems(FXCollections.observableArrayList(all));
        long unpaid = all.stream().filter(v -> "UNPAID".equalsIgnoreCase(v.getStatus())).count();
        if (lblSummary != null) {
            lblSummary.setText("Total: " + all.size() + "   Unpaid: " + unpaid);
        }
    }
}