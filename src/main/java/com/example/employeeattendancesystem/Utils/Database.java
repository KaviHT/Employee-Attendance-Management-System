package com.example.employeeattendancesystem.Utils;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Database {

    private ArrayList<Employee> employees;

    public Database() {
        this.employees = new ArrayList<>();
    }



    MongoDatabase database = MongoDBConnection.getDatabase("attendence_db");
    MongoCollection<Document> EmployeeCollection = database.getCollection("employee");

    MongoCollection<Document> SiteCollection = database.getCollection("site");


    public List<String> getEmployeeSearchDetails() {
        List<String> employeeDetails = new ArrayList<>();

        FindIterable<Document> documents = EmployeeCollection.find();
        for (Document document : documents) {
            String empId = document.getString("emp_id");
            String firstName = document.getString("first_name");
            String lastName = document.getString("last_name");


            employeeDetails.add(empId + " " + firstName + " " + lastName);

        }

        return employeeDetails;
    }


    public List<String> getSiteSearchDetails() {

        List<String> siteDetails = new ArrayList<>();

        FindIterable<Document> documents = SiteCollection.find();
        for (Document document : documents) {
            String siteId = document.getString("site_id");
            String siteName = document.getString("site_name");


            siteDetails.add(siteId + " " + siteName);

        }

        return siteDetails;


    }



}
