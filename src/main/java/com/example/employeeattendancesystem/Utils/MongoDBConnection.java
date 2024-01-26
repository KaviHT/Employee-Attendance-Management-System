package com.example.employeeattendancesystem.Utils;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;


public class MongoDBConnection {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static MongoClient mongoClient;

    public static MongoDatabase getDatabase(String attendence_db) {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(CONNECTION_STRING);
        }
        return mongoClient.getDatabase(attendence_db);
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
