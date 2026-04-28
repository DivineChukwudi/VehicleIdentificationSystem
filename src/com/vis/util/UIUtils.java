package com.vis.util;

/**
 * Reusable UI styling utilities for the Vehicle Identification System.
 */
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

public class UIUtils {

    public static final String DIALOG_BG = "-fx-background-color: #1a2540;";
    public static final String FIELD_STYLE = "-fx-background-color: #12192b; -fx-text-fill: white; " +
            "-fx-border-color: #2a3a5a; -fx-border-radius: 6; -fx-padding: 10; -fx-font-size: 13;";
    public static final String LABEL_STYLE = "-fx-text-fill: #bdc3c7; -fx-font-weight: bold; -fx-font-size: 12;";
    public static final String HEADER_STYLE = "-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;";
    public static final String SUBHEADER_STYLE = "-fx-text-fill: #5a6a8a; -fx-font-size: 13;";

    public static final String PRIMARY_BUTTON_STYLE = "-fx-background-color: #3498db; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 6;";
    public static final String SECONDARY_BUTTON_STYLE = "-fx-background-color: #34495e; -fx-text-fill: white; " +
            "-fx-cursor: hand; -fx-background-radius: 6;";
    public static final String SUCCESS_BUTTON_STYLE = "-fx-background-color: #27ae60; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 6;";
    public static final String DANGER_BUTTON_STYLE = "-fx-background-color: #e74c3c; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 6;";
    
    public static final String TOGGLE_BUTTON_STYLE = "-fx-background-color: #12192b; -fx-text-fill: #8a9ab5; " +
            "-fx-background-radius: 6; -fx-border-color: #2a3a5a; -fx-border-radius: 6; -fx-border-width: 1; -fx-cursor: hand;";
    public static final String TOGGLE_BUTTON_SELECTED_STYLE = "-fx-background-color: #1e3a5f; -fx-text-fill: #3498db; " +
            "-fx-background-radius: 6; -fx-border-color: #3498db; -fx-border-radius: 6; -fx-border-width: 1.5; -fx-cursor: hand;";
    
    public static final String SEARCH_BAR_STYLE = "-fx-background-color: #12192b; -fx-background-radius: 6; " +
            "-fx-border-color: #2a3a5a; -fx-border-radius: 6; -fx-border-width: 1;";

    // Status Badge Styles (Role labels)
    public static final String BADGE_ROLE_BLUE = "-fx-background-color: #1e3a5f; -fx-padding: 4 12; -fx-background-radius: 20; -fx-text-fill: #3498db; -fx-font-size: 11; -fx-font-weight: bold;";
    public static final String BADGE_ROLE_PURPLE = "-fx-background-color: #2a1a4a; -fx-padding: 4 12; -fx-background-radius: 20; -fx-text-fill: #9b59b6; -fx-font-size: 11; -fx-font-weight: bold;";
    public static final String BADGE_ROLE_RED = "-fx-background-color: #3a1a1a; -fx-padding: 4 12; -fx-background-radius: 20; -fx-text-fill: #e74c3c; -fx-font-size: 11; -fx-font-weight: bold;";

    // List and Feed Styles
    public static final String LIST_ROW_STYLE = "-fx-padding: 10 18; -fx-border-color: transparent transparent #1a2540 transparent; -fx-border-width: 1;";
    public static final String ACTIVITY_TITLE_STYLE = "-fx-font-size: 13; -fx-text-fill: white;";
    public static final String ACTIVITY_SUBTITLE_STYLE = "-fx-font-size: 11; -fx-text-fill: #5a6a8a;";
    
    public static final String BADGE_PENDING_STYLE = "-fx-background-color: #4a1a1a; -fx-text-fill: #e74c3c; -fx-padding: 2 8; -fx-background-radius: 10; -fx-font-size: 11;";
    public static final String BADGE_SUCCESS_STYLE = "-fx-background-color: #1a4a2a; -fx-text-fill: #27ae60; -fx-padding: 2 8; -fx-background-radius: 10; -fx-font-size: 11;";
    
