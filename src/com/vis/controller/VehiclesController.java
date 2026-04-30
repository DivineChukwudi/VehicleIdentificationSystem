package com.vis.controller;

import com.vis.db.VehicleDAO;
import com.vis.db.ViolationDAO;
import com.vis.model.Car;
import com.vis.model.Truck;
import com.vis.model.Vehicle;
import com.vis.util.UIUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import java.net.URL;
import java.util.ResourceBundle;

public class VehiclesController implements Initializable {

    @FXML private TextField                      searchField;
    @FXML private TableView<Vehicle>             vehiclesTable;
    @FXML private TableColumn<Vehicle, Integer>  colVehicleID;
    @FXML private TableColumn<Vehicle, String>   colRegistrationNumber;
    @FXML private TableColumn<Vehicle, String>   colMake;
    @FXML private TableColumn<Vehicle, String>   colModel;
    @FXML private TableColumn<Vehicle, Integer>  colYear;
    @FXML private TableColumn<Vehicle, Integer>  colOwnerID;
    @FXML private TableColumn<Vehicle, Void>     colActions;
    @FXML private Label lblTotalVehicles;
    @FXML private Label lblRegistered;
    @FXML private Label lblWithViolations;

    private final ObservableList<Vehicle> vehicleData = FXCollections.observableArrayList();
    private FilteredList<Vehicle> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colVehicleID         .setCellValueFactory(new PropertyValueFactory<>("vehicleID"));
        colRegistrationNumber.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        colMake              .setCellValueFactory(new PropertyValueFactory<>("make"));
        colModel             .setCellValueFactory(new PropertyValueFactory<>("model"));
        colYear              .setCellValueFactory(new PropertyValueFactory<>("year"));
        colOwnerID           .setCellValueFactory(new PropertyValueFactory<>("ownerID"));

