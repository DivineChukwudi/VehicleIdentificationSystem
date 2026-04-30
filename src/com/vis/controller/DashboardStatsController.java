package com.vis.controller;

import com.vis.db.*;
import com.vis.model.*;
import com.vis.util.UIUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class DashboardStatsController implements Initializable {

    @FXML private Label lblLastUpdated;

    // Stat labels
    @FXML private Label statVehicles;
    @FXML private Label statCustomers;
    @FXML private Label statViolations;
    @FXML private Label statReports;
    @FXML private Label statServices;
    @FXML private Label statFines;

    // Sub-labels
    @FXML private Label lblViolationsPending;
    @FXML private Label lblUnpaidViolations;
    @FXML private Label lblPaidViolations;
    @FXML private Label lblTotalServiceCost;

    // Activity feed
    @FXML private VBox activityList;
    @FXML private Label lblActivityCount;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblLastUpdated.setText("Loading data...");
        loadStatsAsync();
    }

    @FXML
    private void quickAddVehicle() {
        if (DashboardController.getInstance() != null) {
            DashboardController.getInstance().navigateTo("Vehicles", "add");
        }
    }

    @FXML
    private void quickAddCustomer() {
        if (DashboardController.getInstance() != null) {
            DashboardController.getInstance().navigateTo("Customers", "add");
        }
    }

    @FXML
    private void quickIssueViolation() {
        if (DashboardController.getInstance() != null) {
            DashboardController.getInstance().navigateTo("Violations", "add");
        }
    }

    @FXML
    private void quickLogService() {
        if (DashboardController.getInstance() != null) {
            DashboardController.getInstance().navigateTo("ServiceRecords", "add");
        }
    }

    @FXML
    public void handleRefresh() {
        lblLastUpdated.setText("Refreshing data...");
        loadStatsAsync();
    }

    private void loadStatsAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                List<com.vis.model.Violation> violations = new ViolationDAO().getAllViolations();
                List<com.vis.model.PoliceReport> reports = new PoliceReportDAO().getAllReports();
                List<com.vis.model.Service> services = new ServiceDAO().getAllServices();
                List<com.vis.model.Customer> customers = new CustomerDAO().getAllCustomers();

                int violationCount = violations.size();
                int reportCount    = reports.size();
                int serviceCount   = services.size();
                int vehicleCount   = VehicleDAO.getAllVehicles().size();
                int customerCount  = customers.size();

                long pending = violations.stream()
                        .filter(v -> "Pending".equalsIgnoreCase(v.getStatus())
                                || "Unpaid".equalsIgnoreCase(v.getStatus()))
                        .count();
                long paid = violations.stream()
                        .filter(v -> "Paid".equalsIgnoreCase(v.getStatus()))
                        .count();
                double totalFines = violations.stream()
                        .filter(v -> !"Paid".equalsIgnoreCase(v.getStatus()))
                        .mapToDouble(Violation::getFineAmount)
                        .sum();

                double totalServiceRevenue = services.stream().mapToDouble(s -> s.getCost()).sum();

                String updatedAt = "Last updated: " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));

                // ── Update UI on JavaFX thread ───────────────────────────────
                Platform.runLater(() -> {
                    statVehicles.setText(String.valueOf(vehicleCount));
                    statCustomers.setText(String.valueOf(customerCount));
                    statViolations.setText(String.valueOf(violationCount));
                    statReports.setText(String.valueOf(reportCount));
                    statServices.setText(String.valueOf(serviceCount));
                    statFines.setText(String.format("M %.2f", totalFines));

                    lblViolationsPending.setText("Pending fines: " + pending);
                    lblUnpaidViolations.setText("Unpaid violations: " + pending);
                    lblPaidViolations.setText("Paid: " + paid);
                    lblTotalServiceCost.setText(String.format("Revenue: M %.2f", totalServiceRevenue));
                    lblLastUpdated.setText(updatedAt);

                    buildUnifiedActivityFeed(violations, reports, services, customers);
                });

            } catch (Exception e) {
                Platform.runLater(() ->
                        lblLastUpdated.setText("Could not load stats — check database connection.")
                );
                e.printStackTrace();
            }
        });
    }

    private void buildUnifiedActivityFeed(List<Violation> violations, List<PoliceReport> reports, 
                                          List<Service> services, List<Customer> customers) {
        activityList.getChildren().clear();

        // Combine all into a single list of Activity items
        class ActivityItem {
            String title, subtitle, status, type;
            LocalDateTime date;
            ActivityItem(String title, String subtitle, String status, String type, LocalDateTime date) {
                this.title = title; this.subtitle = subtitle; this.status = status; this.type = type; this.date = date;
            }
        }
        java.util.List<ActivityItem> feed = new java.util.ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Violations
        for (Violation v : violations) {
            LocalDateTime d = parseDate(v.getViolationDate());
            feed.add(new ActivityItem("Violation #" + v.getViolationId() + " — " + v.getViolationType(),
                    "Vehicle " + v.getVehicleId() + "  •  M " + String.format("%.2f", v.getFineAmount()),
                    v.getStatus(), "violation", d));
        }
        // Reports
        for (PoliceReport r : reports) {
            LocalDateTime d = parseDate(r.getReportDate());
            feed.add(new ActivityItem("Police Report #" + r.getReportId() + " — " + r.getReportType(),
                    "Vehicle " + r.getVehicleId() + "  •  Officer: " + r.getOfficerName(),
                    "Reported", "report", d));
        }
        // Services
        for (Service s : services) {
            LocalDateTime d = parseDate(s.getServiceDate());
            feed.add(new ActivityItem("Service #" + s.getServiceId() + " — " + s.getServiceType(),
                    "Vehicle " + s.getVehicleId() + "  •  Cost: M " + String.format("%.2f", s.getCost()),
                    "Completed", "service", d));
        }
        // New Customers
        for (Customer c : customers) {
            // Customers don't have a date in the model, but we can treat them as activity
            feed.add(new ActivityItem("New Customer: " + c.getName() + " " + c.getSurname(),
                    "Email: " + c.getEmail(), "Registered", "customer", LocalDateTime.now().minusDays(1)));
        }

        // Sort by date desc
        feed.sort((a, b) -> b.date.compareTo(a.date));

        int shown = 0;
        for (ActivityItem item : feed) {
            if (shown >= 10) break;

            boolean isWarning = item.status.equalsIgnoreCase("Unpaid") || item.status.equalsIgnoreCase("Pending");
            boolean isInfo = item.type.equals("customer") || item.type.equals("report");

            HBox row = new HBox();
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            row.setSpacing(14);
            row.setStyle(UIUtils.LIST_ROW_STYLE);

            // Icon
            VBox iconBox = new VBox();
            iconBox.setAlignment(javafx.geometry.Pos.CENTER);
            iconBox.setPrefWidth(32);
            iconBox.setPrefHeight(32);
            
            String iconLiteral = "fas-check";
            String color = "#27ae60";
            String circle = UIUtils.ICON_CIRCLE_SUCCESS;

            if (isWarning) {
                iconLiteral = "fas-exclamation";
                color = "#e74c3c";
                circle = UIUtils.ICON_CIRCLE_PENDING;
            } else if (isInfo) {
                iconLiteral = item.type.equals("customer") ? "fas-user" : "fas-file-alt";
                color = "#3498db";
                circle = UIUtils.ICON_CIRCLE_INFO;
            }

            iconBox.setStyle(circle);
            FontIcon icon = new FontIcon(iconLiteral);
            icon.setIconSize(12);
            icon.setIconColor(javafx.scene.paint.Color.web(color));
            iconBox.getChildren().add(icon);

            // Text
            VBox textBox = new VBox(2);
            Label title = new Label(item.title);
            title.setStyle(UIUtils.ACTIVITY_TITLE_STYLE);

            Label sub = new Label(item.subtitle + "  •  " + item.date.format(DateTimeFormatter.ofPattern("dd MMM")));
            sub.setStyle(UIUtils.ACTIVITY_SUBTITLE_STYLE);

            textBox.getChildren().addAll(title, sub);

            // Status badge
            Label badge = new Label(item.status);
            if (isWarning) badge.setStyle(UIUtils.BADGE_PENDING_STYLE);
            else if (isInfo) badge.setStyle(UIUtils.BADGE_INFO_STYLE);
            else badge.setStyle(UIUtils.BADGE_SUCCESS_STYLE);

            HBox spacer = new HBox();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            row.getChildren().addAll(iconBox, textBox, spacer, badge);
            activityList.getChildren().add(row);
            shown++;
        }

        if (shown == 0) {
            HBox emptyRow = new HBox();
            emptyRow.setAlignment(javafx.geometry.Pos.CENTER);
            emptyRow.setStyle("-fx-padding: 30;");
            Label empty = new Label("No recent activity");
            empty.setStyle("-fx-text-fill: #2a3a5a; -fx-font-size: 13;");
            emptyRow.getChildren().add(empty);
            activityList.getChildren().add(emptyRow);
        }

        lblActivityCount.setText(shown + " recent entries");
    }

    private LocalDateTime parseDate(String dateStr) {
        try {
            if (dateStr == null || dateStr.isEmpty()) return LocalDateTime.now();
            return java.time.LocalDate.parse(dateStr).atStartOfDay();
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}