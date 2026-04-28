package com.vis.controller;

import com.vis.db.ServiceDAO;
import com.vis.db.VehicleDAO;
import com.vis.model.Service;
import com.vis.util.UIUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ServiceRecordsController implements Initializable {

    @FXML private TableView<Service> serviceTable;
    @FXML private TableColumn<Service, Integer> colServiceID;
    @FXML private TableColumn<Service, String> colVehicleID;
    @FXML private TableColumn<Service, String> colServiceDate;
    @FXML private TableColumn<Service, String> colServiceType;
    @FXML private TableColumn<Service, String> colDescription;
    @FXML private TableColumn<Service, Double> colCost;
    @FXML private TextField searchField;
    @FXML private Label lblTotalServices;
    @FXML private Label lblTotalCost;

    private final ObservableList<Service> serviceData = FXCollections.observableArrayList();
    private FilteredList<Service> filteredData;

    public void initialize(URL url, ResourceBundle rb) {
        colServiceID.setCellValueFactory(new PropertyValueFactory<>("serviceID"));
        colVehicleID.setCellValueFactory(new PropertyValueFactory<>("vehicleID"));
        colServiceDate.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));
        colServiceType.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

        filteredData = new FilteredList<>(serviceData, p -> true);
        serviceTable.setItems(filteredData);

        loadServices();
        if (searchField != null) {
            searchField.textProperty().addListener((obs, old, nw) -> {
                filteredData.setPredicate(s -> {
                    if (nw == null || nw.isEmpty()) return true;
                    String lower = nw.toLowerCase();
                    return String.valueOf(s.getVehicleID()).contains(lower) ||
                           s.getServiceType().toLowerCase().contains(lower) ||
                           s.getDescription().toLowerCase().contains(lower);
                });
            });
        }
    }

    private void loadServices() {
        ServiceDAO dao = new ServiceDAO();
        serviceData.setAll(dao.getAllServices());

        if (lblTotalServices != null) {
            lblTotalServices.setText(String.valueOf(serviceData.size()));
        }
        if (lblTotalCost != null) {
            double total = serviceData.stream().mapToDouble(Service::getCost).sum();
            lblTotalCost.setText(String.format("M %.2f", total));
        }
    }

    @FXML
    public void handleRefresh(javafx.event.ActionEvent event) {
        loadServices();
    }


    @FXML
    public void handleAddRecord(javafx.event.ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        UIUtils.styleDialog(dialog, "Log Service", "Record vehicle maintenance", "fas-tools");

        ComboBox<String> vehicleBox = new ComboBox<>();
        VehicleDAO.getAllVehicles().forEach(v -> 
            vehicleBox.getItems().add(v.getVehicleID() + " — " + v.getRegistrationNumber()));
        vehicleBox.setPromptText("Select Vehicle");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Service Date");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Oil Change", "Tyre Replacement", "Brake Service",
                "Engine Repair", "Suspension", "Electrical", "Body Work", "Other");
        typeBox.setPromptText("Service Type");

        TextArea descArea = new TextArea(); descArea.setPromptText("Enter service details...");
        TextField costField = new TextField(); costField.setPromptText("e.g. 1200.00");
        UIUtils.applyNumberValidation(costField);

        VBox form = UIUtils.createFormLayout();
        form.getChildren().addAll(
            UIUtils.createFieldGroup("Vehicle", vehicleBox, "fas-car"),
            UIUtils.createFieldGroup("Date", datePicker, "fas-calendar-day"),
            UIUtils.createFieldGroup("Service Type", typeBox, "fas-wrench"),
            UIUtils.createFieldGroup("Cost (M)", costField, "fas-money-bill-wave"),
            UIUtils.createFieldGroup("Description/Notes", descArea, "fas-comment-alt")
        );

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String vehicle = vehicleBox.getValue();
                    String type    = typeBox.getValue();
                    String date    = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
                    String desc    = descArea.getText().trim();
                    String costStr = costField.getText().trim();

                    if (vehicle == null || type == null || date.isEmpty() || costStr.isEmpty()) {
                        new Alert(Alert.AlertType.WARNING, "All fields are required!").showAndWait();
                        return;
                    }

                    int vehicleId = Integer.parseInt(vehicle.split(" — ")[0]);
                    double cost   = Double.parseDouble(costStr);

                    ServiceDAO dao = new ServiceDAO();
                    if (dao.addService(vehicleId, date, type, desc, cost)) {
                        loadServices();
                        new Alert(Alert.AlertType.INFORMATION, "Service record added!").showAndWait();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to add service record.").showAndWait();
                    }
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.WARNING, "Invalid cost value!").showAndWait();
                }
            }
        });
    }
}