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
import javafx.stage.Stage;

import java.io.IOException;

public class SitesRDController {

    public Button backBtn, searchBtn, createBtn, editBtn, deleteBtn;
    public TextField siteSearchField;
    public ListView<String> siteSuggestionList;
    public ListView<String> siteEmployeeList;
    public Label siteIDLbl, siteNameLbl, siteSupervisorLbl, workingHoursLbl;
    private final ObservableList<String> suggestions = FXCollections.observableArrayList();


    public void initialize() {
        // Populating suggestions data from the database
        suggestions.addAll("Abans", "Pizza Hut", "Barista", "IIT", "SLIIT");  // <--- add site names from the database here

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
            String selectedItem = siteSuggestionList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                siteSearchField.setText(selectedItem);
                siteSuggestionList.setVisible(false);
            }
        });
    }


    public void searchSite(ActionEvent event) {

    }

    public void switchToDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Dashboard.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Shadow - Employee Management System");
        stage.setScene(scene);
        stage.show();
    }

    public void createSite(ActionEvent event) throws IOException {
        openSiteCUWindow(event, "Create Site");
    }

    public void editSite(ActionEvent event) throws IOException {
        openSiteCUWindow(event, "Edit Site");
    }

    public void deleteSite(ActionEvent event) {
        // ask for confirmation
    }

    public void openSiteCUWindow(ActionEvent event, String windowTitle) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/SitesCU.fxml"));
        Parent root = loader.load();

        SitesCUController sitesCUController = loader.getController();
        sitesCUController.setStageTitle(windowTitle);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle(windowTitle);
        stage.setScene(scene);
        stage.show();
    }


}
