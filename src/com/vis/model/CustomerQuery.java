package com.vis.model;

public class CustomerQuery {
    private int queryId;
    private int customerId;
    private int vehicleId;
    private String queryDate;
    private String queryText;
    private String responseText;

    public int getQueryId() { return queryId; }
    public int getCustomerId() { return customerId; }
    public int getVehicleId() { return vehicleId; }
    public String getQueryDate() { return queryDate; }
    public String getQueryText() { return queryText; }
    public String getResponseText() { return responseText; }

    public void setQueryId(int queryId) { this.queryId = queryId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public void setQueryDate(String queryDate) { this.queryDate = queryDate; }
    public void setQueryText(String queryText) { this.queryText = queryText; }
    public void setResponseText(String responseText) { this.responseText = responseText; }

    public CustomerQuery(int queryId, int customerId, int vehicleId, String queryDate, String queryText, String responseText) {
        this.queryId = queryId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.queryDate = queryDate;
        this.queryText = queryText;
        this.responseText = responseText;
    }

    public String getDetails() {
        return "Query ID: " + queryId + "\nCustomer ID: " + customerId + "\nVehicle ID: " + vehicleId + "\nDate: " + queryDate + "\nQuery: " + queryText + "\nResponse: " + responseText;
    }
}