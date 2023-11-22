package com.example.lorav4;

public class Order {
    private String orderId;
    private String orderNumber;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String address;

    // Default constructor for Firebase
    public Order() {
        // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    }

    public Order(String orderId, String orderNumber, String firstName, String lastName, String mobileNumber, String address) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.address = address;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
