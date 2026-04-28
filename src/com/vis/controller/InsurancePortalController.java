package com.vis.controller;

import com.vis.model.User;
import com.vis.util.UIUtils;
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

public class InsurancePortalController implements Initializable {

    @FXML private Label lblAgentName;
    @FXML private StackPane contentArea;
    @FXML private Button navAllPolicies;
    @FXML private Button navAddPolicy;

    private User currentUser;
    private Button activeButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadView("InsuranceListView.fxml");
        setActive(navAllPolicies);
    }

    public void setUser(User user) {
        currentUser = user;
        if (user != null) lblAgentName.setText("Agent " + user.getUsername());
    }

    private void setActive(Button btn) {
        if (activeButton != null) activeButton.setStyle(UIUtils.SIDEBAR_DEFAULT_STYLE);
        activeButton = btn;
        if (btn != null) btn.setStyle(UIUtils.SIDEBAR_ACTIVE_STYLE);
    }

    @FXML private void handleViewAllPolicies(ActionEvent e) { loadView("InsuranceListView.fxml"); setActive(navAllPolicies); }
    @FXML private void handleAddPolicy(ActionEvent e) { loadView("InsuranceAddView.fxml"); setActive(navAddPolicy); }

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
