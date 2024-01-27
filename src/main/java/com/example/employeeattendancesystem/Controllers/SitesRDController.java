package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.Database;
import com.example.employeeattendancesystem.Utils.MongoDBConnection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class SitesRDController {

    public Button backBtn, searchBtn, createBtn, editBtn, deleteBtn;
    public TextField siteSearchField;
    public ListView<String> siteSuggestionList;
    public ListView<String> siteEmployeeList;
    public Label siteIDLbl, siteNameLbl, siteSupervisorLbl, workingHoursLbl;
    private final ObservableList<String> suggestions = FXCollections.observableArrayList();
    MongoDatabase database = MongoDBConnection.getDatabase("attendence_db");
    MongoCollection<Document> SiteDataCollection = database.getCollection("site");
    MongoCollection<Document> siteSupCollection = database.getCollection("siteSupervisor");
    public void initialize() {

        Database database = new Database();

        // Populating suggestions data from the database
        suggestions.addAll(database.getSiteSearchDetails());

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
                SitesCUController siteCU = new SitesCUController();
                siteCU.receiveSelectedItem(selectedItem);
            }
        });
    }

    public void searchSite(ActionEvent event) {

        String empNumber = siteSearchField.getText().split(" ")[0];

        FindIterable<Document> documents = SiteDataCollection.find(eq("site_id", empNumber));
        for (Document document : documents) {
            String siteId = document.getString("site_id");
            String siteName = document.getString("site_name");
            String workingHours = document.getString("working_hours");
            String supDetails = document.getString("sup_details");
            String empList = document.getString("employees");

            siteIDLbl.setText(siteId);
            siteNameLbl.setText(siteName);
            siteSupervisorLbl.setText(supDetails);
            workingHoursLbl.setText(workingHours);
            siteEmployeeList.getItems().add(empList);

            siteEmployeeList.getItems().clear();

            // Split the employee list and add each employee to the ListView
            String[] employees = empList.split(",");
            for (String employee : employees) {
                siteEmployeeList.getItems().add(employee);
            }
        }
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
        // Creating a confirmation alert to get the confirmation form the user
        Alert deleteConfirm = new Alert(Alert.AlertType.CONFIRMATION);
        deleteConfirm.setTitle("Confirmation");
        deleteConfirm.setHeaderText("Delete Site");
        deleteConfirm.setContentText("Delete " + siteNameLbl.getText() + " from the site list?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        deleteConfirm.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = deleteConfirm.showAndWait();

        // Handle the user's response
        if (result.isPresent() && result.get() == yesButton) {  // User clicked OK
            String siteID = siteIDLbl.getText();
            String siteName=siteNameLbl.getText();
            String fullSiteName= siteID+ " " +siteName;

            String SiteNumber = siteSearchField.getText().split(" ")[0];

            // Delete the document from the collection
            SiteDataCollection.deleteOne(eq("site_id", SiteNumber));

            siteSupCollection.updateOne(Filters.all("sites", fullSiteName), Updates.pull("sites", fullSiteName));

            // Clear the site details from the UI
            siteIDLbl.setText("");
            siteNameLbl.setText("");
            siteSupervisorLbl.setText("");
            workingHoursLbl.setText("");
            ObservableList<String> emptyList = FXCollections.observableArrayList();
            siteEmployeeList.setItems(emptyList);
        } else {
            // User clicked Cancel or closed the alert
        }
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
