package com.vis.controller;

import com.vis.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PolicePortalController implements Initializable {

    @FXML private Label lblOfficerName;
    @FXML private StackPane contentArea;
    @FXML private Button navFileReport;
    @FXML private Button navIssueViolation;
    @FXML private Button navSearchVehicle;
    @FXML private Button navAllReports;
    @FXML private Button navAllViolations;

    private User currentUser;
    private Button activeButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadView("PoliceReportsView.fxml");
        setActive(navFileReport);
    }

    public void setUser(User user) {
        currentUser = user;
        if (user != null) lblOfficerName.setText("Officer " + user.getUsername());
    }

    private void setActive(Button btn) {
        if (activeButton != null) activeButton.setStyle(com.vis.util.UIUtils.SIDEBAR_DEFAULT_STYLE);
        activeButton = btn;
        if (btn != null) btn.setStyle(com.vis.util.UIUtils.SIDEBAR_ACTIVE_RED_STYLE);
    }

    @FXML private void handleFileReport(ActionEvent e) { loadView("PoliceReportsView.fxml"); setActive(navFileReport); }
    @FXML private void handleIssueViolation(ActionEvent e) { loadView("ViolationsView.fxml"); setActive(navIssueViolation); }
    @FXML private void handleSearchVehicle(ActionEvent e) { loadView("VehiclesView.fxml"); setActive(navSearchVehicle); }
    @FXML private void handleAllReports(ActionEvent e) { loadView("PoliceReportsView.fxml"); setActive(navAllReports); }
    @FXML private void handleAllViolations(ActionEvent e) { loadView("ViolationsView.fxml"); setActive(navAllViolations); }

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

    private void loadView(String fxml) {
        try {
            URL url = getClass().getResource("/com/vis/fxml/" + fxml);
            if (url == null) { System.err.println("FXML not found: " + fxml); return; }
            Parent view = FXMLLoader.load(url);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}