    public static final String ICON_CIRCLE_PENDING = "-fx-background-color: #4a1a1a; -fx-background-radius: 50;";
    public static final String ICON_CIRCLE_SUCCESS = "-fx-background-color: #1a4a2a; -fx-background-radius: 50;";
    public static final String ICON_CIRCLE_BLUE = "-fx-background-color: #1e3a5f; -fx-background-radius: 50;";

    // Card Styles
    public static final String CARD_STYLE = "-fx-background-color: #12192b; -fx-background-radius: 8; " +
            "-fx-border-color: #2a3a5a; -fx-border-radius: 8; -fx-border-width: 1;";

    // Portal Sidebar Styles
    public static final String SIDEBAR_DEFAULT_STYLE = "-fx-background-color: transparent; -fx-text-fill: #bdc3c7; " +
            "-fx-alignment: CENTER_LEFT; -fx-background-radius: 6; -fx-cursor: hand;";
    public static final String SIDEBAR_ACTIVE_STYLE = "-fx-background-color: #1e3a5f; -fx-text-fill: #3498db; " +
            "-fx-alignment: CENTER_LEFT; -fx-background-radius: 6; -fx-cursor: hand;";
    public static final String SIDEBAR_ACTIVE_RED_STYLE = "-fx-background-color: #3a1a1a; -fx-text-fill: #e74c3c; " +
            "-fx-alignment: CENTER_LEFT; -fx-background-radius: 6; -fx-cursor: hand;";
    public static final String SIDEBAR_ACTIVE_PURPLE_STYLE = "-fx-background-color: #2a1a4a; -fx-text-fill: #9b59b6; " +
            "-fx-alignment: CENTER_LEFT; -fx-background-radius: 6; -fx-cursor: hand;";

    public static void styleDialog(Dialog<?> dialog, String title, String header, String iconLiteral) {
        DialogPane pane = dialog.getDialogPane();
        pane.setStyle(DIALOG_BG + "-fx-border-color: #2a3a5a; -fx-border-width: 1; -fx-border-radius: 12; -fx-background-radius: 12;");
        pane.setPrefWidth(650); // Increased width for enterprise feel
        
        VBox headerBox = new VBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(35, 45, 25, 45));
        headerBox.setStyle("-fx-background-color: linear-gradient(to bottom right, #1e3a5f, #12192b); " +
                          "-fx-background-radius: 12 12 0 0; -fx-border-color: #2a3a5a; -fx-border-width: 0 0 1 0;");
        
        HBox titleRow = new HBox(20);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        
        if (iconLiteral != null) {
            VBox iconCircle = new VBox();
            iconCircle.setAlignment(Pos.CENTER);
            iconCircle.setPrefSize(56, 56);
            iconCircle.setStyle("-fx-background-color: rgba(52, 152, 219, 0.15); -fx-background-radius: 50;");
            
            FontIcon icon = new FontIcon(iconLiteral);
            icon.setIconSize(32);
            icon.setIconColor(Color.web("#3498db"));
            iconCircle.getChildren().add(icon);
            titleRow.getChildren().add(iconCircle);
        }
        
        VBox textStack = new VBox(5);
        Label lblTitle = new Label(title);
        lblTitle.setStyle(HEADER_STYLE + "-fx-font-size: 26; -fx-letter-spacing: 0.5;");
        
        Label lblHeader = new Label(header);
        lblHeader.setStyle(SUBHEADER_STYLE + "-fx-font-size: 15; -fx-text-fill: #8a9ab5;");
        
        textStack.getChildren().addAll(lblTitle, lblHeader);
        titleRow.getChildren().add(textStack);
        
        headerBox.getChildren().add(titleRow);
        pane.setHeader(headerBox);
        
