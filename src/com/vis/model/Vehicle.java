package com.vis.model;

public class Vehicle {
    private int vehicleID;
    private String registrationNumber;
    private String make;
    private String model;
    private int year;
    private int ownerID;

    //  GETTERS
    public int  getVehicleID(){return vehicleID;}
    public String getRegistrationNumber(){return registrationNumber;}
    public String getMake() {
        return make;
    }
    public String getModel() {
        return model;
    }
    public int getYear() {
        return year;
    }
    public int getOwnerID(){return ownerID;}



    //  SETTERS

    public void setVehicleID(int vehicleID){
        this.vehicleID = vehicleID;
    }
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    public void setMake(String make){
        this.make = make;
    }
    public void setModel(String model){
        this.model= model;
    }
    public void setYear(int year){
        this.year = year;
    }
    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    // CONSTRUCTORS
    public Vehicle(int vehicleID, String registrationNumber,String make, String model, int year, int ownerID){
        this.vehicleID = vehicleID;
        this.registrationNumber = registrationNumber;
        this.make = make;
        this.model = model;
        this.year = year;
        this.ownerID = ownerID;

    }

    //Display details
    public String getDetails(){
        return "Vehicle ID: " + vehicleID + "\n" + "Registration Number: " + registrationNumber + "\n" +"Make: " + make + "\n" + "Model: " + model + "\n" + "(" + getYear() + ")" + "\n" + "Owner ID: "  + ownerID;
    }
}
