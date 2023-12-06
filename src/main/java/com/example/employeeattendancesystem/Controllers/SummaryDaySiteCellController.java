package com.example.employeeattendancesystem.Controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
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

import java.io.IOException;

public class SummaryDaySiteCellController {
    public ListView<AnchorPane> siteEmployeeList;
    public Label siteNameLbl, siteNumberLbl;
    public Button editBtn;

    public void initialize() throws IOException {
        // Create a list of items to populate the ListView

        var items = FXCollections.<AnchorPane>observableArrayList();

        // Sample data to work with
        for (int i=0; i<4; i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Cells/SummaryDayEmployeeCell.fxml"));
            Parent cell = loader.load();

            SummaryDayEmployeeCellController employeeCellController = loader.getController();

            employeeCellController.employeeNameLbl.setText("Employee Name " + (i + 1));

            Tooltip tooltip = new Tooltip("Example note");
            Tooltip.install(employeeCellController.noteLbl, tooltip);

            items.add((AnchorPane) cell);
        }

        siteEmployeeList.setItems(items);

        // Calculate and set the preferred height of the ListView
        Platform.runLater(() -> {
            // double itemHeight = siteEmployeeList.getFixedCellSize();

            // implement auto adjusting list view size according to number of employees

            siteEmployeeList.setPrefHeight(200 + 3);
        });

    }

    public void goToEditAttendance(ActionEvent event) throws IOException {

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
