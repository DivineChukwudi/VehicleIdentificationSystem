package com.vis.model;

public class PoliceReport {
    private int reportID;
    private int vehicleID;
    private String reportDate;
    private String reportType;
    private String description;
    private String officerName;


    //  GETTERS
    public int getReportID(){
        return reportID;
    }
    public int getReportId() { return reportID; } // Alias for consistency

    public int getVehicleID() {
        return vehicleID;
    }
    public int getVehicleId() { return vehicleID; } // Alias for consistency
    public String getReportDate() {
        return reportDate;
    }
    public String getReportType() {
        return reportType;
    }
    public String getDescription() {
        return description;
    }
    public String getOfficerName() {
        return officerName;
    }


    //  SETTERS

    public void setReportID(int reportID) {
        this.reportID = reportID;
    }
    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }
    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setOfficerName(String officerName) {
       this.officerName = officerName;
    }


    //  CONSTRUCTOR
    public PoliceReport(int reportID, int vehicleID, String reportDate, String reportType, String description, String officerName){
        this.reportID = reportID;
        this.vehicleID = vehicleID;
        this.reportDate = reportDate;
        this.reportType = reportType;
        this.description = description;
        this.officerName = officerName;

    }
    public String getDetails() {
        return "Report ID: " + reportID + "\nVehicle ID: " + vehicleID + "\nDate: " + reportDate + "\nType: " + reportType + "\nDescription: " + description + "\nOfficer: " + officerName;
    }
}

