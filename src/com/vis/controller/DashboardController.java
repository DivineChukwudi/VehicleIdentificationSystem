package com.vis.controller;

import com.vis.model.User;
import com.vis.util.UIUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    
    private static DashboardController instance;
    
    public static DashboardController getInstance() {
        return instance;
    }

    @FXML private Label lblUsername;
    @FXML private Label lblRole;
    @FXML private StackPane contentArea;
    @FXML private Button btnDashboard;
    @FXML private Button btnVehicles;
    @FXML private Button btnCustomers;
    @FXML private Button btnPoliceReports;
    @FXML private Button btnServiceRecords;
    @FXML private Button btnViolations;
    @FXML private Button btnPagination;

    private Button activeButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        loadView("DashboardStatsView.fxml");
        setActive(btnDashboard);
    }

    public void setActive(Button btn) {
        if (activeButton != null) activeButton.setStyle(UIUtils.SIDEBAR_DEFAULT_STYLE + "-fx-padding: 8 14;");
        activeButton = btn;
        if (btn != null) btn.setStyle(UIUtils.SIDEBAR_ACTIVE_STYLE + "-fx-padding: 8 14; -fx-font-weight: bold;");
    }

    public void navigateTo(String view) {
        navigateTo(view, null);
    }

    public void navigateTo(String view, String action) {
        switch (view) {
            case "Vehicles": 
                loadView("VehiclesView.fxml", (VehiclesController ctrl) -> {
                    if ("add".equals(action)) ctrl.handleAddVehicle(null);
                }); 
                setActive(btnVehicles); 
                break;
            case "Customers": 
                loadView("CustomersView.fxml", (CustomersController ctrl) -> {
                    if ("add".equals(action)) ctrl.handleAddCustomer(null);
                }); 
                setActive(btnCustomers); 
                break;
            case "PoliceReports": 
                loadView("PoliceReportsView.fxml", (PoliceReportsController ctrl) -> {
                    if ("add".equals(action)) ctrl.handleAddReport(null);
                }); 
                setActive(btnPoliceReports); 
                break;
            case "ServiceRecords": 
                loadView("ServiceRecordsView.fxml", (ServiceRecordsController ctrl) -> {
                    if ("add".equals(action)) ctrl.handleAddRecord(null);
                }); 
                setActive(btnServiceRecords); 
                break;
            case "Violations": 
                loadView("ViolationsView.fxml", (ViolationsController ctrl) -> {
                    if ("add".equals(action)) ctrl.handleAddViolation(null);
                }); 
                setActive(btnViolations); 
                break;
            case "Dashboard": handleDashboard(null); break;
        }
    }

    @FXML public void handleDashboard(ActionEvent event) { loadView("DashboardStatsView.fxml"); setActive(btnDashboard); }
    @FXML public void handleVehicles(ActionEvent event) { loadView("VehiclesView.fxml"); setActive(btnVehicles); }
    @FXML public void handleCustomers(ActionEvent event) { loadView("CustomersView.fxml"); setActive(btnCustomers); }
    @FXML public void handlePoliceReports(ActionEvent event) { loadView("PoliceReportsView.fxml"); setActive(btnPoliceReports); }
    @FXML public void handleServiceRecords(ActionEvent event) { loadView("ServiceRecordsView.fxml"); setActive(btnServiceRecords); }
    @FXML public void handleViolations(ActionEvent event) { loadView("ViolationsView.fxml"); setActive(btnViolations); }
    @FXML public void handlePagination(ActionEvent event) { loadView("PaginationView.fxml"); setActive(btnPagination); }

    @FXML private void handleExit(ActionEvent event) { Platform.exit(); }

    @FXML
    private void handleAbout(ActionEvent event) {
        UIUtils.showAlert(Alert.AlertType.INFORMATION, "About VIS", "Vehicle Identification System", 
                "Version: 1.0\nCourse: OOP II (B/DIOP2210)\nStack: JavaFX + PostgreSQL");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/vis/fxml/LoginView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            System.err.println("Error switching to Login View: " + e.getMessage());
        }
    }

    public void setUser(User user) {
        if (user != null) {
            lblUsername.setText("Welcome, " + user.getUsername());
            lblRole.setText("Role: " + user.getRole());
        }
    }

    private void loadView(String fxmlFile) {
        loadView(fxmlFile, null);
    }

    private <T> void loadView(String fxmlFile, java.util.function.Consumer<T> ctrlCallback) {
        try {
            URL fxmlUrl = getClass().getResource("/com/vis/fxml/" + fxmlFile);
            if (fxmlUrl == null) { System.err.println("Cannot find FXML: " + fxmlFile); return; }
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent fxml = loader.load();
            
            if (ctrlCallback != null) {
                T controller = loader.getController();
                ctrlCallback.accept(controller);
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}