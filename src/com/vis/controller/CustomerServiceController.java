package com.vis.controller;

import com.vis.db.ServiceDAO;
import com.vis.model.Service;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerServiceController implements Initializable {

    @FXML private TableView<Service> serviceTable;
    @FXML private TableColumn<Service, Integer> colServiceID;
    @FXML private TableColumn<Service, Integer> colVehicleID;
    @FXML private TableColumn<Service, String> colServiceDate;
    @FXML private TableColumn<Service, String> colServiceType;
    @FXML private TableColumn<Service, String> colDescription;
    @FXML private TableColumn<Service, Double> colCost;

    private int customerId = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colServiceID.setCellValueFactory(new PropertyValueFactory<>("serviceID"));
        colVehicleID.setCellValueFactory(new PropertyValueFactory<>("vehicleID"));
        colServiceDate.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));
        colServiceType.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
        loadServices();
    }

    private void loadServices() {
        if (customerId == -1) return;
        List<Service> services = new ServiceDAO().getServicesByCustomer(customerId);
        serviceTable.setItems(FXCollections.observableArrayList(services));
    }
}