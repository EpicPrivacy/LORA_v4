package com.example.lorav4;

public class Order {
    private String orderId;
    private String userId;
    private String firstName;
    private String lastName;

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

    private String mobileNumber;
    private String address;

    // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    public Order() {
    }

    // Constructor
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
