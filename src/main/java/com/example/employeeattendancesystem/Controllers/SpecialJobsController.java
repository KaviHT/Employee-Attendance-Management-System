package com.example.employeeattendancesystem.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SpecialJobsController {
    public DatePicker pickedDate;
    public TextField siteNameField, employeeSearchField, onTimeField, outTimeField, noteField;
    public ListView<String> employeeList;
    public ListView<AnchorPane> summaryList;
    public Button addBtn;
    private final ObservableList<String> suggestions = FXCollections.observableArrayList();


    public void initialize() throws IOException {
        // Create a list of items to populate the ListView
        var items = FXCollections.<AnchorPane>observableArrayList();

        for (int i=0; i<5; i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Cells/SpecialJobEmployeeCell.fxml"));
            Parent cell = loader.load();

            SpecialJobEmployeeCellController cellController = loader.getController();

            cellController.employeeNumLbl.setText(String.valueOf(i+1));
            cellController.employeeNameLbl.setText("Employee Name");
            cellController.siteNameLbl.setText("Site Name");
            cellController.dateLbl.setText("date");

            items.add((AnchorPane) cell);
        }

        // Set the items in the ListView
        summaryList.setItems(items);


        // Populating suggestions data from the database
        suggestions.addAll("Brian", "Sayura", "Kaivndu", "Movindu");  // <--- add site names from the database here

        // Autocomplete functionality
        employeeSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Clear previous suggestions
            employeeList.getItems().clear();

            // Filter and add matching suggestions
            String searchText = newValue.toLowerCase().trim();
            for (String item : suggestions) {
                if (item.toLowerCase().contains(searchText)) {
                    employeeList.getItems().add(item);
                }
            }

            // Show or hide the suggestion list based on whether there are suggestions
            employeeList.setVisible(!employeeList.getItems().isEmpty());
        });

        // Handle item selection from the suggestion list
        // ----- call the searchSite method inside this method -----
        // ----- make a way to hide the list view when you don't want to search anything -----
        employeeList.setOnMouseClicked(event -> {
            String selectedItem = employeeList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                employeeSearchField.setText(selectedItem);
                employeeList.setVisible(false);
            }
        });
    }

}
