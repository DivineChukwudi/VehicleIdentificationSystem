package com.vis.controller;

import com.vis.db.VehicleDAO;
import com.vis.model.Vehicle;
import com.vis.util.UIUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PaginationController implements Initializable {

    @FXML private ScrollPane        scrollPane;
    @FXML private VBox              itemContainer;
    @FXML private Pagination        pagination;
    @FXML private ProgressBar       progressBar;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label             lblProgress;
    @FXML private Label             lblPageInfo;
    @FXML private Label             lblTotalEntries;
    @FXML private Label             lblTotalPages;

    private static final int ITEMS_PER_PAGE = 5;
    private List<Vehicle> allVehicles;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Fetch real vehicle data from the database
        allVehicles = new VehicleDAO().getAllVehicles();

        if (allVehicles.isEmpty()) {
            itemContainer.getChildren().add(new Label("No vehicles found in database."));
            return;
        }

        int totalPages = (int) Math.ceil(allVehicles.size() / (double) ITEMS_PER_PAGE);

        if (lblTotalEntries != null) lblTotalEntries.setText("Total entries: " + allVehicles.size());
        if (lblTotalPages != null) lblTotalPages.setText("Total pages: " + totalPages);

        pagination.setPageCount(totalPages);
        pagination.setCurrentPageIndex(0);
        pagination.setMaxPageIndicatorCount(5);

        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
            loadPage(newVal.intValue());
            updateProgress(newVal.intValue(), totalPages);
        });

        loadPage(0);
        updateProgress(0, totalPages);
    }

    private void loadPage(int pageIndex) {
        itemContainer.getChildren().clear();
        int start = pageIndex * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allVehicles.size());

        for (int i = start; i < end; i++) {
            itemContainer.getChildren().add(buildVehicleCard(allVehicles.get(i), i + 1));
        }

        if (lblPageInfo != null) {
            lblPageInfo.setText(String.format("Showing entries %d – %d of %d", 
                    start + 1, end, allVehicles.size()));
        }
    }

    private HBox buildVehicleCard(Vehicle v, int number) {
        HBox card = new HBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setStyle(UIUtils.CARD_STYLE);

        Label numBadge = new Label(String.valueOf(number));
        numBadge.setPrefWidth(32);
        numBadge.setPrefHeight(32);
        numBadge.setAlignment(Pos.CENTER);
        numBadge.setStyle(UIUtils.ICON_CIRCLE_BLUE + "-fx-text-fill: #3498db; -fx-font-weight: bold; -fx-font-size: 12;");

        FontIcon icon = new FontIcon("fas-car");
        icon.setIconSize(18);
        icon.setIconColor(Color.web("#3498db"));

        VBox textBox = new VBox(3);
        Label mainLine = new Label(v.getRegistrationNumber() + " — " + v.getMake() + " " + v.getModel());
        mainLine.setStyle(UIUtils.ACTIVITY_TITLE_STYLE + "-fx-font-weight: bold;");
        Label detailLine = new Label("Year: " + v.getYear() + " | Owner ID: " + v.getOwnerID());
        detailLine.setStyle(UIUtils.ACTIVITY_SUBTITLE_STYLE);
        textBox.getChildren().addAll(mainLine, detailLine);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label status = new Label("REGISTERED");
        status.setStyle(UIUtils.BADGE_SUCCESS_STYLE + "-fx-font-weight: bold; -fx-font-size: 10;");

        card.getChildren().addAll(numBadge, icon, textBox, spacer, status);
        return card;
    }

    private void updateProgress(int pageIndex, int totalPages) {
        if (allVehicles == null || allVehicles.isEmpty()) return;
        
        int end = Math.min((pageIndex + 1) * ITEMS_PER_PAGE, allVehicles.size());
        double progress = (double) end / allVehicles.size();
        
        if (progressBar != null) progressBar.setProgress(progress);
        if (progressIndicator != null) progressIndicator.setProgress(progress);
        if (lblProgress != null) {
            lblProgress.setText(String.format("Page %d of %d  •  %d%% browsed", 
                    pageIndex + 1, totalPages, (int)(progress * 100)));
        }
    }
}