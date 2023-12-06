package com.example.employeeattendancesystem.Controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class MarkAttendanceEmployeeCellController {
    public ChoiceBox<String> employeeStatusChoice;
    public TextField onTimeField, outTimeField, noteField;
    public Label employeeNumber, employeeName;
    private final String[] status = {"Present", "Leave", "Half Day"};
    public static ArrayList<String> employeeList = new ArrayList<>();

    public void initialize() {
        employeeStatusChoice.getItems().addAll(status);
        employeeStatusChoice.setOnAction(this::getStatus);

        onTimeField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Focus lost
                updateListWithTextFieldValues();
            }
        });

        outTimeField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Focus lost
                updateListWithTextFieldValues();
            }
        });

        noteField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Focus lost
                updateListWithTextFieldValues();
            }
        });

    }

    public  void getStatus(ActionEvent event) {
        String employeeStatus = employeeStatusChoice.getValue();
        System.out.println(employeeName.getText() + " " + employeeStatus);
        addToArray(employeeName.getText(), employeeStatus, onTimeField.getText(), outTimeField.getText(), noteField.getText());
    }

    private void updateListWithTextFieldValues() {
        // Get the current values from the TextFields
        String inTime = onTimeField.getText();
        String outTime = outTimeField.getText();
        String note = noteField.getText();

        // Call addToArray to update the employeeList with the new values
        addToArray(employeeName.getText(), employeeStatusChoice.getValue(), inTime, outTime, note);
    }

    public void addToArray(String employeeName, String employeeStatus, String inTime, String outTime, String note) {

        String employeeStatusInfo = employeeName + "_" + employeeStatus + "_" + inTime + "_" + outTime + "_" + note;

        // Search if the employee is already added to the employeeList
        boolean found = false;
        for (int i = 0; i < employeeList.size(); i++) {
            String[] parts = employeeList.get(i).split("_");

            if (parts[0].equals(employeeName)) {
                System.out.println("Found duplicate entries for the same employee");
                employeeList.remove(i);
                employeeList.add(employeeStatusInfo);
                found = true;
                break; // No need to continue searching once found
            }
        }

        if (!found) {
            // If the employee is not found, add the new entry
            employeeList.add(employeeStatusInfo);
        }

        System.out.println(employeeList);
    }

    public void saveFunction() {
        for (String employee : employeeList) {
            System.out.println(employee);
        }
    }
}
