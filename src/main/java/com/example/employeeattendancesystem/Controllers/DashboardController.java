package com.example.employeeattendancesystem.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;


public class DashboardController {
    public Button sitesBtn, employeeBtn, attendanceBtn, logoutBtn, backupBtn;
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToLogin(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/Fxml/Login.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToEmployees(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/Fxml/EmployeeRD.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Employees");
        stage.setScene(scene);
        stage.show();
    }

    public void switchToSites(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/Fxml/SitesRD.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Sites");
        stage.setScene(scene);
        stage.show();
    }

    public void switchToAttendance(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/Fxml/ViewFactory/AttendanceDashboardView.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Attendance Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    public void backupDatabase() {
        // Creating a confirmation alert to get the confirmation form the user
        Alert backupConfirm = new Alert(Alert.AlertType.CONFIRMATION);
        backupConfirm.setTitle("Confirmation");
        backupConfirm.setHeaderText("Backup System Database");
        backupConfirm.setContentText("Do you want to save a backup of the database?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        backupConfirm.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = backupConfirm.showAndWait();

        // Handle the user's response
        if (result.isPresent() && result.get() == yesButton) {  // User clicked OK
            LocalDate backupDate = LocalDate.now();

            // ---------------------------------------------------------------
            // Implementation for getting backup from the Database
            // ---------------------------------------------------------------

            filePathSelection(backupDate);
        } else {
            // User clicked Cancel or closed the alert
        }
    }

    private void filePathSelection(LocalDate backupDate) {
        // Open the file path selection window
        FileChooser fileChooser = new FileChooser();
        // Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        // Set a default file name
        fileChooser.setInitialFileName(backupDate + "_Backup" + ".csv");

        // Show save file dialog
        Stage stage = (Stage) backupBtn.getScene().getWindow();
        fileChooser.setTitle("Save Backup");
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {

            // -----------------------------------------------------
            // Implement file saving logic here using file.getPath()
            // -----------------------------------------------------

            System.out.println("Save path selected: " + file.getPath());
        }
    }
}
