package com.example.rajrestaurant.models;

public class DeliveryPersonModel {
    private String name;
    private String email;
    private String phone;
    private String age;

    // Default constructor required for Firestore
    public DeliveryPersonModel() {}

    public DeliveryPersonModel(String name, String email, String phone, String age) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAge() {
        return age;
    }
}
