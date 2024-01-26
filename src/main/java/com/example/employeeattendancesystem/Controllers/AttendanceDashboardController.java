package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.MongoDBConnection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class AttendanceDashboardController {
    public Label presentCountLbl, leaveCountLbl, halfDayCountLbl;
    public TextField siteSearchField;
    public ListView<AnchorPane> allSitesListView;

    MongoDatabase database = MongoDBConnection.getDatabase("attendence_db");
    MongoCollection<Document> AttendanceDataCollection = database.getCollection("attendence");

    MongoCollection<Document> AttendanceSiteCollection = database.getCollection("site");




    public void initialize() throws IOException {
        int totalPresentCount = 0;
        int totalLeaveCount = 0;
        int totalHalfDayCount = 0;

        FindIterable<Document> siteDetails = AttendanceSiteCollection.find();

        // Create a list of items to populate the ListView
        var items = FXCollections.<AnchorPane>observableArrayList();

        int i = 0;
        for (Document site : siteDetails) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Cells/AttendanceDashboardSiteCell.fxml"));
            Parent cell = loader.load();

            AttendanceDashboardSiteCellController siteCellController = loader.getController();

            siteCellController.siteNumberLbl.setText(String.valueOf(i+1));
            String siteName = site.getString("site_details");

            siteCellController.siteNameLbl.setText(siteName); // assuming site_details is a string

            // Get today's date
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            // Get the attendance data for the site
            FindIterable<Document> attendanceData = AttendanceDataCollection.find();

            int presentCount = 0;
            int leaveCount = 0;
            int halfDayCount = 0;

            for (Document doc : attendanceData) {
                List<Document> todayAttendance = (List<Document>) doc.get(today + "_" + siteName);
                if (todayAttendance != null) {
                    for (Document record : todayAttendance) {
                        String status = record.getString("Status");
                        if ("Present".equals(status)) {
                            presentCount++;
                        } else if ("Leave".equals(status)) {
                            leaveCount++;
                        } else if ("Half Day".equals(status)) {
                            halfDayCount++;
                        }
                    }
                }
            }
            totalPresentCount += presentCount;
            totalLeaveCount += leaveCount;
            totalHalfDayCount += halfDayCount;

            siteCellController.sitePresentCountLbl.setText(String.valueOf(presentCount));
            siteCellController.siteLeaveCountLbl.setText(String.valueOf(leaveCount));
            siteCellController.siteHalfDayCountLbl.setText(String.valueOf(halfDayCount));



            items.add((AnchorPane) cell);
            i++;
        }
        presentCountLbl.setText(String.valueOf(totalPresentCount));
        leaveCountLbl.setText(String.valueOf(totalLeaveCount));
        halfDayCountLbl.setText(String.valueOf(totalHalfDayCount));

        // Set the items in the ListView
        allSitesListView.setItems(items);
        // Add a listener to the searchField to handle auto-scrolling
        siteSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                // Iterate through the items to find a match
                for (int j = 0; j < items.size(); j++) {
                    String labelText =((Label) items.get(j).lookup("#siteNumberLbl")).getText().toLowerCase();
                    String searchText = newValue.toLowerCase();
                    if (labelText.contains(newValue)) {
                        // Scroll to the matched item
                        allSitesListView.scrollTo(j);
                        break; // Stop searching after the first match
                    }
                }
            }
        });
    }
}
