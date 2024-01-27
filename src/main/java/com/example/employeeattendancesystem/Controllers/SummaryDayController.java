package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.MongoDBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import org.bson.Document;

import java.io.IOException;
import java.time.LocalDate;

public class SummaryDayController {
    public Button previousDayBtn, nextDayBtn, todayBtn;
    public Label monthLbl, dateLbl, displayDateLbl, noRecordsMsgLbl;
    public ListView<AnchorPane> daySummaryList;
    LocalDate dateFocus;
    MongoDatabase database = MongoDBConnection.getDatabase("attendence_db");
    MongoCollection<Document> SiteCollection = database.getCollection("site");

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

        MongoCursor<Document> cursor = SiteCollection.find().iterator();

        int i = 0;
        try {
            while (cursor.hasNext()) {
                Document siteDocument = cursor.next();

                String siteDetails = siteDocument.getString("site_details");

                DummyController.setSiteName(siteDetails);
                System.out.println(siteDetails);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Cells/SummaryDaySiteCell.fxml"));
                Parent cell = loader.load();

                SummaryDaySiteCellController siteCellController = loader.getController();

                // Get site details from the document
                //String siteDetails = siteDocument.getString("site_details");

                siteCellController.siteNameLbl.setText(siteDetails);

                items.add((AnchorPane) cell);
                i++;
            }
        } finally {
            cursor.close();

            // Set the items in the ListView
            daySummaryList.setItems(items);
        }
    }
}
