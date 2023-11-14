package com.example.employeeattendancesystem.Controllers.testControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class testAttendanceMarkController {

    @FXML
    private ListView<AnchorPane> employeeListView; // Reference to the ListView in FXML

    @FXML
    private Button addEmployeeBtn;

    @FXML
    void addEmployeeCell(ActionEvent event) {
        try {
            // Load the employee cell FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/test/testEmployeeCell.fxml"));
            Parent employeeCell = loader.load();

            // Get the controller for the employee cell
            testEmployeeCellController cellController = loader.getController();

            // You can customize the employee cell here if needed

            // Add the employee cell to the ListView
            employeeListView.getItems().add((AnchorPane) employeeCell);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
