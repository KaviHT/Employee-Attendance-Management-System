package com.example.employeeattendancesystem.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.time.LocalDate;

public class SummarySiteController {
    public Label siteNameLbl, yearLbl, monthLbl, noRecordsMsgLbl;
    public ListView<AnchorPane> employeeList;
    public Button previousMonthBtn, nextMonthBtn, thisMonthBtn;
    LocalDate dateFocus;


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
        yearLbl.setText(String.valueOf(dateFocus.getYear()));
        monthLbl.setText(String.valueOf(dateFocus.getMonth()));

        // Create a list of items to populate the ListView
        var items = FXCollections.<AnchorPane>observableArrayList();

        // Getting last day of the month
        int monthMaxDate = dateFocus.getMonth().maxLength();

        // Checking for a leap year
        if (dateFocus.getYear() % 4 != 0 && monthMaxDate == 29) {
            monthMaxDate = 28;
        }

        // Sample data to work with

        // Implement the way to get number of employees assigned to the related site

        // Replace "3" with the number of employees assigned to the related site
        for (int i=0; i<3; i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Cells/SummarySiteEmployeeCell.fxml"));
            Parent cell = loader.load();

            SummarySiteEmployeeCellController employeeCellController = loader.getController();

            employeeCellController.employeeNameLbl.setText("Employee " + (i + 1));
            employeeCellController.employeeNumberLbl.setText(String.valueOf(i + 1));

            double slotWidth = employeeCellController.statusSummaryHbox.getPrefWidth() / monthMaxDate;

            for (int j=0; j<monthMaxDate; j++) {
                Label dayStatusLbl = new Label();

                dayStatusLbl.setPrefWidth(slotWidth);

                // Implement getting employee's attendance status here

                dayStatusLbl.setText(String.valueOf(j + 1));    // <--- Add it here

                // Here is an example of displaying the note
                // Make sure to add an if and get the note from database
                if (dayStatusLbl.getText().equals("12")) {
                    Tooltip tooltip = new Tooltip("Here is an example of a Note");
                    Tooltip.install(dayStatusLbl, tooltip);
                    dayStatusLbl.setTextFill(Color.RED);
                }

                employeeCellController.statusSummaryHbox.getChildren().add(dayStatusLbl);
            }

            items.add((AnchorPane) cell);
        }

        // Set the items in the ListView
        employeeList.setItems(items);

    }

}
