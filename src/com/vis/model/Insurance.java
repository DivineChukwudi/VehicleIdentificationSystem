package com.vis.model;

import java.time.LocalDate;

public class Insurance {
    private int policyId;
    private int vehicleId;
    private String provider;
    private String policyNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public Insurance(int policyId, int vehicleId, String provider, String policyNumber, LocalDate startDate, LocalDate endDate, String status) {
        this.policyId = policyId;
        this.vehicleId = vehicleId;
        this.provider = provider;
        this.policyNumber = policyNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Getters and Setters
    public int getPolicyId() { return policyId; }
    public void setPolicyId(int policyId) { this.policyId = policyId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
