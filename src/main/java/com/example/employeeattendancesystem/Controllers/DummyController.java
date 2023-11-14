package com.example.employeeattendancesystem.Controllers;

import java.time.LocalDate;

public class DummyController {

    private static String siteName;
    private static LocalDate selectedDate;
    private static boolean editStatus;

    public static String getSiteName() {
        return siteName;
    }

    public static void setSiteName(String name) {
        siteName = name;
    }

    public static LocalDate getSelectedDate() {
        return selectedDate;
    }

    public static void setSelectedDate(LocalDate date) {
        selectedDate = date;
    }

    public static void setEditStatus(boolean status) {
        editStatus = status;
    }

    public static boolean getEditStatus() {
        return editStatus;
    }

}
