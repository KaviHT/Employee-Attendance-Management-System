package com.example.employeeattendancesystem.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class MarkAttendanceSiteController {

    public Button backBtn, saveBtn, addReplacementBtn;
    public ListView<AnchorPane> employeeList;
    public Label siteNameLbl, msgEmployeeLbl, msgAttendanceLbl, dateLbl;
    public TextField employeeSearchField;
    public ListView<String> employeeSuggestionList;
    private final ObservableList<String> suggestions = FXCollections.observableArrayList();
    private LocalDate date;
    private MarkAttendanceEmployeeCellController cellController = null;


    public void initialize() throws IOException {
        date = LocalDate.now();
        msgAttendanceLbl.setVisible(false);
        dateLbl.setVisible(false);

        // Getting site name form DummyController
        String siteName = DummyController.getSiteName();
        siteNameLbl.setText(siteName);

        if (DummyController.getEditStatus()) {
            msgAttendanceLbl.setVisible(true);
            dateLbl.setVisible(true);

            date = DummyController.getSelectedDate();
            dateLbl.setText(String.valueOf(date));

            /*
            ADD ALL PAST RECORDS OF ATTENDANCE FROM DATABASE
             */
        }

        System.out.println(date);


        for (int i=0; i<3; i++) {
            // Load the employee cell FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Cells/MarkAttendanceEmployeeCell.fxml"));
            Parent employeeCell = loader.load();

            // Get the controller for the employee cell
            cellController = loader.getController();

            cellController.employeeNumber.setText(String.valueOf(i+1));
            cellController.employeeName.setText("Employee Number " + (i+1));

            // Add the employee cell to the ListView
            employeeList.getItems().add((AnchorPane) employeeCell);
        }

        /*
        CHECK IF EMPLOYEE REPLACEMENT WERE ADDED TODAY FROM THE DATABASE
        CHECK IF ALREADY MARKED
         */

        // Populating suggestions data from the database
        suggestions.addAll("Kavindu", "Brian", "Movindu", "Sayura");  // <--- add the database here

        // Autocomplete functionality
        employeeSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Clear previous suggestions
            employeeSuggestionList.getItems().clear();

            // Filter and add matching suggestions
            String searchText = newValue.toLowerCase().trim();
            for (String item : suggestions) {
                if (item.toLowerCase().contains(searchText)) {
                    employeeSuggestionList.getItems().add(item);
                }
            }

            // Show or hide the suggestion list based on whether there are suggestions
            employeeSuggestionList.setVisible(!employeeSuggestionList.getItems().isEmpty());
        });

        // Handle item selection from the suggestion list
        // ----- call the searchEmployee method inside this method -----
        // ----- make a way to hide the list view when you don't want to search anything -----
        employeeSuggestionList.setOnMouseClicked(event -> {
            String selectedItem = employeeSuggestionList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                employeeSearchField.setText(selectedItem);
                employeeSuggestionList.setVisible(false);

                // Load the employee cell FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Cells/MarkAttendanceEmployeeCell.fxml"));
                Parent employeeCell = null;
                try {
                    employeeCell = loader.load();
                } catch (IOException ignored) {}

                // Get the controller for the employee cell
                MarkAttendanceEmployeeCellController cellController1 = loader.getController();

                cellController1.employeeNumber.setText("X");
                cellController1.employeeName.setText(selectedItem);

                // Add the employee cell to the ListView
                employeeList.getItems().add((AnchorPane) employeeCell);

            }
        });
    }

    public void addReplacementEmployee() {
        addReplacementBtn.setVisible(false);
        msgEmployeeLbl.setVisible(true);
        employeeSearchField.setVisible(true);
    }

    public void switchToMarkAttendance(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/ViewFactory/MarkAttendanceView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Mark Attendance");
        stage.setScene(scene);
        stage.show();
    }

    public void save() {
        cellController.saveFunction();
    }

}
