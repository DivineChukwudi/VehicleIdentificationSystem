package com.vis.model;

public class Car extends Vehicle {

    private int numberOfDoors;

    public int getNumberOfDoors() {
        return numberOfDoors;
    }

    public void setNumberOfDoors(int numberOfDoors) {
        this.numberOfDoors = numberOfDoors;
    }

    public Car(int vehicleID, String registrationNumber, String make, String model, int year, int ownerID, int numberOfDoors) {
        super(vehicleID, registrationNumber,make, model, year, ownerID);
        this.numberOfDoors = numberOfDoors;
    }
    @Override
    public String getDetails(){
        return "Make: " + getMake() + " " + "Model: " + getModel() + " " + "(" + getYear() + ")" + " " + "Number of Doors: " + numberOfDoors;
    }
}
