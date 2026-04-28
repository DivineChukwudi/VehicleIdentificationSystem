package com.vis.controller;

import com.vis.db.PoliceReportDAO;
import com.vis.db.VehicleDAO;
import com.vis.model.PoliceReport;
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

public class PoliceReportsController implements Initializable {

    @FXML private TableView<PoliceReport>             policeTable;
    @FXML private TableColumn<PoliceReport, Integer>  colReportID;
    @FXML private TableColumn<PoliceReport, String>   colVehicleID;
    @FXML private TableColumn<PoliceReport, String>   colReportDate;
    @FXML private TableColumn<PoliceReport, String>   colReportType;
    @FXML private TableColumn<PoliceReport, String>   colDescription;
    @FXML private TableColumn<PoliceReport, String>   colOfficerName;
    @FXML private TextField searchField;
    @FXML private Label lblTotalReports;
    @FXML private Label lblActiveOfficers;

    private final ObservableList<PoliceReport> reportData = FXCollections.observableArrayList();
    private FilteredList<PoliceReport> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colReportID  .setCellValueFactory(new PropertyValueFactory<>("reportID"));
        colVehicleID .setCellValueFactory(new PropertyValueFactory<>("vehicleID"));
        colReportDate.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        colReportType.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colOfficerName.setCellValueFactory(new PropertyValueFactory<>("officerName"));

        filteredData = new FilteredList<>(reportData, p -> true);
        policeTable.setItems(filteredData);

        loadReports();
        if (searchField != null) {
            searchField.textProperty().addListener((obs, old, nw) -> {
                filteredData.setPredicate(r -> {
                    if (nw == null || nw.isEmpty()) return true;
                    String lower = nw.toLowerCase();
                    return String.valueOf(r.getVehicleID()).contains(lower) ||
                           r.getOfficerName().toLowerCase().contains(lower) ||
                           r.getReportType().toLowerCase().contains(lower);
                });
            });
        }
    }

    private void loadReports() {
        try {
            PoliceReportDAO dao = new PoliceReportDAO();
            reportData.setAll(dao.getAllReports());
            
            if (lblTotalReports != null) {
                lblTotalReports.setText(String.valueOf(reportData.size()));
            }
            if (lblActiveOfficers != null) {
                long distinctOfficers = reportData.stream()
                        .map(PoliceReport::getOfficerName)
                        .distinct()
                        .count();
                lblActiveOfficers.setText(String.valueOf(distinctOfficers));
            }
        } catch (Exception e) {
            System.err.println("PoliceReportsController.loadReports() error: " + e.getMessage());
        }
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        loadReports();
    }

    @FXML
    public void handleAddReport(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        UIUtils.styleDialog(dialog, "File Report", "Create a new incident report", "fas-file-alt");

        ComboBox<String> vehicleBox = new ComboBox<>();
        VehicleDAO.getAllVehicles().forEach(v -> 
            vehicleBox.getItems().add(v.getVehicleID() + " — " + v.getRegistrationNumber()));
        vehicleBox.setPromptText("Select Vehicle");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Report Date");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Accident", "Theft", "Vandalism", "Hit and Run", "Other");
        typeBox.setPromptText("Report Type");

        TextArea descArea = new TextArea(); descArea.setPromptText("Enter report details...");
        TextField officerField = new TextField(); officerField.setPromptText("Officer Name");
        
        // Apply Validation
        UIUtils.applyLetterValidation(officerField);

        VBox form = UIUtils.createFormLayout();
        form.getChildren().addAll(
            UIUtils.createFieldGroup("Vehicle", vehicleBox, "fas-car"),
            UIUtils.createFieldGroup("Date", datePicker, "fas-calendar-day"),
            UIUtils.createFieldGroup("Type", typeBox, "fas-tag"),
            UIUtils.createFieldGroup("Description", descArea, "fas-comment-alt"),
            UIUtils.createFieldGroup("Reporting Officer", officerField, "fas-user-shield")
        );

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        UIUtils.styleDialog(dialog, "File Report", "Create a new incident report", "fas-file-alt");

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String vehicle = vehicleBox.getValue();
                    String type    = typeBox.getValue();
                    String date    = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
                    String desc    = descArea.getText().trim();
                    String officer = officerField.getText().trim();

                    if (vehicle == null || type == null || date.isEmpty() || desc.isEmpty() || officer.isEmpty()) {
                        new Alert(Alert.AlertType.WARNING, "All fields are required!").showAndWait();
                        return;
                    }

                    int vehicleId = Integer.parseInt(vehicle.split(" — ")[0]);
                    PoliceReportDAO dao = new PoliceReportDAO();
                    if (dao.addReport(vehicleId, date, type, desc, officer)) {
                        loadReports();
                        new Alert(Alert.AlertType.INFORMATION, "Report filed!").showAndWait();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to file report.").showAndWait();
                    }
                } catch (Exception e) {
                    new Alert(Alert.AlertType.WARNING, "Please fill all fields correctly!").showAndWait();
                }
            }
        });
    }
}
