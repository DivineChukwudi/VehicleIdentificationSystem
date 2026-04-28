package com.vis.controller;

import com.vis.db.InsuranceDAO;
import com.vis.model.Insurance;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class InsuranceListController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TableView<Insurance> insuranceTable;
    @FXML private TableColumn<Insurance, Integer> colPolicyID;
    @FXML private TableColumn<Insurance, Integer> colVehicleID;
    @FXML private TableColumn<Insurance, String> colProvider;
    @FXML private TableColumn<Insurance, String> colPolicyNumber;
    @FXML private TableColumn<Insurance, LocalDate> colStartDate;
    @FXML private TableColumn<Insurance, LocalDate> colEndDate;
    @FXML private TableColumn<Insurance, String> colStatus;

    @FXML private Label lblTotalPolicies;
    @FXML private Label lblActivePolicies;
    @FXML private Label lblExpiredPolicies;

    private final ObservableList<Insurance> insuranceData = FXCollections.observableArrayList();
    private final InsuranceDAO insuranceDAO = new InsuranceDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colPolicyID.setCellValueFactory(new PropertyValueFactory<>("policyId"));
        colVehicleID.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        colProvider.setCellValueFactory(new PropertyValueFactory<>("provider"));
        colPolicyNumber.setCellValueFactory(new PropertyValueFactory<>("policyNumber"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadData();

        FilteredList<Insurance> filteredData = new FilteredList<>(insuranceData, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(insurance -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (String.valueOf(insurance.getVehicleId()).contains(lowerCaseFilter)) return true;
                if (insurance.getPolicyNumber().toLowerCase().contains(lowerCaseFilter)) return true;
                if (insurance.getProvider().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });
        insuranceTable.setItems(filteredData);
    }

    private void loadData() {
        insuranceData.clear();
        insuranceData.addAll(insuranceDAO.getAllInsurances());
        loadStats();
    }

    private void loadStats() {
        long total = insuranceData.size();
        long active = insuranceData.stream().filter(i -> "ACTIVE".equalsIgnoreCase(i.getStatus())).count();
        long expired = insuranceData.stream().filter(i -> "EXPIRED".equalsIgnoreCase(i.getStatus())).count();

        lblTotalPolicies.setText(String.valueOf(total));
        lblActivePolicies.setText(String.valueOf(active));
        lblExpiredPolicies.setText(String.valueOf(expired));
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }
}
