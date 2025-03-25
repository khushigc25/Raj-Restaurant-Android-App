package com.example.rajrestaurant.models;

// DeliveryPerson.java
public class DeliveryPerson {
    private String id; // Unique ID of the delivery person
    private String name; // Name of the delivery person

    public DeliveryPerson() {
        // Firestore needs a public no-argument constructor
    }

    public DeliveryPerson(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
