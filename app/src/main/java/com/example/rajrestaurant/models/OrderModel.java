package com.example.rajrestaurant.models;

import java.util.List;
import java.util.Map;

public class OrderModel {
    private double totalAmount;
    private String orderDate;
    private String orderTime;
    private List<Map<String, Object>> items;

    // No-argument constructor required for Firestore
    public OrderModel() {
    }

    // Parameterized constructor
    public OrderModel(double totalAmount, String orderDate, String orderTime, List<Map<String, Object>> items) {
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }
}
