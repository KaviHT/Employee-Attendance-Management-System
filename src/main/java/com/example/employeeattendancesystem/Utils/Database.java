package com.example.employeeattendancesystem.Utils;

import java.util.ArrayList;

public class Database {

    private ArrayList<Employee> employees;

    public Database() {
        this.employees = new ArrayList<>();
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public void addEmployee(Employee employee) {
        this.employees.add(employee);
    }

    public ArrayList<String> getEmployeeDetails() {
        ArrayList<String> employeeDetails = new ArrayList<>();
        for (Employee employee : employees) {
            String details = employee.getEmployeeNumber() + " " + employee.getFirstName() + " " + employee.getLastName();
            employeeDetails.add(details);
        }
        return employeeDetails;
    }

}
