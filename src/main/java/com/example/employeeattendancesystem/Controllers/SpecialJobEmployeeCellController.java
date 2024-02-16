package com.example.employeeattendancesystem.Controllers;

import com.example.employeeattendancesystem.Utils.MongoDBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.util.Optional;

public class SpecialJobEmployeeCellController {
    public Button deleteBtn;
    public Label employeeNumLbl, employeeNameLbl, siteNameLbl, dateLbl, inTimeLbl, outTimeLbl;

    MongoDatabase database = MongoDBConnection.getDatabase("attendence_db");
    MongoCollection<Document> DelSpecialJobsCollection = database.getCollection("special_jobs");

    public void deleteRecord(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Record");
        alert.setContentText("Do you want to delete the special job attendance record?");

        ButtonType okButton = new ButtonType("OK");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();

        // Handle the user's response
        if (result.isPresent() && result.get() == okButton) {
            // User clicked OK

            // Create a filter to match the document
            Bson filter = Filters.and(
                    Filters.eq("siteName", siteNameLbl.getText()),
                    Filters.eq("employeeName", employeeNameLbl.getText()),
                    Filters.eq("date", dateLbl.getText())
            );

            // Delete the document from the collection
            DelSpecialJobsCollection.deleteOne(filter);

            reloadWindow(event);

        } else {
            // User clicked Cancel or closed the alert
        }
    }

    public void reloadWindow(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/ViewFactory/SpecialJobsView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Special Jobs");
        stage.setScene(scene);
        stage.show();
    }
}
