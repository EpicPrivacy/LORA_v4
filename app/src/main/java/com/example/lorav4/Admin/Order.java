package com.example.lorav4.Admin;

public class Order {
    private String orderId;
    private String userId;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String address;

    // Default constructor required for Firebase
    public Order() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Parameterized constructor
    public Order(String orderId, String userId, String firstName, String lastName, String mobileNumber, String address) {
        this.orderId = orderId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.address = address;
    }

    // Getters and setters...
}
