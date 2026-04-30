package com.vis.controller;

import com.vis.db.CustomerDAO;
import com.vis.model.Customer;
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

public class CustomersController implements Initializable {

    @FXML private TextField                        searchField;
    @FXML private TableView<Customer>              customersTable;
    @FXML private TableColumn<Customer, Integer>   colCustomerID;
    @FXML private TableColumn<Customer, String>    colName;
    @FXML private TableColumn<Customer, String>    colSurname;
    @FXML private TableColumn<Customer, String>    colAddress;
    @FXML private TableColumn<Customer, String>    colPhone;
    @FXML private TableColumn<Customer, String>    colEmail;
    @FXML private Label                            lblTotalCustomers;
    @FXML private Label                            lblActiveCustomers;

    private final ObservableList<Customer> customerData = FXCollections.observableArrayList();
    private FilteredList<Customer> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        colName      .setCellValueFactory(new PropertyValueFactory<>("name"));
        colSurname   .setCellValueFactory(new PropertyValueFactory<>("surname"));
        colAddress   .setCellValueFactory(new PropertyValueFactory<>("address"));
        colPhone     .setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail     .setCellValueFactory(new PropertyValueFactory<>("email"));

        filteredData = new FilteredList<>(customerData, p -> true);
        customersTable.setItems(filteredData);

        loadCustomers();

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredData.setPredicate(c -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    String lower = newVal.toLowerCase();
                    return c.getName()   .toLowerCase().contains(lower) ||
                           c.getSurname().toLowerCase().contains(lower) ||
                           c.getEmail()  .toLowerCase().contains(lower) ||
                           c.getPhone()  .toLowerCase().contains(lower);
                });
            });
        }
    }

    private void loadCustomers() {
        try {
            CustomerDAO dao = new CustomerDAO();
            customerData.setAll(dao.getAllCustomers());
            
            if (lblTotalCustomers != null) {
                lblTotalCustomers.setText(String.valueOf(customerData.size()));
            }
            if (lblActiveCustomers != null) {
                // For now just show the same count as active
                lblActiveCustomers.setText(String.valueOf(customerData.size()));
            }
        } catch (Exception e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        loadCustomers();
    }

    @FXML
    public void handleAssignLogin(ActionEvent event) {
        Customer selected = customersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a customer first!").showAndWait();
            return;
        }

        com.vis.db.UserDAO userDAO = new com.vis.db.UserDAO();
        if (userDAO.hasUserAccount(selected.getCustomerID())) {
            new Alert(Alert.AlertType.INFORMATION, "This customer already has a login account.").showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        UIUtils.styleDialog(dialog, "Assign Login", "Create credentials for " + selected.getName(), "fas-key");

        TextField userField = new TextField(); 
        userField.setPromptText("Username");
        // Pre-fill with a suggestion
        userField.setText(selected.getName().toLowerCase() + selected.getCustomerID());

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password (min 6 chars)");

        VBox form = UIUtils.createFormLayout();
        form.getChildren().addAll(
            new Label("Assign login credentials for " + selected.getName() + " " + selected.getSurname()),
            UIUtils.createFieldGroup("Username", userField, "fas-user"),
            UIUtils.createFieldGroup("Password", passField, "fas-lock")
        );

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String username = userField.getText().trim();
                String password = passField.getText();

                if (username.isEmpty() || password.length() < 6) {
                    new Alert(Alert.AlertType.WARNING, "Username required and password must be at least 6 characters!").showAndWait();
                    return;
                }

                if (userDAO.createUserForCustomer(selected.getCustomerID(), username, password)) {
                    new Alert(Alert.AlertType.INFORMATION, "Login credentials assigned successfully!").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to create user account. Username might be taken.").showAndWait();
                }
            }
        });
    }

    @FXML
    public void handleAddCustomer(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        UIUtils.styleDialog(dialog, "Add Customer", "Register a new vehicle owner", "fas-user-plus");

        TextField nameField = new TextField(); nameField.setPromptText("First Name");
        TextField surnameField = new TextField(); surnameField.setPromptText("Last Name");
        TextField addressField = new TextField(); addressField.setPromptText("Physical Address");
        TextField phoneField = new TextField(); phoneField.setPromptText("e.g. 55555555");
        TextField emailField = new TextField(); emailField.setPromptText("e.g. john@example.com");

        // Apply Validation
        UIUtils.applyLetterValidation(nameField);
        UIUtils.applyLetterValidation(surnameField);
        UIUtils.applyDigitValidation(phoneField);

        VBox form = UIUtils.createFormLayout();
        form.getChildren().addAll(
            UIUtils.createFieldGroup("First Name", nameField, "fas-user"),
            UIUtils.createFieldGroup("Last Name", surnameField, "fas-user"),
            UIUtils.createFieldGroup("Address", addressField, "fas-map-marker-alt"),
            UIUtils.createFieldGroup("Phone Number", phoneField, "fas-phone"),
            UIUtils.createFieldGroup("Email Address", emailField, "fas-envelope")
        );

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        UIUtils.styleDialog(dialog, "Add Customer", "Register a new vehicle owner", "fas-user-plus");

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String name    = nameField.getText().trim();
                String surname = surnameField.getText().trim();
                String address = addressField.getText().trim();
                String phone   = phoneField.getText().trim();
                String email   = emailField.getText().trim();

                if (name.isEmpty() || surname.isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "Name and surname are required!").showAndWait();
                    return;
                }

                CustomerDAO dao = new CustomerDAO();
                if (dao.addCustomer(name, surname, address, phone, email)) {
                    loadCustomers();
                    new Alert(Alert.AlertType.INFORMATION, "Customer added!").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to add customer.").showAndWait();
                }
            }
        });
    }
}
