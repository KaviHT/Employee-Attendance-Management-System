package com.example.employeeattendancesystem.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AttendanceMenuController {
    public Button attendanceDashboardBtn, markAttendanceBtn, summaryBtn,specialJobsBtn, reportsBtn, backBtn;
    private Stage stage;
    private Scene scene;
    private Parent root;


    public void switchToAttendanceDashboard(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/Fxml/ViewFactory/AttendanceDashboardView.fxml"));
        scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    public void switchToMarkAttendance(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/Fxml/ViewFactory/MarkAttendanceView.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Mark Attendance");
        stage.setScene(scene);
        stage.show();
    }

    public void switchToSummary(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/Fxml/ViewFactory/SummaryView.fxml"));
        scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Summary");
        stage.setScene(scene);
        stage.show();
    }

    public void switchToSpecialJobs(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/Fxml/ViewFactory/SpecialJobsView.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Special Jobs");
        stage.setScene(scene);
        stage.show();
    }

    public void switchToReports(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/Fxml/ViewFactory/ReportsView.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Reports");
        stage.setScene(scene);
        stage.show();
    }

    public void switchToDashboard(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/Fxml/Dashboard.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Attendance");
        stage.setScene(scene);
        stage.show();
    }

}
