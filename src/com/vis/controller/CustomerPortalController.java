package com.vis.controller;

import com.vis.db.UserDAO;
import com.vis.model.User;
import com.vis.util.UIUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerPortalController implements Initializable {

    // UI elements
    @FXML private Label lblCustomerName;
    @FXML private StackPane contentArea; 
    @FXML private Button navMyVehicles;
    @FXML private Button navMyViolations;
    @FXML private Button navMyServiceRecords;
    @FXML private Button navMyProfile;

    private User currentUser;
    private Button activeButton; 
    private int customerId = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    // Injects user
    public void setUser(User user) {
        currentUser = user;
        if (user != null) {
            lblCustomerName.setText("Welcome, " + user.getUsername());
            customerId = new UserDAO().getCustomerId(user.getUserID());
            checkViolations(); 
            handleVehicles(null); 
            setActive(navMyVehicles);
        }
    }

    // Check for fines
    private void checkViolations() {
        if (customerId == -1) return;
        List<String> violations = new UserDAO().getUnpaidViolations(customerId);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("You have unpaid violations:\n\n");
            for (String v : violations) sb.append("• ").append(v).append("\n");
            sb.append("\nPlease contact an officer or admin to resolve them.");
            
            UIUtils.showAlert(Alert.AlertType.WARNING, "Unpaid Violations", 
                    "Outstanding Violations on Your Vehicle(s)", sb.toString());
        }
    }

    // Sidebar highlight
    private void setActive(Button btn) {
        if (activeButton != null) activeButton.setStyle(UIUtils.SIDEBAR_DEFAULT_STYLE);
        activeButton = btn;
        if (btn != null) btn.setStyle(UIUtils.SIDEBAR_ACTIVE_STYLE);
    }

    public int getCustomerId() { return customerId; }

    // Load Vehicles
    @FXML private void handleVehicles(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vis/fxml/CustomerVehiclesView.fxml"));
            Parent view = loader.load();
            CustomerVehiclesController ctrl = loader.getController();
            ctrl.setCustomerId(customerId);
            contentArea.getChildren().setAll(view);
            setActive(navMyVehicles);
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    // Load Violations
    @FXML private void handleViolations(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vis/fxml/CustomerViolationsView.fxml"));
            Parent view = loader.load();
            CustomerViolationsController ctrl = loader.getController();
            ctrl.setCustomerId(customerId);
            contentArea.getChildren().setAll(view);
            setActive(navMyViolations);
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    // Load Service Records
    @FXML private void handleServiceRecords(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vis/fxml/CustomerServiceView.fxml"));
            Parent view = loader.load();
            CustomerServiceController ctrl = loader.getController();
            ctrl.setCustomerId(customerId);
            contentArea.getChildren().setAll(view);
            setActive(navMyServiceRecords);
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    // Load Profile
    @FXML private void handleProfile(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vis/fxml/CustomerProfileView.fxml"));
            Parent view = loader.load();
            CustomerProfileController ctrl = loader.getController();
            ctrl.setCustomerId(customerId);
            contentArea.getChildren().setAll(view);
            setActive(navMyProfile);
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/vis/fxml/LoginView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}