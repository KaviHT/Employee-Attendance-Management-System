package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.Database;
import com.example.employeeattendancesystem.Utils.MongoDBConnection;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Optional;


public class MarkAttendanceSiteController {

    public Button backBtn, saveBtn, addReplacementBtn;
    public AnchorPane anchorPane;
    public ListView<AnchorPane> employeeList;
    public Label siteNameLbl, msgEmployeeLbl, msgAttendanceLbl, dateLbl, errorLbl;
    public TextField employeeSearchField;
    public ListView<String> employeeSuggestionList;
    private final ObservableList<String> suggestions = FXCollections.observableArrayList();
    private LocalDate date;
    private MarkAttendanceEmployeeCellController cellController = null;
    MongoDBConnection mongoDBConnection = new MongoDBConnection();
    MongoDatabase Database = mongoDBConnection.getDatabase("attendence_db");
    MongoCollection<Document> AtteEmpCollection = Database.getCollection("site");
    MongoCollection<Document> RepDelCollection = Database.getCollection("attendence");
    MongoCollection<Document> RepDelEmpCollection = Database.getCollection("EmployeeAttendance");


    public void initialize() throws IOException {
        date = LocalDate.now();
        msgAttendanceLbl.setVisible(false);
        dateLbl.setVisible(false);

        // Getting site name form DummyController
        String siteName = DummyController.getSiteName();
        siteNameLbl.setText(siteName);

        // Checking if the user is editing or not
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

                // Query the database
                Document query = new Document("site_details", siteName);
                Document document = AtteEmpCollection.find(query).first();

                if (document != null) {
                    String employees = document.getString("employees");
                    if (employees.contains(selectedItem)) {
                        errorLbl.setVisible(true);
                        errorLbl.setText("The selected employee is already in the site.");
                        System.out.println("The selected employee is already in the site.");
                    } else {
                        errorLbl.setVisible(false);
                        employeeSearchField.setText(selectedItem);
                        employeeSuggestionList.setVisible(false);

                        // Load the employee cell FXML
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Cells/MarkAttendanceEmployeeCell.fxml"));
                        Parent employeeCell = null;
                        try {
                            employeeCell = loader.load();
                        } catch (IOException ignored) {
                        }

                        // Get the controller for the employee cell
                        MarkAttendanceEmployeeCellController cellController = loader.getController();

                        cellController.employeeNumber.setText("rep");
                        cellController.employeeName.setText(selectedItem + " (rep)");
                        cellController.employeeStatusChoice.setValue("Replacement");
                        cellController.deleteReplacementBtn.setVisible(true);
                        // Delete replacement employee functionality
                        Parent finalEmployeeCell = employeeCell;
                        cellController.deleteReplacementBtn.setOnAction(event1 -> deleteReplacementEmployee((AnchorPane) finalEmployeeCell));

                        // Add the employee cell to the ListView
                        employeeList.getItems().add((AnchorPane) employeeCell);
                    }
                }
            }
        });

        anchorPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (!event.getTarget().equals(employeeSearchField) && !event.getTarget().equals(employeeSuggestionList)) {
                employeeSuggestionList.setVisible(false);
            }
        });
    }

    public void addReplacementEmployee() {
        addReplacementBtn.setVisible(false);
        msgEmployeeLbl.setVisible(true);
        employeeSearchField.setVisible(true);
    }

    public void deleteReplacementEmployee(AnchorPane employeeCell) {
        System.out.println("Replacement employee deleted");
        String siteName = DummyController.getSiteName();

        LocalDate date = LocalDate.now(); // Get today's date

// Create a formatter for the desired pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");

// Convert the LocalDate to a String
        String DayEmp = date.format(formatter);
        System.out.println(date);


        Label employeeNameLabel = (Label) employeeCell.lookup("#employeeName");
        if (employeeNameLabel != null) {
            System.out.println("employee: " + employeeNameLabel.getText());

            // Iterate over the employeeList
            for (Document doc : MarkAttendanceEmployeeCellController.employeeList) {
                // Check if the employeeNameLabel text is in the empDetails of the document
                if (doc.get("empDetails").toString().contains(employeeNameLabel.getText())) {
                    // Remove the document from the list
                    MarkAttendanceEmployeeCellController.employeeList.remove(doc);
                    break;
                }
            }

            Iterator<Document> iterator = MarkAttendanceEmployeeCellController.dayEmpList.iterator();
            while (iterator.hasNext()) {
                Document doc = iterator.next();

                if (doc.get("_id").equals(employeeNameLabel.getText()) && doc.get("Site").equals(siteName) && doc.containsKey(DayEmp)) {
                    // Remove the document from the list
                    doc.remove(DayEmp);
                    break;
                }

            }
        }



        System.out.println(MarkAttendanceEmployeeCellController.employeeList);

        employeeList.getItems().remove(employeeCell);
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
