package com.vis.controller;

import com.vis.db.UserDAO;
import com.vis.model.User;
import com.vis.util.UIUtils;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private VBox          registerFields;
    @FXML private TextField     firstNameField;
    @FXML private TextField     lastNameField;
    @FXML private TextField     emailField;
    @FXML private HBox          confirmPasswordBox;
    @FXML private VBox          confirmPasswordSpacer;
    @FXML private PasswordField confirmPasswordField;
    @FXML private HBox          errorBox;
    @FXML private Label         lblError;
    @FXML private ToggleGroup   roleGroup;
    @FXML private ToggleButton  btnRoleAdmin;
    @FXML private ToggleButton  btnRoleOfficer;
    @FXML private ToggleButton  btnRoleCustomer;
    @FXML private ToggleButton  btnRoleMechanic;
    @FXML private ToggleButton  btnRoleInsurance;
    @FXML private Label         lblFormTitle;
    @FXML private Label         lblFormSubtitle;
    @FXML private Button        btnPrimaryAction;
    @FXML private FontIcon      btnIcon;
    @FXML private Label         btnLabel;
    @FXML private Label         lblTogglePrompt;
    @FXML private Hyperlink     lnkToggleMode;
    @FXML private HBox          forgotBox;

    private boolean isRegisterMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnRoleAdmin.setSelected(true);
        styleSelectedRole(btnRoleAdmin);

        roleGroup.selectedToggleProperty().addListener((obs, old, now) -> {
            for (Toggle t : roleGroup.getToggles())
                ((ToggleButton) t).setStyle(com.vis.util.UIUtils.TOGGLE_BUTTON_STYLE);
            if (now != null) styleSelectedRole((ToggleButton) now);
        });

        DropShadow ds = new DropShadow();
        ds.setColor(Color.color(0.16, 0.50, 0.73, 0.85));
        ds.setRadius(20);
        ds.setOffsetY(5);
        ds.setSpread(0.1);
        btnPrimaryAction.setEffect(ds);

        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), btnPrimaryAction);
        fade.setFromValue(0.65);
        fade.setToValue(1.0);
        fade.setCycleCount(Animation.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();

        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try { handlePrimaryAction(); } catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try { handlePrimaryAction(); } catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        setupValidation();

        // Ensure window is resizable and centered when loaded
        usernameField.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsW, oldWindow, newWindow) -> {
                    if (newWindow instanceof Stage) {
                        Stage stage = (Stage) newWindow;
                        stage.setResizable(true);
                        stage.setMinWidth(1000);
                        stage.setMinHeight(700);
                        stage.centerOnScreen();
                    }
                });
            }
        });
    }

    private void setupValidation() {
        UIUtils.applyLetterValidation(firstNameField);
        UIUtils.applyLetterValidation(lastNameField);
        UIUtils.applyLetterValidation(usernameField);
    }

    private void styleSelectedRole(ToggleButton btn) {
        btn.setStyle(com.vis.util.UIUtils.TOGGLE_BUTTON_SELECTED_STYLE);
    }

    @FXML
    private void handleToggleMode() {
        isRegisterMode = !isRegisterMode;
        hideError();

        if (isRegisterMode) {
            lblFormTitle.setText("Create Account");
            lblFormSubtitle.setText("Fill in your details to register");
            btnLabel.setText("Register");
            btnIcon.setIconLiteral("fas-user-plus");
            lblTogglePrompt.setText("Already have an account?");
            lnkToggleMode.setText("Sign in");
            registerFields.setVisible(true);        registerFields.setManaged(true);
            confirmPasswordBox.setVisible(true);    confirmPasswordBox.setManaged(true);
            confirmPasswordSpacer.setVisible(true); confirmPasswordSpacer.setManaged(true);
            forgotBox.setVisible(false);            forgotBox.setManaged(false);
        } else {
            lblFormTitle.setText("Welcome Back");
            lblFormSubtitle.setText("Sign in to continue");
            btnLabel.setText("Sign In");
            btnIcon.setIconLiteral("fas-sign-in-alt");
            lblTogglePrompt.setText("Don't have an account?");
            lnkToggleMode.setText("Create one");
            registerFields.setVisible(false);        registerFields.setManaged(false);
            confirmPasswordBox.setVisible(false);    confirmPasswordBox.setManaged(false);
            confirmPasswordSpacer.setVisible(false); confirmPasswordSpacer.setManaged(false);
            forgotBox.setVisible(true);              forgotBox.setManaged(true);
            firstNameField.clear();
            lastNameField.clear();
            emailField.clear();
            confirmPasswordField.clear();
        }
    }

    @FXML
    private void handlePrimaryAction() throws IOException {
        if (isRegisterMode) handleRegister();
        else handleLogin();
    }

    private void handleLogin() throws IOException {
        String username     = usernameField.getText().trim();
        String password     = passwordField.getText();
        String selectedRole = getSelectedRole();

        if (username.isEmpty() || password.isEmpty()) { showError("Please enter your username and password."); return; }
        if (selectedRole == null) { showError("Please select your role."); return; }

        User user = new UserDAO().loginUser(username, password);
        if (user == null) { showError("Invalid username or password."); return; }

        if (!user.getRole().equalsIgnoreCase(selectedRole)) {
            showError("Wrong role selected. Your account is registered as: "
                    + user.getRole().substring(0, 1).toUpperCase()
                    + user.getRole().substring(1).toLowerCase());
            return;
        }

        routeUser(user, user.getRole());
    }

    private void handleRegister() {
        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        String email     = emailField.getText().trim();
        String username  = usernameField.getText().trim();
        String password  = passwordField.getText();
        String confirm   = confirmPasswordField.getText();
        String role      = getSelectedRole();

        if (firstName.isEmpty())                     { showError("Please enter your first name.");           return; }
        if (lastName.isEmpty())                      { showError("Please enter your surname.");               return; }
        if (email.isEmpty() || !email.contains("@")) { showError("Please enter a valid email.");             return; }
        if (username.isEmpty())                      { showError("Please choose a username.");                return; }
        if (password.length() < 6)                  { showError("Password must be at least 6 characters."); return; }
        if (!password.equals(confirm))               { showError("Passwords do not match.");                 return; }
        if (role == null)                            { showError("Please select a role.");                   return; }

        boolean ok = new UserDAO().registerUser(username, password, role, firstName, lastName, email);
        if (!ok) { showError("Registration failed — username may already be taken."); return; }

        User user = new UserDAO().loginUser(username, password);
        if (user != null) {
            try { routeUser(user, user.getRole()); }
            catch (IOException e) { showError("Registered! Please sign in."); handleToggleMode(); }
        } else {
            showError("Registered! Please sign in.");
            handleToggleMode();
        }
    }

    private void routeUser(User user, String role) throws IOException {
        String fxml;
        switch (role.toLowerCase()) {
            case "admin":                    fxml = "DashboardView.fxml";      break;
            case "officer": case "police":   fxml = "PolicePortalView.fxml";   break;
            case "customer":                 fxml = "CustomerPortalView.fxml"; break;
            case "mechanic": case "service": fxml = "MechanicPortalView.fxml"; break;
            case "insurance":                fxml = "InsurancePortalView.fxml"; break;
            default:
                showError("Unknown role '" + role + "'. Contact your administrator.");
                return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vis/fxml/" + fxml));
        Parent root = loader.load();

        Object ctrl = loader.getController();
        if      (ctrl instanceof DashboardController)      ((DashboardController)      ctrl).setUser(user);
        else if (ctrl instanceof CustomerPortalController) ((CustomerPortalController) ctrl).setUser(user);
        else if (ctrl instanceof PolicePortalController)   ((PolicePortalController)   ctrl).setUser(user);
        else if (ctrl instanceof MechanicPortalController) ((MechanicPortalController) ctrl).setUser(user);
        else if (ctrl instanceof InsurancePortalController) ((InsurancePortalController) ctrl).setUser(user);

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }

    @FXML private void handleForgotPassword() {
        showError("Please contact your system administrator to reset your password.");
    }

    private String getSelectedRole() {
        Toggle t = roleGroup.getSelectedToggle();
        if (t == null)            return null;
        if (t == btnRoleAdmin)    return "admin";
        if (t == btnRoleOfficer)  return "officer";
        if (t == btnRoleCustomer) return "customer";
        if (t == btnRoleMechanic) return "mechanic";
        if (t == btnRoleInsurance) return "insurance";
        return null;
    }

    private void showError(String msg) { lblError.setText(msg); errorBox.setVisible(true);  errorBox.setManaged(true);  }
    private void hideError()           { lblError.setText("");  errorBox.setVisible(false); errorBox.setManaged(false); }
}