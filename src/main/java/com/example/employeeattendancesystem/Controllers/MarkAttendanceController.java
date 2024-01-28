package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.Database;
import com.example.employeeattendancesystem.Utils.MongoDBConnection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import org.bson.Document;
import com.mongodb.client.MongoCollection;

import javafx.scene.paint.Color;


public class MarkAttendanceController {
    public TextField siteSearchField;
    public Button markHolidayBtn, headOfficeBtn;
    public ListView<String> siteSuggestionList;
    public ListView<AnchorPane> supervisorList;
    private final ObservableList<String> suggestions = FXCollections.observableArrayList();
    public DatePicker holidayDate;


    public void initialize() throws IOException {

        Database database = new Database();
        // Populating suggestions data from the database
        suggestions.addAll(database.getSiteSearchDetails());  // <--- add the database here

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
            String selectedItem = siteSuggestionList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {

                try {
                    DummyController.setSiteName(selectedItem);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/ViewFactory/MarkAttendanceSiteView.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setTitle("Mark Attendance");
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException ignored) {}

                siteSearchField.setText(selectedItem);
                siteSuggestionList.setVisible(false);
            }
        });

        showAllSites();
    }

    public void showAllSites() throws IOException {

        MongoDBConnection mongoDBConnection = new MongoDBConnection();
        MongoDatabase Database = mongoDBConnection.getDatabase("attendence_db");
        MongoCollection<Document> supCollection = Database.getCollection("siteSupervisor");

        var items = FXCollections.<AnchorPane>observableArrayList();

        // ...

        for (Document doc : supCollection.find()) {
            String supName = (String) doc.get("supName");
            List<String> sites = (List<String>) doc.get("sites");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Cells/MarkAttendanceSupervisorCell.fxml"));
            Parent cell = loader.load();

            MarkAttendanceSupervisorCellController cellController = loader.getController();

            cellController.supervisorNameLbl.setText(supName);


            String[] buttonColors = {"#0082e6", "#0091ff", "#1a9cff", "#33a7ff", "#4db2ff", "#66bdff", "#80c8ff", "#99d3ff"};

            for (int i = 0; i < sites.size(); i++) {
                String site = sites.get(i);
                Button button = new Button(site);

                // Set background color based on the index
                int colorIndex = i % buttonColors.length;
                Color color = Color.web(buttonColors[colorIndex]);
                String hexColor = String.format("#%02X%02X%02X",
                        (int) (color.getRed() * 255),
                        (int) (color.getGreen() * 255),
                        (int) (color.getBlue() * 255));

                button.setStyle("-fx-background-color: " + hexColor + ";");

                button.setOnAction(event -> switchToMarkAttendanceSite(event, site));

                // Set the cursor to hand when hovering over the button
                button.setOnMouseEntered(e -> button.getScene().setCursor(Cursor.HAND));
                button.setOnMouseExited(e -> button.getScene().setCursor(Cursor.DEFAULT));

                cellController.supervisorSitesList.getChildren().add(button);
            }


            items.add((AnchorPane) cell);
        }

// ...

        supervisorList.setItems(items);
    }

    private void switchToMarkAttendanceSite(ActionEvent event, String siteName) {

        DummyController.setSelectedDate(LocalDate.now());
        DummyController.setEditStatus(false);

        try {
            DummyController.setSiteName(siteName);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/ViewFactory/MarkAttendanceSiteView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Mark Attendance");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ignored) {}
    }

    public void switchToHeadOfficeSite(ActionEvent event) {

        switchToMarkAttendanceSite(event, "00 Head Office");

    }

    public void markAsHoliday(ActionEvent event) throws IOException {
        String holiday = holidayDate.getValue().toString();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Mark Holiday");
        alert.setContentText("Do you want to mark today: " + holiday + " as a holiday?");

        // gedara iddith attendence system ekata gihilla mark karanna oneda holidays

        ButtonType okButton = new ButtonType("OK");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();

        // Handle the user's response
        if (result.isPresent() && result.get() == okButton) {
            // User clicked OK

            /*
            Implement marking as holiday in database
             */

            switchToAttendanceDashboard(event);

        } else {
            // User clicked Cancel or closed the alert
        }
    }

    public void switchToAttendanceDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/ViewFactory/AttendanceDashboardView.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Attendance Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
