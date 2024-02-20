package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.MongoDBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.bson.Document;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;


public class MarkAttendanceEmployeeCellController {
    public ChoiceBox<String> employeeStatusChoice;
    public TextField onTimeField, outTimeField, noteField;
    public Label employeeNumber, employeeName;
    private final String[] status = {"Present", "Leave", "Half Day"};
    public static List <Document> employeeList = new ArrayList<>();
    public Button deleteReplacementBtn;
    private String DayEmp;
    public static List <Document> dayEmpList = new ArrayList<>();

    MongoDBConnection mongoDBConnection = new MongoDBConnection();
    MongoDatabase Database = mongoDBConnection.getDatabase("attendence_db");
    MongoCollection<Document> AtteEmpCollection = Database.getCollection("attendence");
    MongoCollection<Document> DayAtteEmpCollection = Database.getCollection("EmployeeAttendance");


    public void initialize() {
        employeeStatusChoice.getItems().addAll(status);
        employeeStatusChoice.setOnAction(this::getStatus);
        onTimeField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Focus lost
                updateListWithTextFieldValues();
            }
        });

        outTimeField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Focus lost
                updateListWithTextFieldValues();
            }
        });

        noteField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Focus lost
                updateListWithTextFieldValues();
            }
        });
    }

    public void getStatus(ActionEvent event) {
        String employeeStatus = employeeStatusChoice.getValue();
        System.out.println(employeeName.getText() + " " + employeeStatus);
        addToArray(employeeName.getText(), employeeStatus, onTimeField.getText(), outTimeField.getText(), noteField.getText());
    }

    private void updateListWithTextFieldValues() {
        // Get the current values from the TextFields
        String inTime = onTimeField.getText();
        String outTime = outTimeField.getText();
        String note = noteField.getText();

        // Call addToArray to update the employeeList with the new values
        addToArray(employeeName.getText(), employeeStatusChoice.getValue(), inTime, outTime, note);
    }

    public void addToArray(String employeeName, String employeeStatus, String inTime, String outTime, String note) {
        String siteName = DummyController.getSiteName();

        if(inTime.equals("")){
            inTime="N/A";
        }
        if(outTime.equals("")){
            outTime="N/A";
        }
        if(note.equals("")){
            note="N/A";
        }

        DayEmp = employeeStatus + "_" + inTime + "_" + outTime + "_" + note;

        Document employee = new Document("empDetails", employeeName)
                .append("Status", employeeStatus)
                .append("in_time", inTime)
                .append("out_time", outTime)
                .append("Notes", note);

        int index = -1;
        for (int i = 0; i < employeeList.size(); i++) {
            if (employeeList.get(i).get("empDetails").equals(employeeName)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            employeeList.set(index, employee);
            System.out.println("Employee details have been updated.");
        } else {
            employeeList.add(employee);
            System.out.println("New employee details have been added.");
        }
        System.out.println(employeeList);

        saveEmployeeAttendance(employeeName,siteName,DayEmp);
    }

    public void saveEmployeeAttendance(String employeeName, String site, String dateEmp) {
        LocalDate currentDate = LocalDate.now();
        Document employee = new Document("_id", employeeName)
                .append("Site", site)
                .append(currentDate.toString(), dateEmp);
        dayEmpList.add(employee);
        System.out.println(dateEmp);
    }

    public void saveFunction() {
            LocalDate currentDate = LocalDate.now();
            String siteName = DummyController.getSiteName();
            String dateSiteAttendance = currentDate+"_"+siteName;

        for (Document employee : dayEmpList) {
            String employeeName = employee.getString("_id");
            Document employeeAttendance = DayAtteEmpCollection.find(eq("_id", employeeName)).first();

            if (employeeAttendance == null) {
                DayAtteEmpCollection.insertOne(employee);
            } else {
                DayAtteEmpCollection.findOneAndUpdate(
                        eq("_id", employeeName),
                        new Document("$set", employee),
                        new FindOneAndUpdateOptions().upsert(true));
            }
        }

        System.out.println(dayEmpList);
        dayEmpList.clear();

            Document siteAttendance = AtteEmpCollection.find(eq("_id", siteName)).first();

            if (siteAttendance == null) {
                siteAttendance = new Document("_id", siteName)
                        .append(dateSiteAttendance.toString(), new ArrayList<>());
            }

            AtteEmpCollection.findOneAndUpdate(
                eq("_id", siteName),
                new Document("$set", new Document(dateSiteAttendance.toString(), employeeList)),
                new FindOneAndUpdateOptions().upsert(true));

            // Clear the list for the next batch of records
            employeeList.clear();
    }
}












