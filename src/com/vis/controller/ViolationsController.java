package com.vis.controller;

import com.vis.db.VehicleDAO;
import com.vis.db.ViolationDAO;
import com.vis.model.Violation;
import com.vis.util.UIUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ViolationsController implements Initializable {

    // Table elements
    @FXML private TableView<Violation>             violationsTable;
    @FXML private TableColumn<Violation, Integer>  colViolationID;
    @FXML private TableColumn<Violation, String>   colVehicleID;
    @FXML private TableColumn<Violation, String>   colViolationDate;
    @FXML private TableColumn<Violation, String>   colViolationType;
    @FXML private TableColumn<Violation, Double>   colFineAmount;
    @FXML private TableColumn<Violation, Double>   colAmountPaid;
    @FXML private TableColumn<Violation, String>   colStatus;
    
    @FXML private TextField searchField; // Filter list
    @FXML private Label lblTotalViolations;
    @FXML private Label lblPendingViolations;
    @FXML private Label lblPaidViolations;

    private final ObservableList<Violation> violationData = FXCollections.observableArrayList();
    private FilteredList<Violation> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Link columns
        colViolationID  .setCellValueFactory(new PropertyValueFactory<>("violationId"));
        colVehicleID    .setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        colViolationDate.setCellValueFactory(new PropertyValueFactory<>("violationDate"));
        colViolationType.setCellValueFactory(new PropertyValueFactory<>("violationType"));
        colFineAmount   .setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
        colAmountPaid   .setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        colStatus       .setCellValueFactory(new PropertyValueFactory<>("status"));

        // Live search
        filteredData = new FilteredList<>(violationData, p -> true);
        violationsTable.setItems(filteredData);

        loadViolations();
        setupStatusCellFactory(); // Colored badges
        
        // Search handler
        if (searchField != null) {
            searchField.textProperty().addListener((obs, old, nw) -> {
                filteredData.setPredicate(v -> {
                    if (nw == null || nw.isEmpty()) return true;
                    String lower = nw.toLowerCase();
                    return String.valueOf(v.getVehicleId()).contains(lower) ||
                           v.getViolationType().toLowerCase().contains(lower) ||
                           v.getStatus().toLowerCase().contains(lower);
                });
            });
        }
    }

    // Colors status badges
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
                    String style = UIUtils.BADGE_PENDING_STYLE; // Default red
                    
                    if ("PAID".equalsIgnoreCase(item)) {
                        style = UIUtils.BADGE_SUCCESS_STYLE;
                    } else if ("PARTIALLY PAID".equalsIgnoreCase(item)) {
                        style = UIUtils.BADGE_INFO_STYLE;
                    }
                    
                    badge.setStyle(style);
                    setGraphic(badge);
                }
            }
        });
    }

    // Load data
    private void loadViolations() {
        try {
            ViolationDAO dao = new ViolationDAO();
            violationData.setAll(dao.getAllViolations());
            
            // Update counts
            if (lblTotalViolations != null) {
                lblTotalViolations.setText(String.valueOf(violationData.size()));
            }
            if (lblPendingViolations != null) {
                long pending = violationData.stream()
                        .filter(v -> "Pending".equalsIgnoreCase(v.getStatus()) 
                                || "Unpaid".equalsIgnoreCase(v.getStatus())
                                || "Partially Paid".equalsIgnoreCase(v.getStatus()))
                        .count();
                lblPendingViolations.setText(String.valueOf(pending));
            }
            if (lblPaidViolations != null) {
                long paid = violationData.stream()
                        .filter(v -> "Paid".equalsIgnoreCase(v.getStatus()))
                        .count();
                lblPaidViolations.setText(String.valueOf(paid));
            }
        } catch (Exception e) {
            System.err.println("ViolationsController.loadViolations() error: " + e.getMessage());
        }
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        loadViolations();
    }

    // Record payment
    @FXML
    public void handleRecordPayment(ActionEvent event) {
        Violation selected = violationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Selection Required", null, "Please select a violation first!");
            return;
        }
        if ("PAID".equalsIgnoreCase(selected.getStatus())) {
            UIUtils.showAlert(Alert.AlertType.INFORMATION, "Already Paid", null, "This violation is already fully paid!");
            return;
        }

        double remaining = selected.getRemainingAmount();

        // Payment dialog
        TextInputDialog dialog = new TextInputDialog(String.valueOf(remaining));
        UIUtils.styleDialog(dialog, "Record Payment", "Enter payment amount for Violation #" + selected.getViolationId(), "fas-money-bill-wave");
        dialog.setContentText("Amount to pay (Remaining: M" + String.format("%.2f", remaining) + "):");

        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                // Validate
                if (amount <= 0) {
                    UIUtils.showAlert(Alert.AlertType.ERROR, "Invalid Amount", null, "Payment amount must be greater than zero.");
                    return;
                }
                if (amount > remaining) {
                    UIUtils.showAlert(Alert.AlertType.ERROR, "Overpayment", null, "Payment amount cannot exceed the remaining fine of M" + remaining);
                    return;
                }

                // Save
                if (new ViolationDAO().recordPayment(selected.getViolationId(), amount)) {
                    loadViolations();
                    UIUtils.showAlert(Alert.AlertType.INFORMATION, "Success", null, "Payment recorded successfully!");
                } else {
                    UIUtils.showAlert(Alert.AlertType.ERROR, "Error", null, "Failed to record payment.");
                }
            } catch (NumberFormatException e) {
                UIUtils.showAlert(Alert.AlertType.ERROR, "Invalid Input", null, "Please enter a valid numeric amount.");
            }
        });
    }

    @FXML
    public void handleMarkPaid(ActionEvent event) {
        // Use partial payment logic
        handleRecordPayment(event);
    }

    // Delete violation
    @FXML
    public void handleDeleteViolation(ActionEvent event) {
        Violation selected = violationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a violation to delete!").showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this violation?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText(null);
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (new ViolationDAO().deleteViolation(selected.getViolationId())) {
                    loadViolations();
                    new Alert(Alert.AlertType.INFORMATION, "Violation deleted successfully!").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete violation.").showAndWait();
                }
            }
        });
    }

    @FXML
    public void handleAddViolation(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        UIUtils.styleDialog(dialog, "Issue Violation", "Record a new traffic violation", "fas-exclamation-triangle");

        ComboBox<String> vehicleBox = new ComboBox<>();
        VehicleDAO.getAllVehicles().forEach(v -> 
            vehicleBox.getItems().add(v.getVehicleID() + " — " + v.getRegistrationNumber()));
        vehicleBox.setPromptText("Select Vehicle");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Violation Date");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Speeding", "Red Light", "Illegal Parking", "Reckless Driving", "Expired Disc", "Other");
        typeBox.setPromptText("Violation Type");

        TextField fineField = new TextField(); fineField.setPromptText("e.g. 500.00");
        UIUtils.applyNumberValidation(fineField);

        TextArea descArea = new TextArea(); descArea.setPromptText("Enter violation details/comments...");

        VBox form = UIUtils.createFormLayout();
        form.getChildren().addAll(
            UIUtils.createFieldGroup("Vehicle", vehicleBox, "fas-car"),
            UIUtils.createFieldGroup("Date", datePicker, "fas-calendar-day"),
            UIUtils.createFieldGroup("Type", typeBox, "fas-tag"),
            UIUtils.createFieldGroup("Fine Amount (M)", fineField, "fas-money-bill-wave"),
            UIUtils.createFieldGroup("Description/Comments", descArea, "fas-comment-alt")
        );

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        UIUtils.styleDialog(dialog, "Issue Violation", "Record a new traffic violation", "fas-exclamation-triangle");

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String vehicle = vehicleBox.getValue();
                    String type    = typeBox.getValue();
                    double fine    = Double.parseDouble(fineField.getText().trim());
                    String date    = datePicker.getValue() != null ?
                            datePicker.getValue().toString() : "";
                    String desc    = descArea.getText().trim();

                    if (vehicle == null || type == null || date.isEmpty()) {
                        new Alert(Alert.AlertType.WARNING, "Vehicle, Date and Type are required!").showAndWait();
                        return;
                    }

                    int vehicleId = Integer.parseInt(vehicle.split(" — ")[0]);
                    ViolationDAO dao = new ViolationDAO();
                    if (dao.addViolation(vehicleId, date, type, fine, desc)) {
                        loadViolations();
                        new Alert(Alert.AlertType.INFORMATION, "Violation issued!").showAndWait();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to issue violation.").showAndWait();
                    }
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.WARNING, "Fine amount must be a valid number!").showAndWait();
                }
            }
        });
    }
}
