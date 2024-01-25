package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.MongoDBConnection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javafx.application.Platform;
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
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;

public class SummaryDaySiteCellController {
    public ListView<AnchorPane> siteEmployeeList;
    public Label siteNameLbl, siteNumberLbl;
    public Button editBtn;

    MongoDatabase database = MongoDBConnection.getDatabase("attendence_db");
    MongoCollection<Document> AttendanceDataCollection = database.getCollection("attendence");

    LocalDate date = DummyController.getSelectedDate();
    String dateString = date.toString();


    public void initialize() throws IOException {
        String siteName = DummyController.getSiteName();
        String dateSiteName=dateString+"_"+siteName;

        System.out.println(dateString);

        // Create a list of items to populate the ListView
        var items = FXCollections.<AnchorPane>observableArrayList();

        // Find the document for the site
        Document siteDoc = AttendanceDataCollection.find(eq("_id", siteName)).first();

        // Get the attendance records for the date
        ArrayList<Document> attendanceData = (ArrayList<Document>) siteDoc.get(dateSiteName);

        if (attendanceData == null) {
            System.out.println("No attendance data found for date: " + dateString);
            // Create a dummy record with "No record found" for all fields
            Document dummyRecord = new Document();
            dummyRecord.put("empDetails", "No record found");
            dummyRecord.put("Status", "No record found");
            dummyRecord.put("in_time", "No record found");
            dummyRecord.put("out_time", "No record found");
            dummyRecord.put("Notes", "No record found");
            attendanceData = new ArrayList<>();
            attendanceData.add(dummyRecord);
        }

        System.out.println(attendanceData);

        // Iterate over the attendance records
        for(Document record : attendanceData) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Cells/SummaryDayEmployeeCell.fxml"));
            Parent cell = loader.load();

            SummaryDayEmployeeCellController employeeCellController = loader.getController();

            // Set the relevant labels
            employeeCellController.employeeNameLbl.setText(record.getString("empDetails")); // Set the employee name
            employeeCellController.employeeStatusLbl.setText(record.getString("Status"));
            employeeCellController.employeeOnTimeLbl.setText(record.getString("in_time"));
            employeeCellController.employeeOutTimeLbl.setText(record.getString("out_time"));
            employeeCellController.noteLbl.setText(record.getString("Notes"));

            Tooltip tooltip = new Tooltip("Example ");
            Tooltip.install(employeeCellController.noteLbl, tooltip);

            items.add((AnchorPane) cell);
        }

        siteEmployeeList.setItems(items);

        // Calculate and set the preferred height of the ListView
        Platform.runLater(() -> {
            siteEmployeeList.setPrefHeight(200 + 3);
        });
    }

    public void goToEditAttendance (ActionEvent event) throws IOException {

            DummyController.setSiteName(siteNameLbl.getText());
            DummyController.setEditStatus(true);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/ViewFactory/MarkAttendanceSiteView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Edit Attendance");
            stage.setScene(scene);
            stage.show();
        }
    }

