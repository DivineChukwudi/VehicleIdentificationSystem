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

public class MechanicPortalController implements Initializable {

    @FXML private Label lblMechanicName;
    @FXML private StackPane contentArea;
    @FXML private Button navLogService;
    @FXML private Button navAllServiceRecords;
    @FXML private Button navSearchVehicle;

    private User currentUser;
    private Button activeButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadView("ServiceRecordsView.fxml");
        setActive(navLogService);
    }

    public void setUser(User user) {
        currentUser = user;
        if (user != null) lblMechanicName.setText(user.getUsername());
    }

    private void setActive(Button btn) {
        if (activeButton != null) activeButton.setStyle(com.vis.util.UIUtils.SIDEBAR_DEFAULT_STYLE);
        activeButton = btn;
        if (btn != null) btn.setStyle(com.vis.util.UIUtils.SIDEBAR_ACTIVE_PURPLE_STYLE);
    }

    @FXML private void handleLogService(ActionEvent e) { loadView("ServiceRecordsView.fxml"); setActive(navLogService); }
    @FXML private void handleAllServiceRecords(ActionEvent e) { loadView("ServiceRecordsView.fxml"); setActive(navAllServiceRecords); }
    @FXML private void handleSearchVehicle(ActionEvent e) { loadView("VehiclesView.fxml"); setActive(navSearchVehicle); }

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