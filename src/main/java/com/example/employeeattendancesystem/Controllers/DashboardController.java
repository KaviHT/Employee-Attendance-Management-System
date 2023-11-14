package com.example.employeeattendancesystem.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {
    public Button sitesBtn, employeeBtn, attendanceBtn, logoutBtn;
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

}
