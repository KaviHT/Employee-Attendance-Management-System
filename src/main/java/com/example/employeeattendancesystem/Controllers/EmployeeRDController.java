package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.Database;
import com.example.employeeattendancesystem.Utils.MongoDBConnection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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

public class EmployeeRDController {

    public Button backBtn, searchBtn, createBtn, deleteBtn, editBtn;
    public Label employeeNumberLbl, firstNameLbl, lastNameLbl, genderLbl, dobLbl, contactNumberLbl, addressLbl;
    public TextField employeeSearchField;
    public ListView<String> employeeSuggestionList;
    private final ObservableList<String> suggestions = FXCollections.observableArrayList();
    MongoDatabase database = MongoDBConnection.getDatabase("attendence_db");
    MongoCollection<Document> EmployeeDataCollection = database.getCollection("employee");

    public void initialize() {
        Database database = new Database();

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

                // Pass the selected employee to the EmployeeCU class
                EmployeeCUController cuController = new EmployeeCUController();
                cuController.receiveSelectedItem(selectedItem);
            }
        });
    }

    public void switchToDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Dashboard.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Shadow - Employee Management System");
        stage.setScene(scene);
        stage.show();
    }

    public void searchEmployee() {

        String empNumber = employeeSearchField.getText().split(" ")[0];

        FindIterable<Document> documents = EmployeeDataCollection.find(eq("emp_id", empNumber));
        for (Document document : documents) {
            String empId = document.getString("emp_id");
            String firstName = document.getString("first_name");
            String lastName = document.getString("last_name");
            String empAddress = document.getString("address");
            String gender = document.getString("gender");
            String dateOfBirth = document.getString("dateOfBirth");
            String contactNumber = document.getString("contactNumber");

            employeeNumberLbl.setText(empId);
            firstNameLbl.setText(firstName);
            lastNameLbl.setText(lastName);
            genderLbl.setText(gender);
            dobLbl.setText(dateOfBirth);
            contactNumberLbl.setText(contactNumber);
            addressLbl.setText(empAddress);
        }
    }

    public void createEmployee(ActionEvent event) throws IOException {
        openEmployeeCUWindow(event, "Create Employee");
    }

    public void editEmployee(ActionEvent event) throws IOException {
        openEmployeeCUWindow(event, "Edit Employee");
    }

    public void deleteEmployee() {
        // Creating a confirmation alert to get the confirmation form the user
        Alert deleteConfirm = new Alert(Alert.AlertType.CONFIRMATION);
        deleteConfirm.setTitle("Confirmation");
        deleteConfirm.setHeaderText("Delete Employee");
        deleteConfirm.setContentText("Delete " +
                firstNameLbl.getText() + " " + lastNameLbl.getText() +
                " from employees?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        deleteConfirm.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = deleteConfirm.showAndWait();

        // Handle the user's response
        if (result.isPresent() && result.get() == yesButton) {  // User clicked OK

            String empNumber = employeeSearchField.getText().split(" ")[0];

            // Delete the document from the collection
            EmployeeDataCollection.deleteOne(eq("emp_id", empNumber));

            // Clear the employee details from the UI
            employeeNumberLbl.setText("");
            firstNameLbl.setText("");
            lastNameLbl.setText("");
            genderLbl.setText("");
            dobLbl.setText("");
            contactNumberLbl.setText("");
            addressLbl.setText("");
            employeeSearchField.clear();

        } else {
            // User clicked Cancel or closed the alert
        }
    }

    private void openEmployeeCUWindow(ActionEvent event, String windowTitle) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/EmployeeCU.fxml"));
        Parent root = loader.load();

        EmployeeCUController employeeCUController = loader.getController();
        employeeCUController.setStageTitle(windowTitle);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle(windowTitle);
        stage.setScene(scene);
        stage.show();
    }
}
