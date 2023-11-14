package com.example.employeeattendancesystem.Controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MarkAttendanceEmployeeCellController {
    public ChoiceBox<String> employeeStatusChoice;
    public TextField onTimeField, outTimeField, noteField;
    public Label employeeNumber, employeeName;
    private final String[] status = {"Present", "Leave", "Half Day"};

    public void initialize() {
        employeeStatusChoice.getItems().addAll(status);
        employeeStatusChoice.setOnAction(this::getStatus);
    }

    public  void getStatus(ActionEvent event) {
        String employeeStatus = employeeStatusChoice.getValue();
        System.out.println(employeeName.getText() + " " + employeeStatus);
    }

}
