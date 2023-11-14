package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.Database;
import com.example.employeeattendancesystem.Utils.Employee;
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

public class EmployeeRDController {

    public Button backBtn, searchBtn, createBtn, deleteBtn, editBtn;
    public Label employeeNumberLbl, firstNameLbl, lastNameLbl, genderLbl, dobLbl, contactNumberLbl, addressLbl;
    public TextField employeeSearchField;
    public ListView<String> employeeSuggestionList;
    private final ObservableList<String> suggestions = FXCollections.observableArrayList();


    public void initialize() {
        // Sample data to work with - REMOVE in final
        Employee employee001 = new Employee(
                "001",
                "Kavindu",
                "Thotagamuwa",
                "500/3, Galle Road, Panadura",
                "Male",
                "2022-01-01",
                "0715141482");
        Employee employee002 = new Employee(
                "002",
                "Brian",
                "Henricus",
                "500, Kaduwela, Malabe",
                "Male",
                "2022-02-01",
                "0712345678");

        Database database = new Database();

        database.addEmployee(employee001);
        database.addEmployee(employee002);

        // Populating suggestions data from the database
        suggestions.addAll(database.getEmployeeDetails());  // <--- add the database here

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

    }

    public void createEmployee(ActionEvent event) throws IOException {
        openEmployeeCUWindow(event, "Create Employee");
    }

    public void editEmployee(ActionEvent event) throws IOException {
        openEmployeeCUWindow(event, "Edit Employee");
    }

    public void deleteEmployee() {

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
