package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.Database;
import com.example.employeeattendancesystem.Utils.MongoDBConnection;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsController {
    public DatePicker atrStartDate, atrEndDate, sjrStartDate, sjrEndDate;
    public Button atrConvertBtn, sjrConvertBtn;
    public ListView<String> siteSuggestionList;
    public TextField siteSearchField;
    private final ObservableList<String> suggestions = FXCollections.observableArrayList();
    private String selectedItem;

    MongoDBConnection mongoDBConnection = new MongoDBConnection();

    MongoDatabase Database = mongoDBConnection.getDatabase("attendence_db");
    MongoCollection<Document> PrintEmpCollection = Database.getCollection("EmployeeAttendance");
    MongoCollection<Document> SpJobPrintCollection = Database.getCollection("special_jobs");


    public void initialize() {

        Database database = new Database();

        // Add the "All" to the site options
        suggestions.add("All");

        // Populating suggestions data from the database
        suggestions.addAll(database.getSiteSearchDetails());

        // Set the list to the ListView and select "All" by default
        // siteSuggestionList.getSelectionModel().select("All");
        selectedItem = "All";

        // Update the search field to show the default selected item
        siteSearchField.setText(selectedItem);

        // Autocomplete functionality
        siteSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Clear previous suggestions
            siteSuggestionList.getItems().clear();

            // Filter and add matching suggestions
            String searchText = newValue.toLowerCase().trim();
            for (String item : suggestions) {
                if (item.toLowerCase().contains(searchText)) {
                    siteSuggestionList.getItems().add(item);
                }
            }

            // Show or hide the suggestion list based on whether there are suggestions
            siteSuggestionList.setVisible(!siteSuggestionList.getItems().isEmpty());
        });

        // Handle item selection from the suggestion list
        // ----- call the searchSite method inside this method -----
        // ----- make a way to hide the list view when you don't want to search anything -----
        siteSuggestionList.setOnMouseClicked(event -> {
            selectedItem = siteSuggestionList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                siteSearchField.setText(selectedItem);
                siteSuggestionList.setVisible(false);
                SitesCUController siteCU = new SitesCUController();
                siteCU.receiveSelectedItem(selectedItem);
            }
        });
    }

    public void atrConvertCSV(ActionEvent event) {
        if (dateInputValidation(atrStartDate, atrEndDate)) {
            String startDate = atrStartDate.getValue().toString();
            String endDate = atrEndDate.getValue().toString();


            FindIterable<Document> documents;

            // If selectedItem is not "All", filter the documents where site equals to selectedItem
            if (!selectedItem.equals("All")) {
                documents = PrintEmpCollection.find(Filters.eq("Site", selectedItem));
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
            filePathSelection(startDate, endDate, "Attendance Report", "Attendance_Report_" + selectedItem.replace(" ", "-") + "_", workbook);
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
