module com.example.employeeattendancesystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.employeeattendancesystem to javafx.fxml;
    exports com.example.employeeattendancesystem;
    exports com.example.employeeattendancesystem.Controllers;
    opens com.example.employeeattendancesystem.Controllers to javafx.fxml;
    exports com.example.employeeattendancesystem.Controllers.testControllers;
    opens com.example.employeeattendancesystem.Controllers.testControllers to javafx.fxml;
    exports com.example.employeeattendancesystem.Utils;
    opens com.example.employeeattendancesystem.Utils to javafx.fxml;
}