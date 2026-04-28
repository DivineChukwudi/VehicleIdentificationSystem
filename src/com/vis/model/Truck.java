package com.vis.model;


public class Truck extends Vehicle {

    private double loadCapacity; // tonnes

    public Truck(int vehicleID, String registrationNumber, String make,
                 String model, int year, int ownerID, double loadCapacity) {
        super(vehicleID, registrationNumber, make, model, year, ownerID);
        this.loadCapacity = loadCapacity;
    }

    public double getLoadCapacity() { return loadCapacity; }
    public void setLoadCapacity(double loadCapacity) { this.loadCapacity = loadCapacity; }

    @Override
    public String getDetails() {
        return "Make: " + getMake() + "  Model: " + getModel()
                + "  (" + getYear() + ")  Load Capacity: " + loadCapacity + "t  [TRUCK]";
    }
}
