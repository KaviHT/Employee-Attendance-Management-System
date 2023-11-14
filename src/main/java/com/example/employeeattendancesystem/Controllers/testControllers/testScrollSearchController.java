package com.example.employeeattendancesystem.Controllers.testControllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class testScrollSearchController {
    public ListView<AnchorPane> listView;
    public Button addBtn;
    public TextField searchField;

    public void initialize() throws IOException {
        // Create a list of items to populate the ListView
        var items = FXCollections.<AnchorPane>observableArrayList();

        for (int i=0; i<100; i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/test/testCell.fxml"));
            Parent cell = loader.load();

            testCell cellController = loader.getController();

            cellController.name.setText(String.valueOf(i));
            cellController.gender.setText(i + "p");

            //listView.getItems().add((AnchorPane) cell);

            items.add((AnchorPane) cell);
        }

        // Set the items in the ListView
        listView.setItems(items);

        // Add a listener to the searchField to handle auto-scrolling
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                // Iterate through the items to find a match
                for (int i = 0; i < items.size(); i++) {
                    //String labelText = items.get(i).lookup("#name").getAccessibleText();
                    String labelText =((Label) items.get(i).lookup("#name")).getText().toLowerCase(); // Correct way to get the text
                    String searchText = newValue.toLowerCase();
                    if (labelText.contains(newValue)) {
                        // Scroll to the matched item
                        listView.scrollTo(i);
                        break; // Stop searching after the first match
                    }
                }
            }
        });

    }

    public void add() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/test/testCell.fxml"));
        Parent cell = loader.load();

        testCell cellController = loader.getController();

        listView.getItems().add((AnchorPane) cell);

    }

    public void searchCells(ActionEvent event) {
    }

}
