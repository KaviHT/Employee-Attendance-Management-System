package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.MongoDBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.bson.Document;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MarkAttendanceEmployeeCellController {
    public ChoiceBox<String> employeeStatusChoice;
    public TextField onTimeField, outTimeField, noteField;
    public Label employeeNumber, employeeName;
    private final String[] status = {"Present", "Leave", "Half Day"};

    MongoDBConnection mongoDBConnection = new MongoDBConnection();
    MongoDatabase Database = mongoDBConnection.getDatabase("attendence_db");
    MongoCollection<Document> AtteEmpCollection = Database.getCollection("attendence");

    public void initialize() {
        employeeStatusChoice.getItems().addAll(status);
        employeeStatusChoice.setOnAction(this::getStatus);
    }

    public void getStatus(ActionEvent event) {
        String employeeStatus = employeeStatusChoice.getValue();
        System.out.println(employeeName.getText() + " " + employeeStatus);

    }

    public void saveAttendance(ActionEvent event) {}




    }