        if (colActions != null) {
            colActions.setCellFactory(col -> new TableCell<Vehicle, Void>() {
                private final Button btnDetails = new Button("Details");
                private final Button btnLink = new Button("Link Owner");
                private final HBox container = new HBox(8, btnDetails, btnLink);

                {
                    btnDetails.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                            "-fx-cursor: hand; -fx-background-radius: 4; -fx-font-size: 11;");
                    btnDetails.setOnAction(e -> {
                        Vehicle v = getTableView().getItems().get(getIndex());
                        showDetails(v);
                    });

                    btnLink.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; " +
                            "-fx-cursor: hand; -fx-background-radius: 4; -fx-font-size: 11;");
                    btnLink.setOnAction(e -> {
                        Vehicle v = getTableView().getItems().get(getIndex());
                        handleLinkCustomer(v);
                    });
                    
                    container.setAlignment(Pos.CENTER);
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : container);
                }
            });
        }

        filteredData = new FilteredList<>(vehicleData, p -> true);
        vehiclesTable.setItems(filteredData);

        loadVehicles();

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredData.setPredicate(v -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    String lower = newVal.toLowerCase();
                    return v.getRegistrationNumber().toLowerCase().contains(lower) ||
                           v.getModel().toLowerCase().contains(lower) ||
                           v.getMake().toLowerCase().contains(lower);
                });
            });
        }

        demonstratePolymorphism();
    }

    private void loadVehicles() {
        try {
            vehicleData.setAll(VehicleDAO.getAllVehicles());

            int total = vehicleData.size();
            long withViolations = new ViolationDAO().getAllViolations()
                    .stream().mapToInt(v -> v.getVehicleId()).distinct().count();

            if (lblTotalVehicles  != null) lblTotalVehicles .setText(String.valueOf(total));
            if (lblRegistered     != null) lblRegistered    .setText(String.valueOf(total));
            if (lblWithViolations != null) lblWithViolations.setText(String.valueOf(withViolations));

        } catch (Exception e) {
            System.err.println("VehiclesController.loadVehicles() error: " + e.getMessage());
        }
    }

    private void demonstratePolymorphism() {
        Vehicle[] fleet = {
                new Vehicle(0, "DEMO-001", "Toyota",  "Corolla", 2020, 0),
                new Car    (0, "DEMO-002", "BMW",      "3 Series", 2022, 0, 4),
                new Truck  (0, "DEMO-003", "Volvo",    "FH16",     2021, 0, 20.0)
        };

    }

    private void showDetails(Vehicle v) {
        Dialog<Void> dialog = new Dialog<>();
        UIUtils.styleDialog(dialog, "Vehicle Details", "Viewing information for " + v.getRegistrationNumber(), "fas-info-circle");

        VBox content = UIUtils.createFormLayout();
        
        Label detailsLabel = new Label(v.getDetails());
        detailsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-line-spacing: 5;");
        detailsLabel.setWrapText(true);
        
        content.getChildren().add(detailsLabel);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        UIUtils.styleDialog(dialog, "Vehicle Details", "Viewing information for " + v.getRegistrationNumber(), "fas-info-circle");
        
        dialog.showAndWait();
    }

    @FXML
    public void handleAddVehicle(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        UIUtils.styleDialog(dialog, "Add New Vehicle", "Register a new vehicle in the system", "fas-car");

        TextField regField   = new TextField(); regField.setPromptText("Registration Number");
        TextField makeField  = new TextField(); makeField.setPromptText("Make (e.g. Toyota)");
        TextField modelField = new TextField(); modelField.setPromptText("Model (e.g. Hilux)");

        // Apply Validation
        UIUtils.applyLetterValidation(makeField);
        UIUtils.applyLetterValidation(modelField);

        ComboBox<Integer> yearBox = new ComboBox<>();
        for (int y = 2025; y >= 1990; y--) yearBox.getItems().add(y);
        yearBox.setPromptText("Select Year");

        ComboBox<String> ownerBox = new ComboBox<>();
        new com.vis.db.CustomerDAO().getAllCustomers()
                .forEach(c -> ownerBox.getItems().add(c.getCustomerID() + " — " + c.getName() + " " + c.getSurname()));
        ownerBox.setPromptText("Select Owner");

        VBox form = UIUtils.createFormLayout();
        form.getChildren().addAll(
            UIUtils.createFieldGroup("Registration Number", regField, "fas-id-card"),
            UIUtils.createFieldGroup("Make", makeField, "fas-industry"),
            UIUtils.createFieldGroup("Model", modelField, "fas-car-side"),
            UIUtils.createFieldGroup("Year", yearBox, "fas-calendar-alt"),
            UIUtils.createFieldGroup("Owner", ownerBox, "fas-user-tie")
        );

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        UIUtils.styleDialog(dialog, "Add New Vehicle", "Register a new vehicle in the system", "fas-car");

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String reg    = regField.getText().trim();
                    String make   = makeField.getText().trim();
                    String model  = modelField.getText().trim();
                    Integer year  = yearBox.getValue();
                    String owner  = ownerBox.getValue();

                    if (reg.isEmpty() || make.isEmpty() || model.isEmpty() || year == null || owner == null) {
                        new Alert(Alert.AlertType.WARNING, "All fields are required!").showAndWait();
                        return;
                    }

                    int ownerID = Integer.parseInt(owner.split(" — ")[0]);
                    VehicleDAO dao = new VehicleDAO();
                    if (dao.addVehicle(reg, make, model, year, ownerID)) {
                        loadVehicles();
                        new Alert(Alert.AlertType.INFORMATION, "Vehicle added successfully!").showAndWait();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to add vehicle.").showAndWait();
                    }
                } catch (Exception e) {
                    new Alert(Alert.AlertType.WARNING, "Please fill all fields correctly!").showAndWait();
                }
            }
        });
    }

    private void handleLinkCustomer(Vehicle v) {
        Dialog<ButtonType> dialog = new Dialog<>();
        UIUtils.styleDialog(dialog, "Link Customer", "Assign a customer to vehicle " + v.getRegistrationNumber(), "fas-link");

        ComboBox<String> ownerBox = new ComboBox<>();
        new com.vis.db.CustomerDAO().getAllCustomers()
                .forEach(c -> ownerBox.getItems().add(c.getCustomerID() + " — " + c.getName() + " " + c.getSurname()));
        ownerBox.setPromptText("Select New Owner");
        
        // Pre-select current owner if possible
        int currentOwnerID = v.getOwnerID();
        if (currentOwnerID > 0) {
            for (String item : ownerBox.getItems()) {
                if (item.startsWith(currentOwnerID + " — ")) {
                    ownerBox.setValue(item);
                    break;
                }
            }
        }

        VBox form = UIUtils.createFormLayout();
        form.getChildren().add(UIUtils.createFieldGroup("New Owner", ownerBox, "fas-user-tie"));

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String selected = ownerBox.getValue();
                if (selected == null) {
                    new Alert(Alert.AlertType.WARNING, "Please select a customer!").showAndWait();
                    return;
                }

                int newOwnerID = Integer.parseInt(selected.split(" — ")[0]);
                if (new VehicleDAO().updateVehicleOwner(v.getVehicleID(), newOwnerID)) {
                    loadVehicles();
                    new Alert(Alert.AlertType.INFORMATION, "Customer linked successfully!").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to link customer.").showAndWait();
                }
            }
        });
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        if (searchField != null) searchField.clear();
        loadVehicles();
    }
}