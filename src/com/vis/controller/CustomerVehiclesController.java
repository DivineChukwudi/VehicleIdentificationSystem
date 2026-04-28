package com.vis.controller;

import com.vis.db.VehicleDAO;
import com.vis.model.Vehicle;
import com.vis.util.UIUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerVehiclesController implements Initializable {

    @FXML private TableView<Vehicle> vehiclesTable;
    @FXML private TableColumn<Vehicle, Integer> colVehicleID;
    @FXML private TableColumn<Vehicle, String> colRegistrationNumber;
    @FXML private TableColumn<Vehicle, String> colMake;
    @FXML private TableColumn<Vehicle, String> colModel;
    @FXML private TableColumn<Vehicle, Integer> colYear;
    @FXML private TextField searchField;

    private int customerId = -1;
    private ObservableList<Vehicle> vehicleData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colVehicleID.setCellValueFactory(new PropertyValueFactory<>("vehicleID"));
        colRegistrationNumber.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        colMake.setCellValueFactory(new PropertyValueFactory<>("make"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));

        if (searchField != null) {
            searchField.textProperty().addListener((obs, old, nw) -> filterData(nw));
        }
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
        loadVehicles();
    }

    private void loadVehicles() {
        if (customerId == -1) return;
        VehicleDAO dao = new VehicleDAO();
        vehicleData.setAll(dao.getVehiclesByCustomer(customerId));
        vehiclesTable.setItems(vehicleData);
    }

    private void filterData(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            vehiclesTable.setItems(vehicleData);
            return;
        }
        String lower = keyword.toLowerCase();
        ObservableList<Vehicle> filtered = FXCollections.observableArrayList();
        for (Vehicle v : vehicleData) {
            if (v.getRegistrationNumber().toLowerCase().contains(lower) ||
                    v.getMake().toLowerCase().contains(lower) ||
                    v.getModel().toLowerCase().contains(lower)) {
                filtered.add(v);
            }
        }
        vehiclesTable.setItems(filtered);
    }

    @FXML
    public void handleAddVehicle(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        UIUtils.styleDialog(dialog, "Register Vehicle", "Add a new vehicle to your profile", "fas-car");

        TextField regField   = new TextField(); regField.setPromptText("e.g. ABC-123-GP");
        TextField makeField  = new TextField(); makeField.setPromptText("e.g. Toyota");
        TextField modelField = new TextField(); modelField.setPromptText("e.g. Hilux");

        ComboBox<Integer> yearBox = new ComboBox<>();
        for (int y = 2025; y >= 1990; y--) yearBox.getItems().add(y);
        yearBox.setPromptText("Select Year");

        VBox form = UIUtils.createFormLayout();
        form.getChildren().addAll(
            UIUtils.createFieldGroup("Registration Number", regField),
            UIUtils.createFieldGroup("Make", makeField),
            UIUtils.createFieldGroup("Model", modelField),
            UIUtils.createFieldGroup("Year", yearBox)
        );

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        UIUtils.styleDialog(dialog, "Register Vehicle", "Add a new vehicle to your profile", "fas-car"); // Re-apply to style buttons

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String reg   = regField.getText().trim();
                    String make  = makeField.getText().trim();
                    String model = modelField.getText().trim();
                    Integer year = yearBox.getValue();

                    if (reg.isEmpty() || make.isEmpty() || model.isEmpty() || year == null) {
                        new Alert(Alert.AlertType.WARNING, "All fields are required!").showAndWait();
                        return;
                    }

                    VehicleDAO dao = new VehicleDAO();
                    if (dao.addVehicle(reg, make, model, year, customerId)) {
                        loadVehicles();
                        new Alert(Alert.AlertType.INFORMATION, "Vehicle registered successfully!").showAndWait();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to register vehicle.").showAndWait();
                    }
                } catch (Exception e) {
                    new Alert(Alert.AlertType.WARNING, "Please fill all fields correctly!").showAndWait();
                }
            }
        });
    }
}