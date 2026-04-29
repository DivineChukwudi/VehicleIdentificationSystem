package com.vis.view;

import javafx.animation.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashScreen extends Application {

    private static final double WIDTH  = 900;
    private static final double HEIGHT = 600;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        Pane root = new Pane();
        root.setStyle("-fx-background-color: black;");

        SVGPath car = new SVGPath();
        car.setContent("M10,60 Q10,40 40,35 L100,35 Q130,35 150,55 L250,55 Q280,55 280,80 L280,100 L10,100 Z");
        car.setFill(Color.DARKGRAY);

        SVGPath headlights = new SVGPath();
        headlights.setContent("M280,80 L290,75 M280,90 L290,95");
        headlights.setStroke(Color.YELLOW);
        headlights.setStrokeWidth(3);
        headlights.setOpacity(0);

        Group carGroup = new Group(car, headlights);
        carGroup.setLayoutY(HEIGHT * 0.35);
        carGroup.setLayoutX(WIDTH / 2 - 140);

        VBox textContainer = new VBox(10);
        textContainer.setAlignment(Pos.CENTER);
        textContainer.setPrefWidth(WIDTH);
        textContainer.setLayoutY(HEIGHT * 0.6);

        Text titleText = new Text("");
        titleText.setFill(Color.WHITE);
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 36));

        Line separator = new Line(0, 0, 400, 0);
        separator.setStroke(Color.WHITE);
        separator.setStrokeWidth(2);
        separator.setScaleX(0);

        Text footerText = new Text("Powered by VIS");
        footerText.setFill(Color.GRAY);
        footerText.setFont(Font.font("Arial", 14));
        footerText.setOpacity(0);

        textContainer.getChildren().addAll(titleText, separator, footerText);
        root.getChildren().addAll(carGroup, textContainer);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setTitle("VIS Startup");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();

        // Responsive layout
        root.widthProperty().addListener((obs, old, nw) -> {
            double w = nw.doubleValue();
            textContainer.setPrefWidth(w);
            carGroup.setLayoutX(w / 2 - 140);
        });
        root.heightProperty().addListener((obs, old, nw) -> {
            double h = nw.doubleValue();
            textContainer.setLayoutY(h * 0.6);
            carGroup.setLayoutY(h * 0.35);
        });

        // Animations
        TranslateTransition driveIn = new TranslateTransition(Duration.seconds(3), carGroup);
        driveIn.setFromX(-WIDTH);
        driveIn.setToX(0);
        driveIn.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition flash = new FadeTransition(Duration.millis(300), headlights);
        flash.setFromValue(0);
        flash.setToValue(1);
        flash.setCycleCount(4);
        flash.setAutoReverse(true);

        String fullTitle = "VEHICLE IDENTIFICATION SYSTEM";
        Timeline typewriter = new Timeline();
        for (int i = 0; i <= fullTitle.length(); i++) {
            final int k = i;
            typewriter.getKeyFrames().add(
                new KeyFrame(Duration.millis(k * 50),
                    e -> titleText.setText(fullTitle.substring(0, k)))
            );
        }

        ScaleTransition lineExpand = new ScaleTransition(Duration.seconds(1), separator);
        lineExpand.setToX(1);

        FadeTransition footerFade = new FadeTransition(Duration.seconds(2), footerText);
        footerFade.setToValue(1);

        SequentialTransition sequence = new SequentialTransition(
                driveIn, flash, typewriter, lineExpand, footerFade);
        sequence.setOnFinished(e -> loadLoginScreen());
        sequence.play();
    }

    private void loadLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/vis/fxml/LoginView.fxml"));
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}