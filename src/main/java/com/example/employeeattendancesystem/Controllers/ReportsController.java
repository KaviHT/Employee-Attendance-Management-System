package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.Database;
import com.example.employeeattendancesystem.Utils.MongoDBConnection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.FileOutputStream;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;


public class ReportsController {
    public AnchorPane anchorPane;
    public DatePicker atrStartDate, atrEndDate, sjrStartDate, sjrEndDate, erStartDate, erEndDate, srStartDate, srEndDate;
    public Button atrConvertBtn, sjrConvertBtn, erConvertBtn, srConvertBtn;
    public ListView<String> atrSiteSuggestionList, erEmployeeSuggestionList, srSiteSuggestionList;
    public TextField atrSiteSearchField, erEmployeeSearchField, srSiteSearchField;
    private final ObservableList<String> atrSiteSuggestions = FXCollections.observableArrayList();
    private final ObservableList<String> srSiteSuggestions = FXCollections.observableArrayList();
    private final ObservableList<String> erEmployeeSuggestions = FXCollections.observableArrayList();
    private String atrSelectedItem;
    private String erSelectedItem;
    private String srSelectedItem;

    MongoDBConnection mongoDBConnection = new MongoDBConnection();
    MongoDatabase Database = mongoDBConnection.getDatabase("attendence_db");
    MongoCollection<Document> PrintEmpCollection = Database.getCollection("EmployeeAttendance");
    MongoCollection<Document> SpJobPrintCollection = Database.getCollection("special_jobs");