        // Style buttons
        pane.getButtonTypes().forEach(buttonType -> {
            Button btn = (Button) pane.lookupButton(buttonType);
            if (btn != null) {
                String baseStyle;
                String hoverStyle;
                if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE || 
                    buttonType.getButtonData() == ButtonBar.ButtonData.YES) {
                    baseStyle = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 14 35; -fx-background-radius: 8; -fx-font-size: 14;";
                    hoverStyle = "-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 14 35; -fx-background-radius: 8; -fx-font-size: 14;";
                } else {
                    baseStyle = "-fx-background-color: #34495e; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 14 35; -fx-background-radius: 8; -fx-font-size: 14;";
                    hoverStyle = "-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 14 35; -fx-background-radius: 8; -fx-font-size: 14;";
                }
                
                btn.setStyle(baseStyle);
                
                // Add hover effect
                btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
                btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
            }
        });
    }

    public static void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        String icon = "fas-info-circle";
        if (type == Alert.AlertType.ERROR) icon = "fas-times-circle";
        else if (type == Alert.AlertType.WARNING) icon = "fas-exclamation-triangle";
        else if (type == Alert.AlertType.CONFIRMATION) icon = "fas-question-circle";
        
        styleDialog(alert, title, header, icon);
        
        // Content area styling
        DialogPane pane = alert.getDialogPane();
        pane.setPrefWidth(450); // Alerts can be slightly smaller
        Label contentLabel = (Label) pane.lookup(".content.label");
        if (contentLabel != null) {
            contentLabel.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 14; -fx-padding: 10 30;");
        }
        
        alert.showAndWait();
    }

    public static VBox createFormLayout() {
        VBox form = new VBox(22);
        form.setPadding(new Insets(25, 45, 35, 45));
        form.setStyle(DIALOG_BG);
        return form;
    }

    public static VBox createFieldGroup(String labelText, Node field, String iconLiteral) {
        VBox group = new VBox(10);
        
        HBox labelRow = new HBox(10);
        labelRow.setAlignment(Pos.CENTER_LEFT);
        
        if (iconLiteral != null) {
            FontIcon icon = new FontIcon(iconLiteral);
            icon.setIconSize(16);
            icon.setIconColor(Color.web("#3498db"));
            labelRow.getChildren().add(icon);
        }
        
        Label label = new Label(labelText.toUpperCase());
        label.setStyle(LABEL_STYLE + "-fx-font-size: 11; -fx-letter-spacing: 1; -fx-text-fill: #5a6a8a;");
        labelRow.getChildren().add(label);
        
        if (field instanceof TextInputControl) {
            field.setStyle(FIELD_STYLE + "-fx-background-radius: 8; -fx-border-radius: 8;");
            if (field instanceof TextArea) {
                field.setStyle(FIELD_STYLE + "-fx-padding: 12; -fx-control-inner-background: #12192b; -fx-text-fill: white; -fx-background-radius: 8; -fx-border-radius: 8;");
                ((TextArea) field).setWrapText(true);
                ((TextArea) field).setPrefRowCount(4);
            }
        } else if (field instanceof ComboBox) {
            field.setStyle(FIELD_STYLE + "-fx-background-radius: 8; -fx-border-radius: 8;");
            ((ComboBox<?>) field).setMaxWidth(Double.MAX_VALUE);
        } else if (field instanceof DatePicker) {
            field.setStyle(FIELD_STYLE + "-fx-background-radius: 8; -fx-border-radius: 8;");
            ((DatePicker) field).setMaxWidth(Double.MAX_VALUE);
        }
        
        group.getChildren().addAll(labelRow, field);
        return group;
    }

    // Keep the old one for compatibility if needed, but update it to call the new one
    public static VBox createFieldGroup(String labelText, Node field) {
        return createFieldGroup(labelText, field, null);
    }

    /**
     * Applies validation to allow only letters and spaces on keystroke.
     */
    public static void applyLetterValidation(TextInputControl field) {
        field.textProperty().addListener((obs, old, nw) -> {
            if (nw != null && !nw.matches("[a-zA-Z\\s]*")) {
                field.setText(old);
            }
        });
    }

    /**
     * Applies validation to allow only numbers (including decimals) on keystroke.
     */
    public static void applyNumberValidation(TextInputControl field) {
        field.textProperty().addListener((obs, old, nw) -> {
            if (nw != null && !nw.isEmpty() && !nw.matches("\\d*(\\.\\d*)?")) {
                field.setText(old);
            }
        });
    }

    /**
     * Applies validation to allow only digits (no decimals) on keystroke.
     */
    public static void applyDigitValidation(TextInputControl field) {
        field.textProperty().addListener((obs, old, nw) -> {
            if (nw != null && !nw.isEmpty() && !nw.matches("\\d*")) {
                field.setText(old);
            }
        });
    }
}
