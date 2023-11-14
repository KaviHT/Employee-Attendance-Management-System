package com.example.employeeattendancesystem.Utils;

public class Employee {

    private String employeeNumber;
    private String firstName;
    private String lastName;
    private String address;
    private String gender;
    private String dateOfBirth;
    private String contactNumber;

    public Employee(String employeeNumber, String firstName, String lastName, String address, String gender, String dateOfBirth, String contactNumber) {
        this.employeeNumber = employeeNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.contactNumber = contactNumber;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
