package com.vis.controller;

import com.vis.db.*;
import com.vis.model.Violation;
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

    private void loadStatsAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                // ── Fetch counts ────────────────────────────────────────────
                int vehicleCount = VehicleDAO.getAllVehicles().size();
                int customerCount  = new CustomerDAO().getAllCustomers().size();
                int reportCount    = new PoliceReportDAO().getAllReports().size();
                int serviceCount   = new ServiceDAO().getAllServices().size();

                List<com.vis.model.Violation> violations = new ViolationDAO().getAllViolations();
                int violationCount = violations.size();

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

                double totalServiceRevenue = new ServiceDAO().getAllServices()
                        .stream().mapToDouble(s -> s.getCost()).sum();

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

                    buildActivityFeed(violations, pending);
                });

            } catch (Exception e) {
                Platform.runLater(() ->
                        lblLastUpdated.setText("Could not load stats — check database connection.")
                );
                e.printStackTrace();
            }
        });
    }

    private void buildActivityFeed(List<Violation> violations, long pending) {
        activityList.getChildren().clear();

        // Recent violations as activity entries (up to 8)
        int shown = 0;
        for (Violation v : violations) {
            if (shown >= 8) break;

            boolean isPending = "Pending".equalsIgnoreCase(v.getStatus())
                    || "Unpaid".equalsIgnoreCase(v.getStatus());

            HBox row = new HBox();
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            row.setSpacing(14);
            row.setStyle(UIUtils.LIST_ROW_STYLE);

            // Icon
            VBox iconBox = new VBox();
            iconBox.setAlignment(javafx.geometry.Pos.CENTER);
            iconBox.setPrefWidth(32);
            iconBox.setPrefHeight(32);
            iconBox.setStyle(isPending ? UIUtils.ICON_CIRCLE_PENDING : UIUtils.ICON_CIRCLE_SUCCESS);
            
            FontIcon icon = new FontIcon(isPending ? "fas-exclamation" : "fas-check");
            icon.setIconSize(12);
            icon.setIconColor(javafx.scene.paint.Color.web(isPending ? "#e74c3c" : "#27ae60"));
            iconBox.getChildren().add(icon);

            // Text
            VBox textBox = new VBox(2);
            Label title = new Label("Violation #" + v.getViolationId() +
                    " — " + v.getViolationType());
            title.setStyle(UIUtils.ACTIVITY_TITLE_STYLE);

            Label sub = new Label("Vehicle ID " + v.getVehicleId() +
                    "  •  " + v.getViolationDate() +
                    "  •  M " + String.format("%.2f", v.getFineAmount()));
            sub.setStyle(UIUtils.ACTIVITY_SUBTITLE_STYLE);

            textBox.getChildren().addAll(title, sub);

            // Status badge
            Label badge = new Label(v.getStatus());
            badge.setStyle(isPending ? UIUtils.BADGE_PENDING_STYLE : UIUtils.BADGE_SUCCESS_STYLE);

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
}