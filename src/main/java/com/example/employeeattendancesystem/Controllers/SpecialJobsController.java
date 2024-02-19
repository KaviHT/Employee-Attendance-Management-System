package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.Database;
import com.example.employeeattendancesystem.Utils.MongoDBConnection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.time.LocalDate;

public class SpecialJobsController {
    public AnchorPane anchorPane;
    public DatePicker pickedDate;
    public TextField siteNameField, employeeSearchField, onTimeField, outTimeField, noteField;
    public ListView<String> employeeList;
    public ListView<AnchorPane> summaryList;
    public Button addBtn;
    private final ObservableList<String> suggestions = FXCollections.observableArrayList();


    MongoDatabase database = MongoDBConnection.getDatabase("attendence_db");
    MongoCollection<Document> SpecialJobDataCollection = database.getCollection("special_jobs");
    MongoCollection<Document> SpecialJobSummaryCollection = database.getCollection("special_jobs");


    public void initialize() throws IOException {
        loadSpecialJobs();

        Database database = new Database();

        // Populating suggestions data from the database
        suggestions.addAll(database.getEmployeeSearchDetails());  // <--- add site names from the database here

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

        anchorPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (!event.getTarget().equals(employeeSearchField) && !event.getTarget().equals(employeeList)) {
                employeeList.setVisible(false);
            }
        });
    }

    public void saveSpecialJobs() throws IOException {

        // Get the values from the fields
        String date = pickedDate.getValue().toString();
        String siteName = siteNameField.getText();
        String employeeName = employeeSearchField.getText();
        String inTime = onTimeField.getText().isEmpty() ? "N/A" : onTimeField.getText();
        String outTime = outTimeField.getText().isEmpty() ? "N/A" : outTimeField.getText();
        String note = noteField.getText().isEmpty() ? "N/A" : noteField.getText();

        // Create a new Document
        Document doc = new Document("date", date)
                .append("siteName", siteName)
                .append("employeeName", employeeName)
                .append("inTime", inTime)
                .append("outTime", outTime)
                .append("note", note);

        // Add the document to the collection
        SpecialJobDataCollection.insertOne(doc);

        // Information alert for the user to indicate creation of a special job
        Alert siteCreated = new Alert(Alert.AlertType.INFORMATION);
        siteCreated.setTitle("New Special Job Added");
        siteCreated.setHeaderText(null); // No header text
        siteCreated.setContentText("New Special Job Created Successfully");

        siteCreated.showAndWait();
        loadSpecialJobs();
    }

    private void loadSpecialJobs() throws IOException {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        Bson filter = Filters.and(
                Filters.gte("date", startOfMonth.toString()),
                Filters.lte("date", endOfMonth.toString())
        );
        FindIterable<Document> specialJobs = SpecialJobDataCollection.find(filter);
        var items = FXCollections.<AnchorPane>observableArrayList();
        for (Document job : specialJobs) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Cells/SpecialJobEmployeeCell.fxml"));
            Parent cell = loader.load();

            SpecialJobEmployeeCellController cellController = loader.getController();

            // Set the details for each job
            cellController.employeeNameLbl.setText(job.getString("employeeName"));
            cellController.siteNameLbl.setText(job.getString("siteName"));
            cellController.dateLbl.setText(job.getString("date"));
            cellController.inTimeLbl.setText(job.getString("inTime"));
            cellController.outTimeLbl.setText(job.getString("outTime"));

            items.add((AnchorPane) cell);
        }
        summaryList.setItems(items);
    }
}