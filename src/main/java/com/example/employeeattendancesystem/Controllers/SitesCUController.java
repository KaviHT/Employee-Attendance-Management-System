package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.Database;
import com.example.employeeattendancesystem.Utils.MongoDBConnection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
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
import com.mongodb.client.model.Updates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class SitesCUController {

    public Button backBtn, saveSiteBtn;
    public TextField siteIDField, siteNameField, employeeSearchField, startTimeField, finishTimeField;
    public Label titleLbl, siteSupervisorLbl, displayLbl;
    public ListView<String> siteEmployeeList;
    public ListView<String> employeeSuggestionList;
    private final ObservableList<String> employeeSuggestions = FXCollections.observableArrayList();
    public ToggleGroup addingStatus;
    public RadioButton employeeRBtn, supervisorRBtn;
    private static String theselectItem;


    public void setStageTitle(String windowTitle) {
        if (windowTitle.equals("Create Site")) {
            titleLbl.setText("Create a New Site");
        } else if (windowTitle.equals("Edit Site")) {
            titleLbl.setText("Edit Site");

            String siteNumber = theselectItem.split(" ")[0];

            MongoDBConnection mongoDBConnection = new MongoDBConnection();

            MongoDatabase Database = mongoDBConnection.getDatabase("attendence_db");
            MongoCollection<Document> siteCollection = Database.getCollection("site");

            FindIterable<Document> documents = siteCollection.find(eq("site_id", siteNumber));

            for (Document document : documents) {
                String siteId = document.getString("site_id");
                String siteName = document.getString("site_name");
                String workingHours = document.getString("working_hours");
                String supDetails = document.getString("sup_details");
                String siteEmployees = document.getString("employees");

                String[] hours = workingHours.split("-");
                String startHour = hours[0];
                String endHour = hours[1];

                siteIDField.setText(siteId);
                siteNameField.setText(siteName);
                siteSupervisorLbl.setText(supDetails);

                startTimeField.setText(startHour);
                finishTimeField.setText(endHour);
                ObservableList<String> employeeList = FXCollections.observableArrayList(siteEmployees.split(","));
                siteEmployeeList.setItems(employeeList);
            }
        }
    }

    public void initialize() {
        Database databaseSite = new Database();

        employeeSuggestions.addAll(databaseSite.getEmployeeSearchDetails());

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

    public void receiveSelectedItem(String selectedItem) {
        theselectItem = selectedItem;
    }

    public void saveSite(ActionEvent event) {

        MongoDBConnection mongoDBConnection = new MongoDBConnection();

        MongoDatabase Database = mongoDBConnection.getDatabase("attendence_db");
        MongoCollection<Document> newSiteCollection = Database.getCollection("site");
        MongoCollection<Document> siteSupCollection = Database.getCollection("siteSupervisor");

        String startTime = startTimeField.getText();
        String finishTime = finishTimeField.getText();
        String workingHours = startTime + "-" + finishTime;

        String siteID = siteIDField.getText();
        String siteName=siteNameField.getText();
        String fullSiteName= siteID+ " " +siteName;

        if (titleLbl.getText().equals("Create a New Site")) {


            StringBuilder sb = new StringBuilder();
            List<String> employeeNames = new ArrayList<>();
            for (Object employee : siteEmployeeList.getItems()) {
                employeeNames.add(employee.toString());
            }
            for (int i = 0; i < employeeNames.size(); i++) {
                sb.append(employeeNames.get(i));
                if (i != employeeNames.size() - 1) {
                    sb.append(",");
                }
            }

            String siteEmployee = sb.toString();

            Document siteDoc = new Document("site_id", siteIDField.getText())
                    .append("site_name", siteNameField.getText())
                    .append("working_hours", workingHours)
                    .append("sup_details", siteSupervisorLbl.getText())
                    .append("employees", siteEmployee)
                    .append("site_details",fullSiteName);

            newSiteCollection.insertOne(siteDoc);

            Document supervisor = siteSupCollection.find(Filters.eq("supName", siteSupervisorLbl.getText())).first();
            if (supervisor == null) {
                // Supervisor does not exist, create a new document
                List<String> sites = new ArrayList<>();
                sites.add(fullSiteName);
                Document newSupervisor = new Document("supName", siteSupervisorLbl.getText())
                        .append("sites", sites);
                siteSupCollection.insertOne(newSupervisor);
            } else {
                // Supervisor exists
                Object sitesField = supervisor.get("sites");
                if (sitesField instanceof String) {
                    // 'sites' is a string, convert it to an array
                    List<String> sites = new ArrayList<>();
                    sites.add((String) sitesField);
                    siteSupCollection.updateOne(Filters.eq("supName", siteSupervisorLbl.getText()), Updates.set("sites", sites));
                }
                // Add the new site to the existing document
                siteSupCollection.updateOne(Filters.eq("supName", siteSupervisorLbl.getText()), Updates.addToSet("sites", fullSiteName));
            }

        } else if (titleLbl.getText().equals("Edit Site")) {

            StringBuilder sb = new StringBuilder();
            List<String> employeeNames = new ArrayList<>();
            for (Object employee : siteEmployeeList.getItems()) {
                employeeNames.add(employee.toString());
            }
            for (int i = 0; i < employeeNames.size(); i++) {
                sb.append(employeeNames.get(i));
                if (i != employeeNames.size() - 1) {
                    sb.append(",");
                }
            }
            String siteEmployee = sb.toString();

            Document existingSite = newSiteCollection.find(Filters.eq("site_id", siteIDField.getText())).first();
            // update the employee details
            existingSite.put("site_name", siteNameField.getText());
            existingSite.put("working_hours", workingHours);
            existingSite.put("sup_details", siteSupervisorLbl.getText());
            existingSite.put("employees", siteEmployee);
            existingSite.put("site_details",fullSiteName);

            Document supervisor = siteSupCollection.find(Filters.eq("supName", siteSupervisorLbl.getText())).first();
            if (supervisor == null) {
                // Supervisor does not exist, create a new document
                List<String> sites = new ArrayList<>();
                sites.add(fullSiteName);
                Document newSupervisor = new Document("supName", siteSupervisorLbl.getText())
                        .append("sites", sites);
                siteSupCollection.insertOne(newSupervisor);
            } else {
                // Supervisor exists
                List<String> sites;
                Object sitesField = supervisor.get("sites");
                if (sitesField instanceof List) {
                    // 'sites' is a list, use it directly
                    sites = (List<String>) sitesField;
                } else {
                    // 'sites' is not a list, convert it to a list
                    sites = new ArrayList<>();
                    sites.add(sitesField.toString());
                }
                // Add the new site to the existing list
                if (!sites.contains(fullSiteName)) {
                    sites.add(fullSiteName);
                }

                siteSupCollection.updateOne(Filters.all("sites", fullSiteName), Updates.pull("sites", fullSiteName));

                // Update the 'sites' field in the database
                siteSupCollection.updateOne(Filters.eq("supName", siteSupervisorLbl.getText()), Updates.set("sites", sites));

            }

            newSiteCollection.replaceOne(Filters.eq("site_id", siteIDField.getText()), existingSite);
        }
    }
}