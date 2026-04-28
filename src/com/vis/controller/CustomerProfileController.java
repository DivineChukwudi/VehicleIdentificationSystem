package com.vis.controller;


import com.vis.db.CustomerDAO;
import com.vis.model.Customer;
import com.vis.util.UIUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerProfileController implements Initializable {

    @FXML private TextField fieldFirstName;
    @FXML private TextField fieldSurname;
    @FXML private TextField fieldEmail;
    @FXML private TextField fieldPhone;
    @FXML private TextField fieldAddress;

    private int currentCustomerId = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        UIUtils.applyLetterValidation(fieldFirstName);
        UIUtils.applyLetterValidation(fieldSurname);
        UIUtils.applyDigitValidation(fieldPhone);
    }

    public void setCustomerId(int id) {
        this.currentCustomerId = id;
        loadCustomerData();
    }

    private void loadCustomerData() {
        if (currentCustomerId == -1) return;
        com.vis.model.Customer c = new com.vis.db.CustomerDAO().getCustomerById(currentCustomerId);
        if (c != null) {
            fieldFirstName.setText(c.getName());
            fieldSurname.setText(c.getSurname());
            fieldEmail.setText(c.getEmail());
            fieldPhone.setText(c.getPhone());
            fieldAddress.setText(c.getAddress());
        }
    }

    @FXML
    private void handleSaveProfile() {
        if (currentCustomerId == -1) return;
        
        String name = fieldFirstName.getText();
        String surname = fieldSurname.getText();
        String email = fieldEmail.getText();
        String phone = fieldPhone.getText();
        String address = fieldAddress.getText();
        
        com.vis.model.Customer c = new com.vis.model.Customer(currentCustomerId, name, surname, address, phone, email);
        boolean success = new com.vis.db.CustomerDAO().updateCustomer(c);
        
        if (success) {
            com.vis.util.UIUtils.showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Success", "Profile Updated", "Your profile has been updated successfully.");
        } else {
            com.vis.util.UIUtils.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "Update Failed", "Could not update profile. Please try again.");
        }
    }
}