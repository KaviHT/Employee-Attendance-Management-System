package com.example.employeeattendancesystem.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
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
            filePathSelection(backupDate);
        } else {
            // User clicked Cancel or closed the alert
        }
    }

    private void filePathSelection(LocalDate backupDate) {
        // Open the directory path selection window
        // Open the directory path selection window
        DirectoryChooser directoryChooser = new DirectoryChooser();

        // Set initial directory
        File initialDirectory = new File(System.getProperty("user.home"));
        directoryChooser.setInitialDirectory(initialDirectory);

        // Show save directory dialog
        Stage stage = (Stage) backupBtn.getScene().getWindow();
        directoryChooser.setTitle("Save Backup");
        File directory = directoryChooser.showDialog(stage);

        if (directory != null) {
            try {
                String backupPath = directory.getPath() + File.separator + backupDate + "_Backup";
                String mongodumpPath = "C:\\Program Files\\MongoDB\\Tools\\100\\bin\\mongodump";
                ProcessBuilder processBuilder = new ProcessBuilder(mongodumpPath, "--db", "attendence_db", "--out", backupPath);
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("Backup created successfully at " + backupPath);
                } else {
                    System.out.println("Backup creation failed with exit code " + exitCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}}
