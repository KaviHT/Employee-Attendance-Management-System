package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class SummaryController {

    public Label yearLbl, monthLbl;
    public Button previousMonthBtn, nextMonthBtn, todayBtn;
    public FlowPane calendarFlowPane;
    public TextField siteSearchField;
    public ListView<String> siteSuggestionList;
    ZonedDateTime dateFocus;
    ZonedDateTime today;
    private final ObservableList<String> suggestions = FXCollections.observableArrayList();
    public Button button = new Button();


    public void initialize() {
        // Setting calendar properties
        dateFocus = ZonedDateTime.now();
        today = ZonedDateTime.now();
        drawCalendar();

        // Sample data to work with - REMOVE in final
        // Populating suggestions data from the database
        Database database = new Database();
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
        // ----- call the switch method inside this method -----
        // ----- make a way to hide the list view when you don't want to search anything -----
        siteSuggestionList.setOnMouseClicked(event -> {
            String selectedItem = siteSuggestionList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {

                try {
                    DummyController.setSiteName(selectedItem);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/ViewFactory/SummarySiteView.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setTitle("Summary");
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException ignored) {}
            }
        });
    }

    public void previousMonth() {
        dateFocus = dateFocus.minusMonths(1);
        calendarFlowPane.getChildren().clear();
        drawCalendar();
    }

    public void nextMonth() {
        dateFocus = dateFocus.plusMonths(1);
        calendarFlowPane.getChildren().clear();
        drawCalendar();
    }

    public void currentMonth() {
        dateFocus = ZonedDateTime.now();
        calendarFlowPane.getChildren().clear();
        drawCalendar();
    }

    public void drawCalendar() {
        yearLbl.setText(String.valueOf(dateFocus.getYear()));
        monthLbl.setText(String.valueOf(dateFocus.getMonth()));

        calendarFlowPane.setAlignment(Pos.CENTER);

        double calendarWidth = calendarFlowPane.getPrefWidth();
        double calendarHeight = calendarFlowPane.getPrefHeight();
        double strokeWidth = 1;
        double spacingH = calendarFlowPane.getHgap();
        double spacingV = calendarFlowPane.getVgap();

        // Getting last day of the month
        int monthMaxDate = dateFocus.getMonth().maxLength();

        // Checking for a leap year
        if (dateFocus.getYear() % 4 != 0 && monthMaxDate == 29) {
            monthMaxDate = 28;
        }

        int dateOffset = ZonedDateTime.of(dateFocus.getYear(), dateFocus.getMonthValue(), 1,0,0,0,0,dateFocus.getZone()).getDayOfWeek().getValue();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {

                StackPane stackPane = new StackPane();
                Rectangle rectangle = new Rectangle();

                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.TRANSPARENT);
                rectangle.setStrokeWidth(strokeWidth);

                double rectangleWidth = (calendarWidth/7) - strokeWidth*7 - spacingH*7;
                rectangle.setWidth(rectangleWidth);
                double rectangleHeight = (calendarHeight/6) - strokeWidth*6 - spacingV*6;
                rectangle.setHeight(rectangleHeight);

                stackPane.getChildren().add(rectangle);

                int calculatedDate = (j+1)+(7*i);

                if (calculatedDate > dateOffset) {

                    int currentDate = calculatedDate - dateOffset;

                    if (currentDate <= monthMaxDate) {
                        Text date = new Text(String.valueOf(currentDate));
                        button = new Button(date.getText());
                        button.setPrefWidth(rectangleWidth);
                        button.setPrefHeight(rectangleHeight);
                        stackPane.getChildren().add(button);


                        LocalDate buttonDate = LocalDate.of(
                                dateFocus.getYear(),
                                dateFocus.getMonthValue(),
                                currentDate);

                        button.setOnAction(event -> switchToSummaryDay(event, buttonDate));
                    }

                    if (today.getYear() == dateFocus.getYear() && today.getMonth() == dateFocus.getMonth() && today.getDayOfMonth() == currentDate) {
                        button.setBackground(Background.fill(Color.GRAY));
                    }
                }
                calendarFlowPane.getChildren().add(stackPane);
            }
        }
    }

    public void switchToSummaryDay(ActionEvent event, LocalDate selectedDate) {

        DummyController.setSelectedDate(selectedDate);

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Fxml/ViewFactory/SummaryDayView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Summary");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ignored) {}
    }
}
