package com.vis.model;

public class Service {
    private int serviceID;
    private int vehicleID;
    private String serviceDate;
    private String serviceType;
    private String description;
    private double cost;


    //  GETTERS
    public int getServiceID(){
        return serviceID;
    }
    public int getServiceId() { return serviceID; } // Alias for consistency

    public int getVehicleID(){
        return vehicleID;
    }
    public int getVehicleId() { return vehicleID; } // Alias for consistency
    public String getServiceDate(){
        return serviceDate;
    }
    public String getServiceType(){
        return serviceType;
    }
    public String getDescription(){
        return description;
    }
    public double getCost(){
        return cost;
    }

    //     SETTERS
    public void setServiceID(int serviceID){
        this.serviceID = serviceID;
    }
    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }
    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }

    public Service(int serviceID, int vehicleID, String serviceDate, String serviceType, String description, double cost){
        this.serviceID = serviceID;
        this.vehicleID = vehicleID;
        this.serviceDate = serviceDate;
        this.serviceType = serviceType;
        this.description = description;
        this.cost = cost;
    }

    // GET SERVICE DETAILS
    public String getServiceDetails() {
        return "ServiceID: " + serviceID + "\n" + "VehicleID: " + vehicleID + "\n" + "Service Date: " + serviceDate + "\n" + "Service Type: " + serviceType + "\n" + "Description: " + description + "\n" + "Cost: " + cost;
    }
}
