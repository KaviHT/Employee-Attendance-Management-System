package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.MongoDBConnection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.mongodb.client.model.Filters.eq;

public class EmployeeCUController {

    public Button backBtn, saveEmployeeBtn;
    public TextField employeeNumberField, firstNameField, lastNameField, addressField, contactNumberField, nicField;
    public DatePicker dobField;
    public ToggleGroup gender;
    public RadioButton maleRBtn, femaleRBtn, otherRBtn;
    public Label titleLbl, dobErrorLbl, genderErrorLbl;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private static String theItem;


    public void setStageTitle(String windowTitle) {
        if (windowTitle.equals("Create Employee")) {
            titleLbl.setText("Create a New Employee");
        } else if (windowTitle.equals("Edit Employee")) {
            titleLbl.setText("Edit Employee");

            String empNumber = theItem.split(" ")[0];

            MongoDBConnection mongoDBConnection = new MongoDBConnection();
            MongoDatabase Database = mongoDBConnection.getDatabase("attendence_db");
            MongoCollection<Document> employeeCollection = Database.getCollection("employee");

            FindIterable<Document> documents = employeeCollection.find(eq("emp_id", empNumber));

            for (Document document : documents) {
                String empId = document.getString("emp_id");
                String firstName = document.getString("first_name");
                String lastName = document.getString("last_name");
                String empAddress = document.getString("address");
                String gender = document.getString("gender");
                String dateOfBirth = document.getString("dateOfBirth");
                String contactNumber = document.getString("contactNumber");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(dateOfBirth, formatter);

                employeeNumberField.setText(empId);
                firstNameField.setText(firstName);
                lastNameField.setText(lastName);
                setSelectedGender(gender);
                dobField.setValue(date);
                contactNumberField.setText(contactNumber);
                addressField.setText(empAddress);
            }
        }
    }

    // Get the selectedItem from the EmployeeRD class
    public void receiveSelectedItem(String selectedItem) {
        System.out.println("Received item in AnotherClass: " + selectedItem);
        theItem = selectedItem;
    }

    public void saveEmployee(ActionEvent event) {

        // Input validation
        if (dobField.getValue() == null) {
            dobErrorLbl.setVisible(true);
            genderErrorLbl.setVisible(false);
            return;
        } else if (gender.getSelectedToggle() == null) {
            genderErrorLbl.setVisible(true);
            dobErrorLbl.setVisible(false);
            return;
        } else {
            dobErrorLbl.setVisible(false);
            genderErrorLbl.setVisible(false);
        }

        MongoDBConnection mongoDBConnection = new MongoDBConnection();
        MongoDatabase Database = mongoDBConnection.getDatabase("attendence_db");
        MongoCollection<Document> employeeCollection = Database.getCollection("employee");

        // Implementation of creating a new Employee
        if (titleLbl.getText().equals("Create a New Employee")) {

            Document doc = new Document("emp_id", employeeNumberField.getText().replace(" ",""))
                    .append("first_name", firstNameField.getText())
                    .append("last_name", lastNameField.getText())
                    .append("address", addressField.getText())
                    .append("gender", getSelectedGender())
                    .append("dateOfBirth", dobField.getValue().toString())
                    .append("contactNumber", contactNumberField.getText());
            // add newEmployee to the database
            employeeCollection.insertOne(doc);

            // Information alert for the user to indicate the creation of a new employee
            Alert empCreated = new Alert(Alert.AlertType.INFORMATION);
            empCreated.setTitle("New Employee Saved");
            empCreated.setHeaderText(null); // No header text
            empCreated.setContentText("The employee successfully created!");

            empCreated.showAndWait();
            clearInputFields();

        }
        // Implementation of editing an existing Employee
        else if (titleLbl.getText().equals("Edit Employee")) {

            Document existingEmployee = employeeCollection.find(Filters.eq("emp_id", employeeNumberField.getText())).first();
            // update the employee details
            existingEmployee.put("first_name", firstNameField.getText());
            existingEmployee.put("last_name", lastNameField.getText());
            existingEmployee.put("address", addressField.getText());
            existingEmployee.put("gender", getSelectedGender());
            existingEmployee.put("dateOfBirth", dobField.getValue().toString());
            existingEmployee.put("contactNumber", contactNumberField.getText());
            // update the employee in the database
            employeeCollection.replaceOne(Filters.eq("emp_id", employeeNumberField.getText()), existingEmployee);

            // Information alert for the user to indicate the edit of an employee
            Alert empEdited = new Alert(Alert.AlertType.INFORMATION);
            empEdited.setTitle("Employee Edit Saved");
            empEdited.setHeaderText(null); // No header text
            empEdited.setContentText("The employee successfully edited!");

            empEdited.showAndWait();
            clearInputFields();
        }
    }

    private String getSelectedGender() {
        RadioButton selectedRadioButton = (RadioButton) gender.getSelectedToggle();
        return selectedRadioButton.getText();
    }

    public void setSelectedGender(String genderText) {
        if (genderText.equals(maleRBtn.getText())) {
            maleRBtn.setSelected(true);
        } else if (genderText.equals(femaleRBtn.getText())) {
            femaleRBtn.setSelected(true);
        } else if (genderText.equals(otherRBtn.getText())) {
            otherRBtn.setSelected(true);
        }
    }

    public void switchToEmployeeRD(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/Fxml/EmployeeRD.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Employees");
        stage.setScene(scene);
        stage.show();
    }

    public void clearInputFields() {
        employeeNumberField.clear();
        firstNameField.clear();
        lastNameField.clear();
        addressField.clear();
        dobField.setValue(null);
        gender.selectToggle(null);
        contactNumberField.clear();
    }
}
