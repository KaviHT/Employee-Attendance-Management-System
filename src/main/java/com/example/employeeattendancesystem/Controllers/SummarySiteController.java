package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.MongoDBConnection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import org.bson.Document;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

public class SummarySiteController {
    public Label siteNameLbl, yearLbl, monthLbl, noRecordsMsgLbl;
    public ListView<AnchorPane> employeeList;
    public Button previousMonthBtn, nextMonthBtn, thisMonthBtn;
    LocalDate dateFocus;
    MongoDatabase database = MongoDBConnection.getDatabase("attendence_db");
    MongoCollection<Document> DaySiteSummaryDataCollection = database.getCollection("EmployeeAttendance");


    public void initialize() throws IOException {
        // Getting site name form DummyController
        String siteName = DummyController.getSiteName();
        siteNameLbl.setText(siteName);

        // Setting calendar properties
        dateFocus = LocalDate.now();
        checkRecords();
    }

    public void previousMonth() throws IOException {
        dateFocus = dateFocus.minusMonths(1);
        checkRecords();
    }

    public void nextMonth() throws IOException {
        dateFocus = dateFocus.plusMonths(1);
        checkRecords();
    }

    public void currentMonth() throws IOException {
        dateFocus = LocalDate.now();
        checkRecords();
    }

    public void checkRecords() throws IOException {
        if (LocalDate.now().isBefore(dateFocus)) {
            noRecordsMsgLbl.setVisible(true);
            employeeList.getItems().clear();
            yearLbl.setText(String.valueOf(dateFocus.getYear()));
            monthLbl.setText(String.valueOf(dateFocus.getMonth()));
        } else {
            noRecordsMsgLbl.setVisible(false);
            drawSummaryTable();
        }
    }

    private void drawSummaryTable() throws IOException {

        String siteName = DummyController.getSiteName();

        yearLbl.setText(String.valueOf(dateFocus.getYear()));
        monthLbl.setText(String.valueOf(dateFocus.getMonth()));

        var items = FXCollections.<AnchorPane>observableArrayList();
        int monthMaxDate = dateFocus.getMonth().maxLength();
        if (dateFocus.getYear() % 4 != 0 && monthMaxDate == 29) {
            monthMaxDate = 28;
        }

        // Get the current year and month in the format "yyyy-MM"
        String currentYearMonth = String.format("%04d-%02d", dateFocus.getYear(), dateFocus.getMonthValue());

        // Fetch all documents with the given siteName
        FindIterable<Document> docs = DaySiteSummaryDataCollection.find(Filters.eq("Site", siteName));

        int employeeCount = 0;
        for (Document doc : docs) {
            // Get the field names (dates) in the document
            Set<String> keys = doc.keySet();

            // Check if the document has any fields (dates) that start with the current year and month
            boolean hasCurrentMonthRecord = keys.stream().anyMatch(key -> key.startsWith(currentYearMonth));

            // If the document has no fields (dates) for the current month, skip this document
            if (!hasCurrentMonthRecord) {
                continue;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Cells/SummarySiteEmployeeCell.fxml"));
            Parent cell = loader.load();
            SummarySiteEmployeeCellController employeeCellController = loader.getController();

            // Set employee name and number
            employeeCellController.employeeNameLbl.setText(doc.getString("_id"));
            employeeCellController.employeeNumberLbl.setText(String.valueOf(++employeeCount));

            double slotWidth = employeeCellController.statusSummaryHbox.getPrefWidth() / monthMaxDate;
            for (int j = 0; j < monthMaxDate; j++) {
                Label dayStatusLbl = new Label();
                dayStatusLbl.setPrefWidth(slotWidth);

                // Fill the label with the date
                dayStatusLbl.setText(String.valueOf(j + 1));

                // Get the attendance status for the day
                String dateKey = currentYearMonth + "-" + String.format("%02d", j + 1);
                String attendanceData = doc.getString(dateKey);
                if (attendanceData != null) {
                    String[] parts = attendanceData.split("_");
                    String attendanceStatus = parts[0];// Attendance status is the first element
                    String InTime = parts[1];
                    String OutTime = parts[2];
                    String Notes = parts[3];

                    Tooltip tooltip = new Tooltip("InTime: " + InTime + "\nOutTime: " + OutTime + "\nNotes: " + Notes);
                    Tooltip.install(dayStatusLbl, tooltip);

                    // If the employee is present, mark the date in green
                    if ("Present".equals(attendanceStatus)) {
                        dayStatusLbl.setTextFill(Color.GREEN);
                    }
                    // If the employee is absent, mark the date in red
                    else if ("Leave".equals(attendanceStatus)) {
                        dayStatusLbl.setTextFill(Color.RED);
                    }
                    // If the employee is on half day, mark the date in yellow
                    else if ("Half Day".equals(attendanceStatus)) {
                        dayStatusLbl.setTextFill(Color.ORANGE);
                    } else if ("Replacement".equals(attendanceStatus)) {
                        dayStatusLbl.setTextFill(Color.BLUE);
                    }
                }

                employeeCellController.statusSummaryHbox.getChildren().add(dayStatusLbl);
            }

            items.add((AnchorPane) cell);
        }
        employeeList.setItems(items);
    }
}