    public void initialize() {

        Database database = new Database();

        // Add the "All" to the site options
        atrSiteSuggestions.add("All");

        // Populating suggestions data from the database
        atrSiteSuggestions.addAll(database.getSiteSearchDetails());
        srSiteSuggestions.addAll(database.getSiteSearchDetails());
        erEmployeeSuggestions.addAll(database.getEmployeeSearchDetails());

        // Set the list to the ListView and select "All" by default
        // siteSuggestionList.getSelectionModel().select("All");
        atrSelectedItem = "All";

        // Update the search field to show the default selected item
        atrSiteSearchField.setText(atrSelectedItem);

        // Autocomplete functionality
        atrSiteSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Clear previous suggestions
            atrSiteSuggestionList.getItems().clear();

            // Filter and add matching suggestions
            String searchText = newValue.toLowerCase().trim();
            for (String item : atrSiteSuggestions) {
                if (item.toLowerCase().contains(searchText)) {
                    atrSiteSuggestionList.getItems().add(item);
                }
            }

            // Show or hide the suggestion list based on whether there are suggestions
            atrSiteSuggestionList.setVisible(!atrSiteSuggestionList.getItems().isEmpty());
        });

        // Handle item selection from the suggestion list
        atrSiteSuggestionList.setOnMouseClicked(event -> {
            atrSelectedItem = atrSiteSuggestionList.getSelectionModel().getSelectedItem();
            if (atrSelectedItem != null) {
                atrSiteSearchField.setText(atrSelectedItem);
                atrSiteSuggestionList.setVisible(false);
                SitesCUController siteCU = new SitesCUController();
                siteCU.receiveSelectedItem(atrSelectedItem);
            }
        });

        // Autocomplete functionality
        erEmployeeSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Clear previous suggestions
            erEmployeeSuggestionList.getItems().clear();

            // Filter and add matching suggestions
            String searchText = newValue.toLowerCase().trim();
            for (String item : erEmployeeSuggestions) {
                if (item.toLowerCase().contains(searchText)) {
                    erEmployeeSuggestionList.getItems().add(item);
                }
            }

            // Show or hide the suggestion list based on whether there are suggestions
            erEmployeeSuggestionList.setVisible(!erEmployeeSuggestionList.getItems().isEmpty());
        });

        // Handle item selection from the suggestion list
        erEmployeeSuggestionList.setOnMouseClicked(event -> {
            erSelectedItem = erEmployeeSuggestionList.getSelectionModel().getSelectedItem();
            if (erSelectedItem != null) {
                erEmployeeSearchField.setText(erSelectedItem);
                erEmployeeSuggestionList.setVisible(false);
                SitesCUController siteCU = new SitesCUController();
                siteCU.receiveSelectedItem(erSelectedItem);
            }
        });


        // Autocomplete functionality
        srSiteSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Clear previous suggestions
            srSiteSuggestionList.getItems().clear();

            // Filter and add matching suggestions
            String searchText = newValue.toLowerCase().trim();
            for (String item : srSiteSuggestions) {
                if (item.toLowerCase().contains(searchText)) {
                    srSiteSuggestionList.getItems().add(item);
                }
            }

            // Show or hide the suggestion list based on whether there are suggestions
            srSiteSuggestionList.setVisible(!srSiteSuggestionList.getItems().isEmpty());
        });

        // Handle item selection from the suggestion list
        srSiteSuggestionList.setOnMouseClicked(event -> {
            srSelectedItem = srSiteSuggestionList.getSelectionModel().getSelectedItem();
            if (srSelectedItem != null) {
                srSiteSearchField.setText(srSelectedItem);
                srSiteSuggestionList.setVisible(false);
                SitesCUController siteCU = new SitesCUController();
                siteCU.receiveSelectedItem(srSelectedItem);
            }
        });

        anchorPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (!event.getTarget().equals(atrSiteSearchField) && !event.getTarget().equals(atrSiteSuggestionList)) {
                atrSiteSuggestionList.setVisible(false);
            }
            if (!event.getTarget().equals(erEmployeeSearchField) && !event.getTarget().equals(erEmployeeSuggestionList)) {
                erEmployeeSuggestionList.setVisible(false);
            }
            if (!event.getTarget().equals(srSiteSearchField) && !event.getTarget().equals(srSiteSuggestionList)) {
                srSiteSuggestionList.setVisible(false);
            }
        });
    }

    public void atrConvertCSV(ActionEvent event) {
        if (dateInputValidation(atrStartDate, atrEndDate)) {
            String startDate = atrStartDate.getValue().toString();
            String endDate = atrEndDate.getValue().toString();

            FindIterable<Document> documents;

            // If selectedItem is not "All", filter the documents where site equals to selectedItem
            if (!atrSelectedItem.equals("All")) {
                documents = PrintEmpCollection.find(Filters.eq("Site", atrSelectedItem));
            } else {
                documents = PrintEmpCollection.find();
            }

            // Sort the documents by site
            documents.sort(Sorts.ascending("Site"));

            Map<String, List<Map<String, Object>>> groupedRecords = new HashMap<>();

            for (Document document : documents) {
                String employeeDetails = document.getString("_id");
                String site = document.getString("Site");

                // Iterate over all the keys in the document
                for (String key : document.keySet()) {
                    // Check if the key is a date and is between startDate and endDate
                    if (key.compareTo(startDate) >= 0 && key.compareTo(endDate) <= 0) {
                        String record = document.getString(key);

                        // Split the record by "_"
                        String[] parts = record.split("_");
                        String status = parts[0];
                        String inTime = parts[1];
                        String outTime = parts[2];
                        String notes = parts[3];

                        Map<String, Object> recordObject = new HashMap<>();
                        recordObject.put("EmployeeDetails", employeeDetails);
                        recordObject.put("Site", site);
                        recordObject.put("Date", key);
                        recordObject.put("Status", status);
                        recordObject.put("InTime", inTime);
                        recordObject.put("OutTime", outTime);
                        recordObject.put("Notes", notes);

                        // Add the record to the list of records for this site
                        if (!groupedRecords.containsKey(site)) {
                            groupedRecords.put(site, new ArrayList<>());
                        }
                        groupedRecords.get(site).add(recordObject);
                    }
                }
            }

            // Create a new workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Records");

            // Create the header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Site", "EmployeeDetails", "Date", "Status", "InTime", "OutTime", "Notes"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Fill the sheet with data from records
            int rowIndex = 1;
            for (Map.Entry<String, List<Map<String, Object>>> entry : groupedRecords.entrySet()) {
                int startRow = rowIndex;
                for (Map<String, Object> record : entry.getValue()) {
                    Row row = sheet.createRow(rowIndex++);  // Increment rowIndex for each new row

                    for (int j = 0; j < headers.length; j++) {
                        org.apache.poi.ss.usermodel.Cell cell = row.createCell(j);
                        cell.setCellValue(record.get(headers[j]).toString());
                    }
                }
                // Merge cells in the "Site" column for this group of records
                sheet.addMergedRegion(new CellRangeAddress(startRow, rowIndex - 1, 0, 0));
            }
            // Save the workbook to a file
            filePathSelection(startDate, endDate, "Attendance Report", "Attendance_Report_" + atrSelectedItem.replace(" ", "-") + "_", workbook);
        }
    }

    public void sjrConvertCSV(ActionEvent event) {
        if (dateInputValidation(sjrStartDate, sjrEndDate)) {
            String startDate = sjrStartDate.getValue().toString();
            String endDate = sjrEndDate.getValue().toString();

            // Fetch the documents from the database
            Bson filter = Filters.and(Filters.gte("date", startDate), Filters.lte("date", endDate));
            FindIterable<Document> documents = SpJobPrintCollection.find(filter);

            // Create a new workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Special Job Report");

            // Create the header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Date");
            headerRow.createCell(1).setCellValue("Site");
            headerRow.createCell(2).setCellValue("Employee Details");
            headerRow.createCell(3).setCellValue("In Time");
            headerRow.createCell(4).setCellValue("Out Time");
            headerRow.createCell(5).setCellValue("Notes");

            // Add the documents to the sheet
            int rowNum = 1;
            for (Document doc : documents) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(doc.getString("date"));
                row.createCell(1).setCellValue(doc.getString("siteName"));
                row.createCell(2).setCellValue(doc.getString("employeeName"));
                row.createCell(3).setCellValue(doc.getString("inTime"));
                row.createCell(4).setCellValue(doc.getString("outTime"));
                row.createCell(5).setCellValue(doc.getString("note"));
            }

            // Save the workbook to a file
            filePathSelection(startDate, endDate, "Special Job Report", "Special_Job_Report_", workbook);
        }
    }

    public void erConvertCSV() {
        if (dateInputValidation(erStartDate, erEndDate)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startDate = erStartDate.getValue().format(formatter);
            String endDate = erEndDate.getValue().format(formatter);
            String selectedEmployee = erEmployeeSearchField.getText();

            // Create a new workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Employee Report");

            // Connect to MongoDB and fetch the document for the selected employee
            MongoCollection<Document> collection = mongoDBConnection.getDatabase("attendence_db").getCollection("EmployeeAttendance");
            Document employeeDocument = collection.find(Filters.eq("_id", selectedEmployee)).first();

            // Extract the site name from the employee's document
            String siteName = employeeDocument.getString("Site");

            // Create the first row for the site name
            Row siteNameRow = sheet.createRow(0);
            Cell siteNameCell = siteNameRow.createCell(0);
            siteNameCell.setCellValue("Site Name: " + siteName);

            // Create the second row for the employee details
            Row employeeDetailsRow = sheet.createRow(1);
            Cell employeeDetailsCell = employeeDetailsRow.createCell(0);
            employeeDetailsCell.setCellValue("Employee Details: " + selectedEmployee);

            // Create the third row for the column headers
            Row headerRow = sheet.createRow(2);
            headerRow.createCell(0).setCellValue("Date");
            headerRow.createCell(1).setCellValue("Status");
            headerRow.createCell(2).setCellValue("In Time");
            headerRow.createCell(3).setCellValue("Out Time");
            headerRow.createCell(4).setCellValue("Notes");

            // Initialize counters for presents, leaves, and half days
            int totalPresents = 0;
            int totalLeaves = 0;
            int totalHalfDays = 0;

            // Initialize rowIndex for the first data row
            int rowIndex = 3; // Data rows start from the fourth row

            // Iterate over each date within the date range
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);
            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                String dateKey = date.format(formatter);
                String record = employeeDocument.getString(dateKey); // Get the record for the date

                if (record != null && !record.isEmpty()) {
                    String[] parts = record.split("_"); // Split the record into parts
                    String status = parts[0]; // Status is the first part of the record

                    // Update counters based on the status
                    if (status.equalsIgnoreCase("Present")) {
                        totalPresents++;
                    } else if (status.equalsIgnoreCase("Leave")) {
                        totalLeaves++;
                    } else if (status.equalsIgnoreCase("Half Day")) {
                        totalHalfDays++;
                    }

                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(dateKey); // Date
                    row.createCell(1).setCellValue(status); // Status
                    row.createCell(2).setCellValue(parts[1]); // In Time
                    row.createCell(3).setCellValue(parts[2]); // Out Time
                    row.createCell(4).setCellValue(parts[3]); // Notes
                }
            }

            // Add rows at the bottom for the total counts
            Row totalPresentsRow = sheet.createRow(rowIndex++);
            totalPresentsRow.createCell(0).setCellValue("Total Presents");
            totalPresentsRow.createCell(1).setCellValue(totalPresents);

            Row totalLeavesRow = sheet.createRow(rowIndex++);
            totalLeavesRow.createCell(0).setCellValue("Total Leaves");
            totalLeavesRow.createCell(1).setCellValue(totalLeaves);

            Row totalHalfDaysRow = sheet.createRow(rowIndex++);
            totalHalfDaysRow.createCell(0).setCellValue("Total Half Days");
            totalHalfDaysRow.createCell(1).setCellValue(totalHalfDays);

            // Autosize the columns based on the content
            for (int i = 0; i < 5; i++) { // There are 5 columns in total
                sheet.autoSizeColumn(i);
            }

            // Save the workbook to a file
            filePathSelection(startDate, endDate, "Employee Report", "Employee_Report_" + selectedEmployee.replace(" ", "_") + "_", workbook);
        }
    }

    public void srConvertCSV() {
        if (dateInputValidation(srStartDate, srEndDate)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startDate = srStartDate.getValue().format(formatter);
            String endDate = srEndDate.getValue().format(formatter);
            String selectedSite = srSiteSearchField.getText();

            // Create a new workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Site Report");

            // Calculate the number of columns needed for the dates
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);
            int totalDays = (int) ChronoUnit.DAYS.between(start, end) + 1; // Inclusive of end date

            // Create a row at the top for the site name and merge cells
            Row siteNameRow = sheet.createRow(0);
            Cell siteNameCell = siteNameRow.createCell(0);
            siteNameCell.setCellValue("Site Name: " + selectedSite);
            // Merge cells for the site name
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, totalDays + 1)); // Merge across all date columns and two additional columns for totals

            // Create the header row for the dates starting from the second row
            Row headerRow = sheet.createRow(1);
            headerRow.createCell(0).setCellValue("_id");

            // Fill in the headers with dates
            int dateColumnIndex = 1; // Column index for dates starts from 1
            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                headerRow.createCell(dateColumnIndex++).setCellValue(date.format(formatter));
            }

            // Headers for total presents and absents
            headerRow.createCell(dateColumnIndex++).setCellValue("Total Presents");
            headerRow.createCell(dateColumnIndex++).setCellValue("Total Leaves");

            // Connect to MongoDB and fetch documents
            MongoCollection<Document> collection = mongoDBConnection.getDatabase("attendence_db").getCollection("EmployeeAttendance");
            FindIterable<Document> documents = collection.find(Filters.eq("Site", selectedSite));

            // Initialize rowIndex for the first employee row
            int rowIndex = 2; // Employee rows start from the third row

            // Iterate over each document (employee)
            for (Document doc : documents) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(doc.getString("_id")); // Employee's ID and Name

                int cellIndex = 1; // Reset cell index for each employee row
                int totalPresents = 0; // Counter for presents
                int totalAbsents = 0; // Counter for absents

                // Iterate over each date within the date range
                for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                    String dateKey = date.format(formatter);
                    String record = doc.getString(dateKey); // Get the record for the date

                    // Extract the status if there is a record for that date
                    String status = "N/A";
                    if (record != null && !record.isEmpty()) {
                        status = record.split("_")[0]; // The status is the first part of the record
                        if (status.equalsIgnoreCase("Present")) {
                            totalPresents++;
                        } else if (status.equalsIgnoreCase("Leave")) {
                            totalAbsents++;
                        }
                    }
                    row.createCell(cellIndex++).setCellValue(status); // Set the status in the corresponding cell
                }

                // Write the total counts at the end of the row
                row.createCell(cellIndex++).setCellValue(totalPresents); // Total presents
                row.createCell(cellIndex++).setCellValue(totalAbsents); // Total absents
            }

            // Autosize all columns based on the content
            for (int i = 0; i < dateColumnIndex; i++) { // Include the '_id' column and the two total count columns
                sheet.autoSizeColumn(i);
            }

            // Save the workbook to a file
            filePathSelection(startDate, endDate, "Site Report", "Site_Report_" + selectedSite.replace(" ", "_") + "_", workbook);
        }
    }


    private void filePathSelection(String startDate, String endDate, String title, String fileName, Workbook workbook) {
        // Open the file path selection window
        FileChooser fileChooser = new FileChooser();

        // Set extension filter for Excel files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        // Set a default file name
        fileChooser.setInitialFileName(fileName + startDate + "_to_" + endDate + ".xlsx");

        // Show save file dialog
        Stage stage = (Stage) atrConvertBtn.getScene().getWindow();
        fileChooser.setTitle("Save " + title);
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileOutputStream out = new FileOutputStream(file)) {
                workbook.write(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Save path selected: " + file.getPath());
        }
    }

    private boolean dateInputValidation(DatePicker startDatePicker, DatePicker endDatePicker) {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        LocalDate now = LocalDate.now();

        // Validation process
        if (startDate == null || endDate == null) {
            showAlert("Date inputs cannot be empty.");
            return false;
        } else if (startDate.isAfter(now) || endDate.isAfter(now)) {
            showAlert("Dates cannot be in the future.");
            return false;
        } else if (startDate.isAfter(endDate)) {
            showAlert("Start date cannot be after end date.");
            return false;
        }
        return true;    // Validation passed
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
