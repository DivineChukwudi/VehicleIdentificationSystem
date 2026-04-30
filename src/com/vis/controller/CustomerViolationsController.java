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

    // Table elements for the customer's personal violations
    @FXML private TableView<Violation> violationsTable;
    @FXML private TableColumn<Violation, Integer> colViolationID;
    @FXML private TableColumn<Violation, Integer> colVehicleID;
    @FXML private TableColumn<Violation, String> colViolationDate;
    @FXML private TableColumn<Violation, String> colViolationType;
    @FXML private TableColumn<Violation, Double> colFineAmount;
    @FXML private TableColumn<Violation, Double> colAmountPaid;
    @FXML private TableColumn<Violation, String> colStatus;
    @FXML private Label lblSummary; // Status summary at the top

    private int customerId = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Map columns to Violation object fields
        colViolationID.setCellValueFactory(new PropertyValueFactory<>("violationId"));
        colVehicleID.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        colViolationDate.setCellValueFactory(new PropertyValueFactory<>("violationDate"));
        colViolationType.setCellValueFactory(new PropertyValueFactory<>("violationType"));
        colFineAmount.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
        colAmountPaid.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        setupStatusCellFactory(); // Colored badges for statuses
    }

    /**
     * Styles the status column with badges (PAID, PARTIAL, UNPAID).
     */
    private void setupStatusCellFactory() {
        colStatus.setCellFactory(column -> new TableCell<Violation, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item.toUpperCase());
                    String style = com.vis.util.UIUtils.BADGE_PENDING_STYLE; 
                    
                    if ("PAID".equalsIgnoreCase(item)) {
                        style = com.vis.util.UIUtils.BADGE_SUCCESS_STYLE;
                    } else if ("PARTIALLY PAID".equalsIgnoreCase(item)) {
                        style = com.vis.util.UIUtils.BADGE_INFO_STYLE;
                    }
                    
                    badge.setStyle(style);
                    setGraphic(badge);
                }
            }
        });
    }

    /**
     * Sets the customer ID and triggers the data load.
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
        loadViolations();
    }

    /**
     * Fetches only the violations for this specific customer.
     */
    private void loadViolations() {
        if (customerId == -1) return;
        List<Violation> all = new ViolationDAO().getViolationsByCustomer(customerId);
        violationsTable.setItems(FXCollections.observableArrayList(all));
        
        // Calculate counts for the summary label
        long pending = all.stream()
                .filter(v -> !"PAID".equalsIgnoreCase(v.getStatus()))
                .count();
        if (lblSummary != null) {
            lblSummary.setText("Total: " + all.size() + "   Unpaid/Partial: " + pending);
        }
    }
}