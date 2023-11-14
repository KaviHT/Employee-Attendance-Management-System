package com.example.employeeattendancesystem.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class SitesCUController {

    public Button backBtn, saveSiteBtn;
    public TextField siteIDField, siteNameField, employeeSearchField, startTimeField, finishTimeField;
    public Label titleLbl, siteSupervisorLbl, displayLbl;
    public ListView<String> siteEmployeeList;
    public ListView<String> employeeSuggestionList;
    private final ObservableList<String> employeeSuggestions = FXCollections.observableArrayList();
    public ToggleGroup addingStatus;
    public RadioButton employeeRBtn, supervisorRBtn;


    public void setStageTitle(String windowTitle) {
        if (windowTitle.equals("Create Site")) {
            titleLbl.setText("Create a New Site");
        } else if (windowTitle.equals("Edit Site")) {
            titleLbl.setText("Edit Site");
        }
    }

    public void initialize() {
        employeeSuggestions.addAll("Brian", "Kavindu", "Sayura", "Movindu");

        // Autocomplete functionality
        employeeSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Clear previous suggestions
            employeeSuggestionList.getItems().clear();

            // Filter and add matching suggestions
            String searchText = newValue.toLowerCase().trim();
            for (String item : employeeSuggestions) {
                if (item.toLowerCase().contains(searchText)) {
                    employeeSuggestionList.getItems().add(item);
                }
            }

            // Show or hide the suggestion list based on whether there are suggestions
            employeeSuggestionList.setVisible(!employeeSuggestionList.getItems().isEmpty());
        });

        getSelectedPerson();

        siteEmployeeList.setOnMouseClicked(event -> {
            String selectedEmployee = siteEmployeeList.getSelectionModel().getSelectedItem();
            siteEmployeeList.getItems().remove(selectedEmployee);
        });

    }

    public void switchToSiteRD(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/SitesRD.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Sites");
        stage.setScene(scene);
        stage.show();
    }

    public String getSelectedPerson() {
        if (employeeRBtn.isSelected()) {
            displayLbl.setText("Employees:");
            // Handle item selection from the suggestion list
            // ----- make a way to hide the list view when you don't want to search anything -----
            employeeSuggestionList.setOnMouseClicked(event -> {
                String selectedItem = employeeSuggestionList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {

                    if (!siteEmployeeList.getItems().contains(selectedItem)) {
                        siteEmployeeList.getItems().add(selectedItem);
                    } else {
                        System.out.println("That employee is already added");
                    }

                }
            });
            return "E";
        } else if (supervisorRBtn.isSelected()) {
            displayLbl.setText("a Supervisor:");
            employeeSuggestionList.setOnMouseClicked(event -> {
                String selectedItem = employeeSuggestionList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    siteSupervisorLbl.setText(selectedItem);
                }
            });
            return "S";
        }
        return null;
    }
}
