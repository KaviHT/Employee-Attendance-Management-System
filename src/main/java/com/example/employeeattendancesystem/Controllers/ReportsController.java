package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;

public class ReportsController {
    public DatePicker atrStartDate, atrEndDate, sjrStartDate, sjrEndDate;
    public Button atrConvertBtn, sjrConvertBtn;
    public ListView<String> siteSuggestionList;
    public TextField siteSearchField;
    private final ObservableList<String> suggestions = FXCollections.observableArrayList();
    private String selectedItem;

    public void initialize() {

        Database database = new Database();

        // Add the "All" to the site options
        suggestions.add("All");

        // Populating suggestions data from the database
        suggestions.addAll(database.getSiteSearchDetails());

        // Set the list to the ListView and select "All" by default
        // siteSuggestionList.getSelectionModel().select("All");
        selectedItem = "All";

        // Update the search field to show the default selected item
        siteSearchField.setText(selectedItem);

        // Autocomplete functionality
        siteSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Clear previous suggestions
            siteSuggestionList.getItems().clear();

            // Filter and add matching suggestions
            String searchText = newValue.toLowerCase().trim();
            for (String item : suggestions) {
                if (item.toLowerCase().contains(searchText)) {
                    siteSuggestionList.getItems().add(item);
                }
            }

            // Show or hide the suggestion list based on whether there are suggestions
            siteSuggestionList.setVisible(!siteSuggestionList.getItems().isEmpty());
        });

        // Handle item selection from the suggestion list
        // ----- call the searchSite method inside this method -----
        // ----- make a way to hide the list view when you don't want to search anything -----
        siteSuggestionList.setOnMouseClicked(event -> {
            selectedItem = siteSuggestionList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                siteSearchField.setText(selectedItem);
                siteSuggestionList.setVisible(false);
                SitesCUController siteCU = new SitesCUController();
                siteCU.receiveSelectedItem(selectedItem);
            }
        });
    }

    public void atrConvertCSV(ActionEvent event) {
        if (dateInputValidation(atrStartDate, atrEndDate)) {
            String startDate = atrStartDate.getValue().toString();
            String endDate = atrEndDate.getValue().toString();
            String fileName = "Attendance_Report_" + selectedItem.replace(" ", "-") + "_";

            // ---------------------------------------------------------------
            // Implementation for getting the CSV conversion from the database
            // ---------------------------------------------------------------

            filePathSelection(startDate, endDate, "Attendance Report", fileName);
        }
    }

    public void sjrConvertCSV(ActionEvent event) {
        if (dateInputValidation(sjrStartDate, sjrEndDate)) {
            String startDate = sjrStartDate.getValue().toString();
            String endDate = sjrEndDate.getValue().toString();

            // ---------------------------------------------------------------
            // Implementation for getting the CSV conversion from the database
            // ---------------------------------------------------------------

            filePathSelection(startDate, endDate, "Special Job Report", "Special_Job_Report_");
        }
    }

    private void filePathSelection(String startDate, String endDate, String title, String fileName) {
        // Open the file path selection window
        FileChooser fileChooser = new FileChooser();
        // Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        // Set a default file name
        fileChooser.setInitialFileName(fileName + startDate + "_to_" + endDate + ".csv");

        // Show save file dialog
        Stage stage = (Stage) atrConvertBtn.getScene().getWindow();
        fileChooser.setTitle("Save " + title);
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {

            // -----------------------------------------------------
            // Implement file saving logic here using file.getPath()
            // -----------------------------------------------------

            System.out.println("Save path selected: " + file.getPath());
        }
    }

    private boolean dateInputValidation(DatePicker startDatePicker, DatePicker endDatePicker) {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        LocalDate now = LocalDate.now();

        // Validation process
        if (startDate == null || endDate == null) {
            showAlert("Date inputs cannot be empty.");
            return false;
        } else if (startDate.isAfter(now) || endDate.isAfter(now)) {
            showAlert("Dates cannot be in the future.");
            return false;
        } else if (startDate.isAfter(endDate)) {
            showAlert("Start date cannot be after end date.");
            return false;
        }
        return true;    // Validation passed
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
