package com.vis.model;

public class Violation {
    private int violationId;
    private int vehicleId;
    private String violationDate;
    private String violationType;
    private double fineAmount;
    private double amountPaid;
    private String status;
    private String description;

    public int getViolationId() { return violationId; }
    public int getViolationID() { return violationId; } // Alias

    public int getVehicleId() { return vehicleId; }
    public int getVehicleID() { return vehicleId; } // Alias
    public String getViolationDate() { return violationDate; }
    public String getViolationType() { return violationType; }
    public double getFineAmount() { return fineAmount; }
    public double getAmountPaid() { return amountPaid; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }

    public void setViolationId(int violationId) { this.violationId = violationId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public void setViolationDate(String violationDate) { this.violationDate = violationDate; }
    public void setViolationType(String violationType) { this.violationType = violationType; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
    public void setStatus(String status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }

    public Violation(int violationId, int vehicleId, String violationDate, String violationType, double fineAmount, String status, String description) {
        this(violationId, vehicleId, violationDate, violationType, fineAmount, 0.0, status, description);
    }

    public Violation(int violationId, int vehicleId, String violationDate, String violationType, double fineAmount, double amountPaid, String status, String description) {
        this.violationId = violationId;
        this.vehicleId = vehicleId;
        this.violationDate = violationDate;
        this.violationType = violationType;
        this.fineAmount = fineAmount;
        this.amountPaid = amountPaid;
        this.status = status;
        this.description = description;
    }

    public double getRemainingAmount() {
        return fineAmount - amountPaid;
    }

    public String getDetails() {
        return "Violation ID: " + violationId + "\nVehicle ID: " + vehicleId + "\nDate: " + violationDate + "\nType: " + violationType + "\nFine: " + fineAmount + "\nPaid: " + amountPaid + "\nStatus: " + status + "\nDescription: " + description;
    }
}