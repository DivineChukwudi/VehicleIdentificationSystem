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

    @FXML private TableView<Violation>             violationsTable;
    @FXML private TableColumn<Violation, Integer>  colViolationID;
    @FXML private TableColumn<Violation, String>   colVehicleID;
    @FXML private TableColumn<Violation, String>   colViolationDate;
    @FXML private TableColumn<Violation, String>   colViolationType;
    @FXML private TableColumn<Violation, Double>   colFineAmount;
    @FXML private TableColumn<Violation, String>   colStatus;
    @FXML private TextField searchField;
    @FXML private Label lblTotalViolations;
    @FXML private Label lblPendingViolations;
    @FXML private Label lblPaidViolations;

    private final ObservableList<Violation> violationData = FXCollections.observableArrayList();
    private FilteredList<Violation> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colViolationID  .setCellValueFactory(new PropertyValueFactory<>("violationId"));
        colVehicleID    .setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        colViolationDate.setCellValueFactory(new PropertyValueFactory<>("violationDate"));
        colViolationType.setCellValueFactory(new PropertyValueFactory<>("violationType"));
        colFineAmount   .setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
        colStatus       .setCellValueFactory(new PropertyValueFactory<>("status"));

        filteredData = new FilteredList<>(violationData, p -> true);
        violationsTable.setItems(filteredData);

        loadViolations();
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

    private void loadViolations() {
        try {
            ViolationDAO dao = new ViolationDAO();
            violationData.setAll(dao.getAllViolations());
            
            if (lblTotalViolations != null) {
                lblTotalViolations.setText(String.valueOf(violationData.size()));
            }
            if (lblPendingViolations != null) {
                long pending = violationData.stream()
                        .filter(v -> "Pending".equalsIgnoreCase(v.getStatus()) || "Unpaid".equalsIgnoreCase(v.getStatus()))
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

    @FXML
    public void handleMarkPaid(ActionEvent event) {
        Violation selected = violationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a violation first!").showAndWait();
            return;
        }
        if ("PAID".equalsIgnoreCase(selected.getStatus())) {
            new Alert(Alert.AlertType.INFORMATION, "This violation is already paid!").showAndWait();
            return;
        }
        new ViolationDAO().markAsPaid(selected.getViolationId());
        loadViolations();
        new Alert(Alert.AlertType.INFORMATION, "Violation marked as paid!").showAndWait();
    }

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
