package com.example.employeeattendancesystem.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class EmployeeCUController {

    public Button backBtn, saveEmployeeBtn;
    public TextField employeeNumberField, firstNameField, lastNameField, addressField, contactNumberField;
    public DatePicker dobField;
    public ToggleGroup gender;
    public RadioButton maleRBtn, femaleRBtn, otherRBtn;
    public Label titleLbl;
    private Stage stage;
    private Scene scene;
    private Parent root;


    public void initialize() {
        if (titleLbl.equals("Edit Employee")) {

        }
    }

    public void setStageTitle(String windowTitle) {
        if (windowTitle.equals("Create Employee")) {
            titleLbl.setText("Create a New Employee");
        } else if (windowTitle.equals("Edit Employee")) {
            titleLbl.setText("Edit Employee");
        }
    }

    public void saveEmployee(ActionEvent event) {
        if (titleLbl.getText().equals("Create a New Employee")) {

        } else if (titleLbl.getText().equals("Edit Employee")) {

        }
    }

    public void switchToEmployeeRD(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/Fxml/EmployeeRD.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Employees");
        stage.setScene(scene);
        stage.show();
    }
}
