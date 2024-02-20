package com.example.employeeattendancesystem.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    public TextField usernameFld;
    public PasswordField passwordFld;
    public Button loginBtn;
    public Label errorLbl;

    public void switchToDashboard(ActionEvent event) throws IOException {
        // Account validation
        if (!usernameFld.getText().equals("admin") || !passwordFld.getText().equals("admin")) {
            errorLbl.setVisible(true);
            errorLbl.setText("Wrong Username or Password");
        }
        else {
            errorLbl.setVisible(false);
            Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }
}
