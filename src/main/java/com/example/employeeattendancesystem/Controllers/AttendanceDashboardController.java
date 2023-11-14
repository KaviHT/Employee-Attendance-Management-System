package com.example.employeeattendancesystem.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AttendanceDashboardController {
    public Label presentCountLbl, leaveCountLbl, halfDayCountLbl;
    public TextField siteSearchField;
    public ListView<AnchorPane> allSitesListView;

    public void initialize() throws IOException {
        // Create a list of items to populate the ListView
        var items = FXCollections.<AnchorPane>observableArrayList();

        // Sample data to work with
        for (int i=0; i<60; i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Cells/AttendanceDashboardSiteCell.fxml"));
            Parent cell = loader.load();

            AttendanceDashboardSiteCellController siteCellController = loader.getController();

            siteCellController.siteNumberLbl.setText(String.valueOf(i+1));
            siteCellController.siteNameLbl.setText("Site Name");

            items.add((AnchorPane) cell);
        }

        // Set the items in the ListView
        allSitesListView.setItems(items);
        // Add a listener to the searchField to handle auto-scrolling
        siteSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                // Iterate through the items to find a match
                for (int i = 0; i < items.size(); i++) {
                    //String labelText = items.get(i).lookup("#name").getAccessibleText();
                    String labelText =((Label) items.get(i).lookup("#siteNumberLbl")).getText().toLowerCase();
                    String searchText = newValue.toLowerCase();
                    if (labelText.contains(newValue)) {
                        // Scroll to the matched item
                        allSitesListView.scrollTo(i);
                        break; // Stop searching after the first match
                    }
                }
            }
        });

    }

}
