package com.example.rajrestaurant.models;

import java.util.List;
import java.util.Map;

public class Order {
    private String orderId;
    private List<Map<String, Object>> items; // Change this based on your actual data structure
    private double totalAmount;
    private String address;
    private String orderDate;
    private String orderTime;

    private String userName;
    private String userMobile;

    private String orderTimestamp;

    // No-argument constructor
    public Order() {
        // This constructor is needed for Firestore to deserialize the object
    }

    public Order(String orderId, List<Map<String, Object>> items, double totalAmount, String address, String orderDate, String orderTime, String userName, String userMobile, String orderTimestamp) {
        this.orderId = orderId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.address = address;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.userName = userName;
        this.userMobile = userMobile;
        this.orderTimestamp = orderTimestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getAddress() {
        return address;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }
    public String getOrderTimestamp() {
        return orderTimestamp;
    }

    public void setOrderTimestamp(String orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

}
