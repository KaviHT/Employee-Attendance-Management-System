package com.example.employeeattendancesystem.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;

public class SummaryDayController {
    public Button previousDayBtn, nextDayBtn, todayBtn;
    public Label monthLbl, dateLbl, displayDateLbl, noRecordsMsgLbl;
    public ListView<AnchorPane> daySummaryList;
    LocalDate dateFocus;

    public void initialize() throws IOException {
        // Getting the selected date from DummyController
        dateFocus = DummyController.getSelectedDate();
        checkRecords();
    }

    public void previousDay() throws IOException {
        dateFocus = dateFocus.minusDays(1);
        checkRecords();
    }

    public void nextDay() throws IOException {
        dateFocus = dateFocus.plusDays(1);
        checkRecords();
    }

    public void currentDay() throws IOException {
        dateFocus = LocalDate.now();
        checkRecords();
    }

    public void checkRecords() throws IOException {
        DummyController.setSelectedDate(dateFocus);
        if (LocalDate.now().isBefore(dateFocus)) {
            noRecordsMsgLbl.setVisible(true);
            daySummaryList.getItems().clear();
            monthLbl.setText(String.valueOf(dateFocus.getMonth()));
            dateLbl.setText(String.valueOf(dateFocus.getDayOfMonth()));
        } else {
            noRecordsMsgLbl.setVisible(false);
            drawDaySummary();
        }
    }

    public void drawDaySummary() throws IOException {
        monthLbl.setText(String.valueOf(dateFocus.getMonth()));
        dateLbl.setText(String.valueOf(dateFocus.getDayOfMonth()));

        // Create a list of items to populate the ListView
        var items = FXCollections.<AnchorPane>observableArrayList();

        // Sample data to work with
        for (int i=0; i<5; i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Cells/SummaryDaySiteCell.fxml"));
            Parent cell = loader.load();

            SummaryDaySiteCellController siteCellController = loader.getController();

            siteCellController.siteNumberLbl.setText(String.valueOf(i+1));
            siteCellController.siteNameLbl.setText("Site Name" + (i + 1));

            items.add((AnchorPane) cell);
        }

        // Set the items in the ListView
        daySummaryList.setItems(items);
    }

}
