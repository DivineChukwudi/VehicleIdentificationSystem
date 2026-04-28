package com.vis.model;

public class Violation {
    private int violationId;
    private int vehicleId;
    private String violationDate;
    private String violationType;
    private double fineAmount;
    private String status;
    private String description;

    public int getViolationId() { return violationId; }
    public int getVehicleId() { return vehicleId; }
    public String getViolationDate() { return violationDate; }
    public String getViolationType() { return violationType; }
    public double getFineAmount() { return fineAmount; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }

    public void setViolationId(int violationId) { this.violationId = violationId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public void setViolationDate(String violationDate) { this.violationDate = violationDate; }
    public void setViolationType(String violationType) { this.violationType = violationType; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }
    public void setStatus(String status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }

    public Violation(int violationId, int vehicleId, String violationDate, String violationType, double fineAmount, String status, String description) {
        this.violationId = violationId;
        this.vehicleId = vehicleId;
        this.violationDate = violationDate;
        this.violationType = violationType;
        this.fineAmount = fineAmount;
        this.status = status;
        this.description = description;
    }

    public String getDetails() {
        return "Violation ID: " + violationId + "\nVehicle ID: " + vehicleId + "\nDate: " + violationDate + "\nType: " + violationType + "\nFine: " + fineAmount + "\nStatus: " + status + "\nDescription: " + description;
    }
}