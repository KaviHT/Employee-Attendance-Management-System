package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.Database;
import com.example.employeeattendancesystem.Utils.MongoDBConnection;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;


public class MarkAttendanceSiteController {

    public Button backBtn, saveBtn, addReplacementBtn;
    public ListView<AnchorPane> employeeList;
    public Label siteNameLbl, msgEmployeeLbl, msgAttendanceLbl, dateLbl;
    public TextField employeeSearchField;
    public ListView<String> employeeSuggestionList;
    private final ObservableList<String> suggestions = FXCollections.observableArrayList();
    private LocalDate date;
    private MarkAttendanceEmployeeCellController cellController = null;
    MongoDBConnection mongoDBConnection = new MongoDBConnection();
    MongoDatabase Database = mongoDBConnection.getDatabase("attendence_db");
    MongoCollection<Document> AtteEmpCollection = Database.getCollection("site");


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

        String[] parts = siteName.split(" ");
        String siteID = parts[0];

        Document siteDoc = AtteEmpCollection.find(Filters.eq("site_details",siteName)).first();

        // Get the employees string and split it into an array of employee IDs
        String[] employeeIds = siteDoc.getString("employees").split(",");

        // Iterate over each employee ID
        for (int i = 0; i < employeeIds.length; i++) {
            // Load the employee cell FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Cells/MarkAttendanceEmployeeCell.fxml"));
            Parent employeeCell = loader.load();

            // Get the controller for the employee cell
            cellController = loader.getController();

            // Set the employee details in the text fields
            cellController.employeeNumber.setText(String.valueOf(i+1));
            cellController.employeeName.setText(employeeIds[i]);

            // Add the employee cell to the ListView
            employeeList.getItems().add((AnchorPane) employeeCell);

        }
        Database database = new Database();

        /*
        CHECK IF EMPLOYEE REPLACEMENT WERE ADDED TODAY FROM THE DATABASE
        CHECK IF ALREADY MARKED
         */

        // Populating suggestions data from the database
        suggestions.addAll(database.getEmployeeSearchDetails());  // <--- add the database here

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
                MarkAttendanceEmployeeCellController cellController = loader.getController();

                cellController.employeeNumber.setText("X");
                cellController.employeeName.setText(selectedItem+" (rep)");
                cellController.employeeStatusChoice.setValue("Replacement");

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

    public void save(ActionEvent event) throws IOException {
        Alert saveConfirm = new Alert(Alert.AlertType.CONFIRMATION);
        saveConfirm.setTitle("Confirmation");
        saveConfirm.setHeaderText("Save Attendance");
        saveConfirm.setContentText("Do you want to save the marked attendance records?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        saveConfirm.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = saveConfirm.showAndWait();

        // Handle the user's response
        if (result.isPresent() && result.get() == yesButton) {
            cellController.saveFunction();
            switchToMarkAttendance(event);
        } else {
            // User clicked Cancel or closed the alert
        }
    }
}
